package com.example.lease.web.admin.schedule;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.lease.model.entity.LeaseAgreement;
import com.example.lease.model.enums.LeaseStatus;
import com.example.lease.web.admin.service.LeaseAgreementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduleTasks {

    @Autowired
    private LeaseAgreementService service;

    @Scheduled(cron="0 0 0 * * *")
     public void checkLeaseStatus(){
        LambdaUpdateWrapper<LeaseAgreement> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.le(LeaseAgreement::getLeaseEndDate,new Date());
        updateWrapper.in(LeaseAgreement::getStatus,LeaseStatus.SIGNED,LeaseStatus.WITHDRAWING);
        updateWrapper.set(LeaseAgreement::getStatus, LeaseStatus.EXPIRED);
        service.update(updateWrapper);
    }

}
