package com.nexus.backend.scheduler;

import com.nexus.backend.entity.Subscription;
import com.nexus.backend.entity.Transaction;
import com.nexus.backend.entity.Wallet;
import com.nexus.backend.repository.SubscriptionRepository;
import com.nexus.backend.repository.TransactionRepository;
import com.nexus.backend.repository.WalletRepository;
import com.nexus.backend.repository.UsageLogRepository;
import com.nexus.backend.entity.UsageLog;
import com.nexus.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UsageLogRepository usageLogRepository;
    private final NotificationService notificationService;

    // Executa todos os dias à meia-noite
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processSubscriptions() {
        LocalDate today = LocalDate.now();
        List<Subscription> allSubscriptions = subscriptionRepository.findAll();

        for (Subscription sub : allSubscriptions) {
            // Lógica de Pagamento
            if ("ACTIVE".equals(sub.getStatus()) && sub.getDueDate().isEqual(today)) {
                processPayment(sub);
            }

            // Lógica de Subutilização (Inteligência)
            checkUnderuse(sub);
        }
    }

    private void checkUnderuse(Subscription sub) {
        if (!"ACTIVE".equals(sub.getStatus())) return;

        Optional<UsageLog> lastUsage = usageLogRepository.findTopBySubscriptionOrderByUsageDateDesc(sub);
        LocalDateTime limitDate = LocalDateTime.now().minusDays(30);

        if (lastUsage.isPresent()) {
            if (lastUsage.get().getUsageDate().isBefore(limitDate)) {
                notificationService.sendNotification(
                        sub.getUser(),
                        "Você não utiliza o serviço " + sub.getServiceName() + " há mais de 30 dias. Considere pausar para economizar!",
                        "UNDERUSE_ALERT"
                );
            }
        } else {
            // Se nunca foi registrado uso e a assinatura tem mais de 30 dias
            // (Para simplificar, vamos assumir que se não tem log, nunca foi usado)
            // Poderíamos checar a data de criação da assinatura aqui
        }
    }

    private void processPayment(Subscription sub) {
        Wallet wallet = walletRepository.findByUser(sub.getUser()).orElse(null);
        if (wallet == null) return;

        if (wallet.getBalance().compareTo(sub.getPrice()) >= 0) {
            // Saldo suficiente - Pagar virtualmente
            wallet.setBalance(wallet.getBalance().subtract(sub.getPrice()));
            walletRepository.save(wallet);

            Transaction transaction = Transaction.builder()
                    .description("Pagamento: " + sub.getServiceName())
                    .amount(sub.getPrice())
                    .type("PAYMENT")
                    .timestamp(LocalDateTime.now())
                    .wallet(wallet)
                    .build();
            transactionRepository.save(transaction);

            // Atualizar data de vencimento para o próximo mês
            sub.setDueDate(sub.getDueDate().plusMonths(1));
            subscriptionRepository.save(sub);

            notificationService.sendNotification(
                    sub.getUser(),
                    "Pagamento de R$ " + sub.getPrice() + " para " + sub.getServiceName() + " realizado com sucesso!",
                    "PAYMENT_SUCCESS"
            );
        } else {
            // Saldo insuficiente
            notificationService.sendNotification(
                    sub.getUser(),
                    "Saldo insuficiente para pagar " + sub.getServiceName() + " (R$ " + sub.getPrice() + ")",
                    "INSUFFICIENT_BALANCE"
            );
        }
    }
}
