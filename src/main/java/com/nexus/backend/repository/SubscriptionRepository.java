package com.nexus.backend.repository;

import com.nexus.backend.entity.Subscription;
import com.nexus.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findAllByUser(User user);
}
