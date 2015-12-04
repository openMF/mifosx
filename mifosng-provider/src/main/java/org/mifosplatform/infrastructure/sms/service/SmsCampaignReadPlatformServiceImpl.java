/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.sms.service;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.sms.data.PreviewCampaignMessage;
import org.mifosplatform.infrastructure.sms.data.SmsBusinessRulesData;
import org.mifosplatform.infrastructure.sms.data.SmsCampaignData;
import org.mifosplatform.infrastructure.sms.data.SmsCampaignTimeLine;
import org.mifosplatform.infrastructure.sms.domain.SmsCampaignStatus;
import org.mifosplatform.infrastructure.sms.domain.SmsCampaignStatusEnumerations;
import org.mifosplatform.infrastructure.sms.domain.SmsCampaignType;
import org.mifosplatform.infrastructure.sms.domain.SmsMessageEnumerations;
import org.mifosplatform.infrastructure.sms.exception.SmsBusinessRuleNotFound;
import org.mifosplatform.infrastructure.sms.exception.SmsCampaignNotFound;
import org.mifosplatform.infrastructure.sms.exception.SmsNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SmsCampaignReadPlatformServiceImpl implements SmsCampaignReadPlatformService{


    private final JdbcTemplate jdbcTemplate;

    private final BusinessRuleMapper businessRuleMapper;

    private final SmsCampaignMapper smsCampaignMapper;

    @Autowired
    public SmsCampaignReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.businessRuleMapper = new BusinessRuleMapper();
        this.smsCampaignMapper = new SmsCampaignMapper();
    }


    private static final class SmsCampaignMapper implements RowMapper<SmsCampaignData>{

        final String schema;

        private SmsCampaignMapper() {
            final StringBuilder sql = new StringBuilder(400);
            sql.append("sc.id as id, ");
            sql.append("sc.campaign_name as campaignName, ");
            sql.append("sc.campaign_type as campaignType, ");
            sql.append("sc.runReport_id as runReportId, ");
            sql.append("sc.message as message, ");
            sql.append("sc.param_value as paramValue, ");
            sql.append("sc.status_enum as status, ");
            sql.append("sc.recurrence as recurrence, ");
            sql.append("sc.recurrence_start_date as recurrenceStartDate, ");
            sql.append("sc.next_trigger_date as nextTriggerDate, ");
            sql.append("sc.last_trigger_date as lastTriggerDate, ");
            sql.append("sc.submittedon_date as submittedOnDate, ");
            sql.append("sbu.username as submittedByUsername, ");
            sql.append("sc.closedon_date as closedOnDate, ");
            sql.append("clu.username as closedByUsername, ");
            sql.append("acu.username as activatedByUsername, ");
            sql.append("sc.approvedon_date as activatedOnDate ");
            sql.append("from sms_campaign sc ");
            sql.append("left join m_appuser sbu on sbu.id = sc.submittedon_userid ");
            sql.append("left join m_appuser acu on acu.id = sc.approvedon_userid ");
            sql.append("left join m_appuser clu on clu.id = sc.closedon_userid ");

            this.schema = sql.toString();
        }
        public String schema() {
            return this.schema;
        }

        @Override
        public SmsCampaignData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Long id = JdbcSupport.getLong(rs, "id");
            final String campaignName = rs.getString("campaignName");
            final Integer campaignType = JdbcSupport.getInteger(rs, "campaignType");
            final Long runReportId = JdbcSupport.getLong(rs, "runReportId");
            final String paramValue = rs.getString("paramValue");
            final String message  = rs.getString("message");

            final Integer statusId = JdbcSupport.getInteger(rs, "status");
            final EnumOptionData status = SmsCampaignStatusEnumerations.status(statusId);
            final DateTime nextTriggerDate = JdbcSupport.getDateTime(rs, "nextTriggerDate");
            final LocalDate  lastTriggerDate = JdbcSupport.getLocalDate(rs, "lastTriggerDate");


            final LocalDate closedOnDate = JdbcSupport.getLocalDate(rs, "closedOnDate");
            final String closedByUsername = rs.getString("closedByUsername");


            final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
            final String submittedByUsername = rs.getString("submittedByUsername");

            final LocalDate activatedOnDate = JdbcSupport.getLocalDate(rs, "activatedOnDate");
            final String activatedByUsername = rs.getString("activatedByUsername");
            final String recurrence  =rs.getString("recurrence");
            final DateTime recurrenceStartDate = JdbcSupport.getDateTime(rs, "recurrenceStartDate");
            final SmsCampaignTimeLine smsCampaignTimeLine = new SmsCampaignTimeLine(submittedOnDate,submittedByUsername,
                    activatedOnDate,activatedByUsername,closedOnDate,closedByUsername);



            return SmsCampaignData.instance(id,campaignName,campaignType,runReportId,paramValue,status,message,nextTriggerDate,lastTriggerDate,smsCampaignTimeLine,
                    recurrenceStartDate,recurrence);
        }
    }


    private static final class BusinessRuleMapper implements ResultSetExtractor<List<SmsBusinessRulesData>>{

        final String schema;

        private BusinessRuleMapper() {
            final StringBuilder sql = new StringBuilder(300);
            sql.append("sr.id as id, ");
            sql.append("sr.report_name as reportName, ");
            sql.append("sr.report_type as reportType, ");
            sql.append("sr.description as description, ");
            sql.append("sp.parameter_variable as params, ");
            sql.append("sp.parameter_FormatType as paramType, ");
            sql.append("sp.parameter_label as paramLabel, ");
            sql.append("sp.parameter_name as paramName ");
            sql.append("from stretchy_report sr ");
            sql.append("left join stretchy_report_parameter as srp on srp.report_id = sr.id ");
            sql.append("left join stretchy_parameter as sp on sp.id = srp.parameter_id ");

            this.schema = sql.toString();
        }

        public String schema(){
            return this.schema;
        }

        @Override
        public List<SmsBusinessRulesData> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<SmsBusinessRulesData> smsBusinessRulesDataList = new ArrayList<SmsBusinessRulesData>();

            SmsBusinessRulesData smsBusinessRulesData = null;

            Map<Long,SmsBusinessRulesData> mapOfSameObjects = new HashMap<Long, SmsBusinessRulesData>();

            while(rs.next()){
                final Long id = rs.getLong("id");
                smsBusinessRulesData  = mapOfSameObjects.get(id);
                if(smsBusinessRulesData == null){
                    final String reportName = rs.getString("reportName") ;
                    final String reportType = rs.getString("reportType");
                    final String paramName  = rs.getString("paramName");
                    final String paramLabel = rs.getString("paramLabel");
                    final String description = rs.getString("description");

                    Map<String,Object> hashMap = new HashMap<String, Object>();
                    hashMap.put(paramLabel,paramName);
                    smsBusinessRulesData = SmsBusinessRulesData.instance(id,reportName,reportType,hashMap,description);
                    mapOfSameObjects.put(id,smsBusinessRulesData);
                    //add to the list
                    smsBusinessRulesDataList.add(smsBusinessRulesData);
                }
                //add new paramType to the existing object
                Map<String,Object> hashMap = new HashMap<String, Object>();
                final String paramName  = rs.getString("paramName");
                final String paramLabel = rs.getString("paramLabel");
                hashMap.put(paramLabel,paramName);

                //get existing map and add new items to it
                smsBusinessRulesData.getReportParamName().putAll(hashMap);
            }

            return smsBusinessRulesDataList;
        }
    }

    @Override
    public Collection<SmsBusinessRulesData> retrieveAll() {
        final String searchType = "sms";
        final String sql = "select " + this.businessRuleMapper.schema() + " where sr.report_type = ?";

        return this.jdbcTemplate.query(sql, this.businessRuleMapper, new Object[] {searchType});
    }

    @Override
    public SmsBusinessRulesData retrieveOneTemplate(Long resourceId) {
        final String searchType = "sms";

        final String sql = "select " + this.businessRuleMapper.schema() + " where sr.report_type = ? and sr.id = ?";

        List<SmsBusinessRulesData> retrieveOne =  this.jdbcTemplate.query(sql, this.businessRuleMapper, new Object[] {searchType,resourceId});
        try{
            SmsBusinessRulesData smsBusinessRulesData = retrieveOne.get(0);
            return smsBusinessRulesData;
        }
        catch (final IndexOutOfBoundsException e){
            throw new SmsBusinessRuleNotFound(resourceId);
        }

    }

    @Override
    public SmsCampaignData retrieveOne(Long resourceId) {
        final Integer isVisible =1;
        try{
            final String sql = "select " + this.smsCampaignMapper.schema + " where sc.id = ? and sc.is_visible = ?";
            return this.jdbcTemplate.queryForObject(sql, this.smsCampaignMapper, new Object[] { resourceId,isVisible});
        } catch (final EmptyResultDataAccessException e) {
            throw new SmsCampaignNotFound(resourceId);
        }
    }

    @Override
    public Collection<SmsCampaignData> retrieveAllCampaign() {
        final Integer visible = 1;
        final String sql = "select " + this.smsCampaignMapper.schema() + " where sc.is_visible = ?";
        return this.jdbcTemplate.query(sql, this.smsCampaignMapper, new Object[] {visible});
    }

    @Override
    public Collection<SmsCampaignData> retrieveAllScheduleActiveCampaign() {
        final Integer scheduleCampaignType = SmsCampaignType.SCHEDULE.getValue();
        final Integer statusEnum  = SmsCampaignStatus.ACTIVE.getValue();
        final Integer visible     = 1;
        final String sql = "select " + this.smsCampaignMapper.schema() + " where sc.status_enum = ? and sc.campaign_type = ? and sc.is_visible = ?";
        return this.jdbcTemplate.query(sql,this.smsCampaignMapper, new Object [] {statusEnum,scheduleCampaignType,visible});
    }



}
