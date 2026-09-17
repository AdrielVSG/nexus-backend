package com.nexus.backend.repository;

import com.nexus.backend.entity.Transaction;
import com.nexus.backend.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByWalletOrderByTimestampDesc(Wallet wallet);
}
