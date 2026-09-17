package com.nexus.backend.repository;

import com.nexus.backend.entity.User;
import com.nexus.backend.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);
}
