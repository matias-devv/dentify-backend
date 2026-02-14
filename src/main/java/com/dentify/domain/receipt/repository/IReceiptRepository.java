package com.dentify.domain.receipt.repository;

import com.dentify.domain.receipt.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByIssueDateBefore(LocalDateTime cutoff);
}
