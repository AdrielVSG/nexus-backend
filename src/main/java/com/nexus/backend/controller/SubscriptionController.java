package com.nexus.backend.controller;

import com.nexus.backend.dto.SubscriptionDTOs.*;
import com.nexus.backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(@RequestBody SubscriptionRequest request, Authentication auth) {
        return ResponseEntity.ok(subscriptionService.create(request, auth.getName()));
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> list(Authentication auth) {
        return ResponseEntity.ok(subscriptionService.getAllByUser(auth.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        subscriptionService.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> update(@PathVariable Long id, @RequestBody SubscriptionRequest request, Authentication auth) {
        return ResponseEntity.ok(subscriptionService.update(id, request, auth.getName()));
    }

    @PatchMapping("/{id}/toggle-pause")
    public ResponseEntity<SubscriptionResponse> togglePause(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(subscriptionService.togglePause(id, auth.getName()));
    }

    @PostMapping("/{id}/usage")
    public ResponseEntity<Void> logUsage(@PathVariable Long id, Authentication auth) {
        subscriptionService.logUsage(id, auth.getName());
        return ResponseEntity.ok().build();
    }
}
