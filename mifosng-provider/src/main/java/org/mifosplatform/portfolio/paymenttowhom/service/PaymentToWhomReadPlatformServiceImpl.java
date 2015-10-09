/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.paymenttowhom.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.paymenttowhom.data.PaymentToWhomData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class PaymentToWhomReadPlatformServiceImpl implements PaymentToWhomReadPaltformService {
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public PaymentToWhomReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource){
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public Collection<PaymentToWhomData> retrieveAllPaymentToWhom(){
		this.context.authenticatedUser();
		
		final PaymentToWhomMapper ptm = new PaymentToWhomMapper();
		final String sql = "select " + ptm.schema() + "order by position";
		return this.jdbcTemplate.query(sql, ptm, new Object [] {});
		
	}
	@Override
	public PaymentToWhomData retrieveOne(Long paymentToWhomId){
		this.context.authenticatedUser();
		
		final PaymentToWhomMapper ptm = new PaymentToWhomMapper();
		final String sql = "select " + ptm.schema() + "where pm.id = ?";
		
		return this.jdbcTemplate.queryForObject(sql, ptm, new Object[] { paymentToWhomId });
	}
	
	private static final class PaymentToWhomMapper implements RowMapper<PaymentToWhomData>{
		
		 public String schema() {
	            return " pm.id as id, pm.value as name, pm.description as description,pm.is_cash_payment as isCashPayment,pm.order_position as position from m_payment_to_whom pm ";
	        }
		@Override
		public PaymentToWhomData mapRow(final ResultSet rs,@SuppressWarnings("unused") final int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String description = rs.getString("description");
            final boolean isCashPayment = rs.getBoolean("isCashPayment");
            final Long position = rs.getLong("position");
            
            return PaymentToWhomData.instance(id, name,description,isCashPayment,position);
			
		}
	}
}
