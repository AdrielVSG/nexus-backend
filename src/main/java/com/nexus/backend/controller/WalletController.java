package com.nexus.backend.controller;

import com.nexus.backend.dto.WalletDTOs.*;
import com.nexus.backend.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<WalletResponse> getWallet(Authentication auth) {
        return ResponseEntity.ok(walletService.getWalletInfo(auth.getName()));
    }

    @PostMapping("/deposit")
    public ResponseEntity<WalletResponse> deposit(@RequestBody DepositRequest request, Authentication auth) {
        return ResponseEntity.ok(walletService.deposit(auth.getName(), request.getAmount()));
    }
}
