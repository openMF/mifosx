package org.mifosplatform.infrastructure.jobs.service;

import java.util.List;

import org.mifosplatform.infrastructure.jobs.domain.ScheduledJobDetail;
import org.mifosplatform.infrastructure.jobs.domain.ScheduledJobDetailRepository;
import org.mifosplatform.infrastructure.jobs.domain.ScheduledJobRunHistory;
import org.mifosplatform.infrastructure.jobs.domain.ScheduledJobRunHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchedularServiceJpaRepositoryImpl implements SchedularService {

    private final ScheduledJobDetailRepository scheduledJobDetailsRepository;

    private final ScheduledJobRunHistoryRepository scheduledJobRunHistoryRepository;

    @Autowired
    public SchedularServiceJpaRepositoryImpl(final ScheduledJobDetailRepository scheduledJobDetailsRepository,
            final ScheduledJobRunHistoryRepository scheduledJobRunHistoryRepository) {
        this.scheduledJobDetailsRepository = scheduledJobDetailsRepository;
        this.scheduledJobRunHistoryRepository = scheduledJobRunHistoryRepository;
    }

    @Override
    public List<ScheduledJobDetail> getScheduledJobDetails() {
        return scheduledJobDetailsRepository.findAll();
    }

    @Override
    public ScheduledJobDetail getByTriggerKey(final String triggerKey) {
        return scheduledJobDetailsRepository.findByTriggerKey(triggerKey);
    }

    @Transactional
    @Override
    public void saveOrUpdate(final ScheduledJobDetail scheduledJobDetails) {
        this.scheduledJobDetailsRepository.save(scheduledJobDetails);
    }

    @Transactional
    @Override
    public void saveOrUpdate(final ScheduledJobDetail scheduledJobDetails, final ScheduledJobRunHistory scheduledJobRunHistory) {
        this.scheduledJobDetailsRepository.save(scheduledJobDetails);
        this.scheduledJobRunHistoryRepository.save(scheduledJobRunHistory);
    }

    @Override
    public Long getMaxVersionBy(final String triggerKey) {
        Long version = 0L;
        Long versionFromDB = scheduledJobRunHistoryRepository.findMaxVersionByTriggerKey(triggerKey);
        if (versionFromDB != null) {
            version = versionFromDB;
        }
        return version;
    }
}
