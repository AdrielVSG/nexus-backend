package com.nexus.backend.service;

import com.nexus.backend.dto.SubscriptionDTOs.*;
import com.nexus.backend.entity.Subscription;
import com.nexus.backend.entity.User;
import com.nexus.backend.repository.CategoryRepository;
import com.nexus.backend.repository.SubscriptionRepository;
import com.nexus.backend.repository.UserRepository;
import com.nexus.backend.repository.UsageLogRepository;
import com.nexus.backend.entity.UsageLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final UsageLogRepository usageLogRepository;

    public SubscriptionResponse create(SubscriptionRequest request, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        var category = request.getCategoryId() != null ? 
                categoryRepository.findById(request.getCategoryId()).orElse(null) : null;

        var subscription = Subscription.builder()
                .serviceName(request.getServiceName())
                .price(request.getPrice())
                .dueDate(request.getDueDate())
                .status("ACTIVE")
                .imageUrl(request.getImageUrl())
                .user(user)
                .category(category)
                .build();

        subscription = subscriptionRepository.save(subscription);
        return mapToResponse(subscription);
    }

    public List<SubscriptionResponse> getAllByUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return subscriptionRepository.findAllByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void delete(Long id, String email) {
        Subscription subscription = subscriptionRepository.findById(id).orElseThrow();
        if (!subscription.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Não autorizado");
        }
        subscriptionRepository.delete(subscription);
    }

    public SubscriptionResponse update(Long id, SubscriptionRequest request, String email) {
        System.out.println("Solicitação de atualização para ID: " + id + " pelo usuário: " + email);
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assinatura não encontrada com ID: " + id));

        if (!subscription.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Não autorizado a alterar dados de outro usuário");
        }

        var category = request.getCategoryId() != null ?
                categoryRepository.findById(request.getCategoryId()).orElse(null) : null;

        subscription.setServiceName(request.getServiceName());
        subscription.setPrice(request.getPrice());
        subscription.setDueDate(request.getDueDate());
        subscription.setCategory(category);
        subscription.setImageUrl(request.getImageUrl());

        Subscription updated = subscriptionRepository.save(subscription);
        System.out.println("Assinatura atualizada com sucesso no banco de dados.");
        return mapToResponse(updated);
    }

    public SubscriptionResponse togglePause(Long id, String email) {
        Subscription subscription = subscriptionRepository.findById(id).orElseThrow();
        if (!subscription.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Não autorizado");
        }

        String newStatus = "ACTIVE".equals(subscription.getStatus()) ? "PAUSED" : "ACTIVE";
        subscription.setStatus(newStatus);
        
        subscription = subscriptionRepository.save(subscription);
        return mapToResponse(subscription);
    }

    public void logUsage(Long id, String email) {
        Subscription subscription = subscriptionRepository.findById(id).orElseThrow();
        if (!subscription.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Não autorizado");
        }

        UsageLog log = UsageLog.builder()
                .subscription(subscription)
                .usageDate(LocalDateTime.now())
                .build();
        usageLogRepository.save(log);
    }

    private SubscriptionResponse mapToResponse(Subscription s) {
        return SubscriptionResponse.builder()
                .id(s.getId())
                .serviceName(s.getServiceName())
                .price(s.getPrice())
                .dueDate(s.getDueDate())
                .status(s.getStatus())
                .categoryName(s.getCategory() != null ? s.getCategory().getName() : "Sem Categoria")
                .imageUrl(s.getImageUrl())
                .build();
    }
}
