package com.nexus.backend.service;

import com.nexus.backend.dto.WalletDTOs.*;
import com.nexus.backend.entity.Transaction;
import com.nexus.backend.entity.User;
import com.nexus.backend.entity.Wallet;
import com.nexus.backend.repository.TransactionRepository;
import com.nexus.backend.repository.UserRepository;
import com.nexus.backend.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public WalletResponse getWalletInfo(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Wallet wallet = walletRepository.findByUser(user).orElseThrow();
        
        var transactions = transactionRepository.findAllByWalletOrderByTimestampDesc(wallet).stream()
                .limit(10)
                .map(t -> TransactionResponse.builder()
                        .id(t.getId())
                        .description(t.getDescription())
                        .amount(t.getAmount())
                        .type(t.getType())
                        .timestamp(t.getTimestamp())
                        .build())
                .collect(Collectors.toList());

        return WalletResponse.builder()
                .balance(wallet.getBalance())
                .recentTransactions(transactions)
                .build();
    }

    @Transactional
    public WalletResponse deposit(String email, BigDecimal amount) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Wallet wallet = walletRepository.findByUser(user).orElseThrow();

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .description("Depósito na Carteira")
                .amount(amount)
                .type("DEPOSIT")
                .timestamp(LocalDateTime.now())
                .wallet(wallet)
                .build();
        transactionRepository.save(transaction);

        return getWalletInfo(email);
    }
}
