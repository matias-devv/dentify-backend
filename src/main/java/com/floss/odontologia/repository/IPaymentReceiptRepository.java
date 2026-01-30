package com.floss.odontologia.repository;

import com.floss.odontologia.model.PaymentReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPaymentReceiptRepository extends JpaRepository<PaymentReceipt, Long> {
}
