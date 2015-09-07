/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.StopWatch;
import org.mifosplatform.infrastructure.cache.domain.CacheType;
import org.mifosplatform.infrastructure.cache.service.CacheWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationDomainService;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.security.data.PlatformRequestLog;
import org.mifosplatform.infrastructure.security.exception.InvalidTenantIdentiferException;
import org.mifosplatform.infrastructure.security.service.BasicAuthTenantDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

/**
 * 
 * This filter is responsible for extracting multi-tenant from the request and
 * setting Cross-Origin details to response. 
 * 
 * If multi-tenant are valid, the details of the tenant are stored in
 * {@link MifosPlatformTenant} and stored in a {@link ThreadLocal} variable for
 * this request using {@link ThreadLocalContextUtil}.
 * 
 * If multi-tenant are invalid, a http error response is returned.
 * 
 * Used to support Oauth2 authentication and the service is loaded only when "oauth" profile is active.
 */
@Service(value = "tenantIdentifierProcessingFilter")
@Profile("oauth")
public class TenantAwareTenantIdentifierFilter extends GenericFilterBean {

    private static boolean firstRequestProcessed = false;
    private final static Logger logger = LoggerFactory.getLogger(TenantAwareTenantIdentifierFilter.class);

    private final BasicAuthTenantDetailsService basicAuthTenantDetailsService;
    private final ToApiJsonSerializer<PlatformRequestLog> toApiJsonSerializer;
    private final ConfigurationDomainService configurationDomainService;
    private final CacheWritePlatformService cacheWritePlatformService;

    private final String tenantRequestHeader = "X-Mifos-Platform-TenantId";
    private final boolean exceptionIfHeaderMissing = true;
    private final String apiUri = "/api/v1/";

    @Autowired
    public TenantAwareTenantIdentifierFilter(final BasicAuthTenantDetailsService basicAuthTenantDetailsService,
            final ToApiJsonSerializer<PlatformRequestLog> toApiJsonSerializer, final ConfigurationDomainService configurationDomainService,
            final CacheWritePlatformService cacheWritePlatformService) {
        this.basicAuthTenantDetailsService = basicAuthTenantDetailsService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.configurationDomainService = configurationDomainService;
        this.cacheWritePlatformService = cacheWritePlatformService;
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        final StopWatch task = new StopWatch();
        task.start();

        try {

            // allows for Cross-Origin
            // Requests (CORs) to be performed against the platform API.
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            final String reqHead = request.getHeader("Access-Control-Request-Headers");

            if (null != reqHead && !reqHead.equals(null)) {
                response.setHeader("Access-Control-Allow-Headers", reqHead);
            }

            if (!"OPTIONS".equalsIgnoreCase(request.getMethod())) {

                String tenantIdentifier = request.getHeader(this.tenantRequestHeader);
                if (org.apache.commons.lang.StringUtils.isBlank(tenantIdentifier)) {
                    tenantIdentifier = request.getParameter("tenantIdentifier");
                }

                if (tenantIdentifier == null && this.exceptionIfHeaderMissing) { throw new InvalidTenantIdentiferException(
                        "No tenant identifier found: Add request header of '" + this.tenantRequestHeader
                                + "' or add the parameter 'tenantIdentifier' to query string of request URL."); }

                String pathInfo = request.getRequestURI();
                boolean isReportRequest = false;
                if (pathInfo != null && pathInfo.contains("report")) {
                    isReportRequest = true;
                }
                final MifosPlatformTenant tenant = this.basicAuthTenantDetailsService.loadTenantById(tenantIdentifier, isReportRequest);

                ThreadLocalContextUtil.setTenant(tenant);
                String authToken = request.getHeader("Authorization");

                if (authToken != null && authToken.startsWith("bearer ")) {
                    ThreadLocalContextUtil.setAuthToken(authToken.replaceFirst("bearer ", ""));
                }

                if (!firstRequestProcessed) {
                    final String baseUrl = request.getRequestURL().toString()
                            .replace(request.getRequestURI(), request.getContextPath() + apiUri);
                    System.setProperty("baseUrl", baseUrl);

                    final boolean ehcacheEnabled = this.configurationDomainService.isEhcacheEnabled();
                    if (ehcacheEnabled) {
                        this.cacheWritePlatformService.switchToCache(CacheType.SINGLE_NODE);
                    } else {
                        this.cacheWritePlatformService.switchToCache(CacheType.NO_CACHE);
                    }
                    TenantAwareTenantIdentifierFilter.firstRequestProcessed = true;
                }
                chain.doFilter(request, response);
            }
        } catch (final InvalidTenantIdentiferException e) {
            // deal with exception at low level
            SecurityContextHolder.getContext().setAuthentication(null);

            response.addHeader("WWW-Authenticate", "Basic realm=\"" + "Mifos Platform API" + "\"");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } finally {
            task.stop();
            final PlatformRequestLog log = PlatformRequestLog.from(task, request);
            logger.info(this.toApiJsonSerializer.serialize(log));
        }

    }
}