package com.telegramapproval.repository;

import com.telegramapproval.model.PendingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingRequestRepository extends JpaRepository<PendingRequest, Long> {
    List<PendingRequest> findAllByOrderByCreatedAtDesc();
}
