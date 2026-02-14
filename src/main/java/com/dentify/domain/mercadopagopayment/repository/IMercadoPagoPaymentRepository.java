package com.dentify.domain.mercadopagopayment.repository;

import com.dentify.domain.mercadopagopayment.model.MercadoPagoPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IMercadoPagoPaymentRepository extends JpaRepository< MercadoPagoPayment ,Long> {
    Optional<MercadoPagoPayment> findByExternalReference(String externalReference);
}
