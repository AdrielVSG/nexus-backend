package com.nexus.backend.repository;

import com.nexus.backend.entity.Subscription;
import com.nexus.backend.entity.UsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsageLogRepository extends JpaRepository<UsageLog, Long> {
    Optional<UsageLog> findTopBySubscriptionOrderByUsageDateDesc(Subscription subscription);
}
