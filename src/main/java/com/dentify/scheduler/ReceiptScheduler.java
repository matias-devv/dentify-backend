package com.dentify.scheduler;

import com.dentify.domain.receipt.model.Receipt;
import com.dentify.integration.cloudinary.ICloudinaryService;
import com.dentify.domain.receipt.service.IReceiptService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReceiptScheduler {

    private final IReceiptService receiptService;
    private final ICloudinaryService cloudinaryService;

    /**
     * Delete receipts older than 2 years (every day at 2 AM)
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanOldReceipts() {

        LocalDateTime cutoffDate = LocalDateTime.now().minusYears(2);

        List<Receipt> oldReceipts = receiptService.findByGenerationDateBefore(cutoffDate);

        log.info("🧹 Cleaning {} old receipts...", oldReceipts.size());

        for (Receipt receipt : oldReceipts) {
            try {

                receiptService.deleteReceipt( receipt.getId_receipt());

            } catch (Exception e) {
                log.error("Error deleting receipt {}: {}", receipt.getId_receipt(), e.getMessage());
            }
        }
    }
}
