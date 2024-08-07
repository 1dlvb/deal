package com.fintech.deal.service.impl;

import com.fintech.deal.model.DealStatus;
import com.fintech.deal.repository.StatusRepository;
import com.fintech.deal.service.StatusService;
import com.onedlvb.advice.LogLevel;
import com.onedlvb.advice.annotation.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * An implementation of {@link StatusService}
 * @author Matushkin Anton
 */
@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final StatusRepository statusRepository;

    @Override
    @AuditLog(logLevel = LogLevel.INFO)
    public DealStatus getStatusById(String id) {
        return statusRepository.findById(id).orElse(null);
    }

}
