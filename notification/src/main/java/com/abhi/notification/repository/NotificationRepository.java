package com.abhi.notification.repository;

import com.abhi.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationLog,Long> {
    boolean existsByReferenceId(String referenceId);
}
