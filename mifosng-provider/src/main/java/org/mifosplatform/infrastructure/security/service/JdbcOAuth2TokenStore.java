/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.security.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

@Service(value = "tokenStore")
public class JdbcOAuth2TokenStore implements OAuth2TokenStore {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformUserDetailsService userDetailsService;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();


    @Autowired
    public JdbcOAuth2TokenStore(final RoutingDataSource dataSource,
            final PlatformUserDetailsService userDetailsService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.userDetailsService = userDetailsService;
    }

    @Override
    public OAuth2Authentication readAuthentication(final OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(final String token) {
        final AuthenticationDataMapper rm = new AuthenticationDataMapper();
        String sql = "select " + rm.schema() + " where token like ?";
        return this.jdbcTemplate.queryForObject(sql, rm, new Object[]{token});
    }

    @Override
    public void storeAccessToken(final OAuth2AccessToken token, final OAuth2Authentication authentication) {

        final String sql = "insert into m_oauth_access_token(token, client_id, expiration_time, authentication_id) " +
                "values(?, ?, ?, ?)";

        if (readAccessToken(token.getValue()) != null) {
            removeAccessToken(token.getValue());
        }

        final String tokenValue = token.getValue();
        final String client = authentication.getOAuth2Request().getClientId();
        final Long expiration = token.getExpiration() != null ? token.getExpiration().getTime() : null;
        final String authentication_id = authenticationKeyGenerator.extractKey(authentication);

        jdbcTemplate.update(sql, new Object[] {tokenValue, client, expiration, authentication_id });

    }

    @Override
    public OAuth2AccessToken readAccessToken(final String tokenValue) {
        final AccessTokenDataMapper rm = new AccessTokenDataMapper();
        String sql = "select " + rm.schema() + " where token like ?";
        OAuth2AccessToken accessToken = null;
        try {
            accessToken = this.jdbcTemplate.queryForObject(sql, rm, new Object[]{ tokenValue });
        }
        catch (EmptyResultDataAccessException ignored) {
        }
        return accessToken;
    }

    @Override
    public void removeAccessToken(final OAuth2AccessToken token) {
        removeAccessToken(token.getValue());
    }

    private void removeAccessToken(final String token) {
        String sql = "delete from m_oauth_access_token where token like '" + token + "'";
        this.jdbcTemplate.update(sql);
    }

    @Override
    public void storeRefreshToken(final OAuth2RefreshToken refreshToken, final OAuth2Authentication authentication) {
        throw new UnsupportedOperationException("Refresh tokens are not supported.");
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(final String tokenValue) {
        throw new UnsupportedOperationException("Refresh tokens are not supported.");
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(final OAuth2RefreshToken token) {
        throw new UnsupportedOperationException("Refresh tokens are not supported.");
    }

    @Override
    public void removeRefreshToken(final OAuth2RefreshToken token) {
        throw new UnsupportedOperationException("Refresh tokens are not supported.");
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(final OAuth2RefreshToken refreshToken) {
        throw new UnsupportedOperationException("Refresh tokens are not supported.");
    }

    @Override
    public OAuth2AccessToken getAccessToken(final OAuth2Authentication authentication) {
        final String authenticationId = authenticationKeyGenerator.extractKey(authentication);
        final AccessTokenDataMapper rm = new AccessTokenDataMapper();
        String sql = "select " + rm.schema() + " where authentication_id like ?";
        OAuth2AccessToken accessToken = null;

        try {
            accessToken = this.jdbcTemplate.queryForObject(sql, rm, new Object[]{ authenticationId });
        } catch (EmptyResultDataAccessException ignored) {
        }

        return accessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(final String clientId,
            final String userName) {
        return findTokensByClientId(clientId);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(final String clientId) {
        return retiveTokensByClientId(clientId);
    }

    private Collection<OAuth2AccessToken> retiveTokensByClientId(final String clientId) {
        final AccessTokenDataMapper rm = new AccessTokenDataMapper();
        final String sql = "select " + rm.schema() + " where client_id like ?";
        List<OAuth2AccessToken> accessTokens = new ArrayList<>();

        try {
            accessTokens = this.jdbcTemplate.query(sql, rm, new Object[]{clientId});
        } catch (EmptyResultDataAccessException ignored) {
        }

        return accessTokens;
    }

    private final class AccessTokenDataMapper implements RowMapper<OAuth2AccessToken> {

        public String schema() {
            return "t.token as token, t.expiration_time as expiration from m_oauth_access_token t";
        }

        @Override
        public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long expirationTime = rs.getLong("expiration");
            final Date expiration = rs.wasNull() ? null : new Date(expirationTime);
            final Set<String> scope = Sets.newHashSet("all");
            final String tokenId = rs.getString("token");

            final DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(tokenId);

            token.setExpiration(expiration);
            token.setScope(scope);

            return token;
        }
    }

    private final class AuthenticationDataMapper implements RowMapper<OAuth2Authentication> {

        public String schema() {
            return "t.client_id as client, t.token as token, t.expiration_time as expiration, " +
                    "t.authentication_id as authentication from m_oauth_access_token t";
        }

        @Override
        public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {

            final Map<String, String> requestParameters = new HashMap<>();
            requestParameters.put("grant_type", "client_credentials");
            final String clientId = rs.getString("client");
            final Set<String> scope = Sets.newHashSet("all");
            OAuth2Authentication authentication = null;
            Authentication userAuthentication = null;
            OAuth2Request request;
            Collection<? extends GrantedAuthority> grantedAuthorities = new ArrayList<>();

            try {
                final UserDetails userDetails = userDetailsService.loadUserByUsername(clientId);

                userAuthentication = new UsernamePasswordAuthenticationToken(userDetails,
                        userDetails.getPassword(), userDetails.getAuthorities());
                grantedAuthorities = userDetails.getAuthorities();

            } catch (UsernameNotFoundException ignored) {
            }

            request = new OAuth2Request(requestParameters, clientId,
                    grantedAuthorities, true, scope, null, null, null, null);
            authentication = new OAuth2Authentication(request, userAuthentication);
            return authentication;
        }
    }
}
