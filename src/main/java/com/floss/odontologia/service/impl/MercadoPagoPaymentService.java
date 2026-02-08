package com.floss.odontologia.service.impl;

import com.floss.odontologia.repository.IMercadoPagoPaymentRepository;
import com.floss.odontologia.service.interfaces.IMercadoPagoPaymentService;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MercadoPagoPayment implements IMercadoPagoPaymentService {

    private final IMercadoPagoPaymentRepository mercadoPagoPaymentRepository;

    @Override
    public void updatePayMercadoPagoData(com.floss.odontologia.model.MercadoPagoPayment payMP, Payment payment) {
        payMP.setPaymentId(payment.getId().toString());
        payMP.setStatus(payment.getStatus());
        payMP.setStatusDetail(payment.getStatusDetail());
        payMP.setTransactionAmount(payment.getTransactionAmount());
        payMP.setPayerEmail(payment.getPayer() != null ? payment.getPayer().getEmail() : null);
        payMP.setInstallments(payment.getInstallments());
        payMP.setPaymentTypeId(payment.getPaymentTypeId());

        if (payment.getDateApproved() != null) {
            payMP.setDateApproved(
                    LocalDateTime.ofInstant(
                            payment.getDateApproved().toInstant(),
                            ZoneId.systemDefault()
                    )
            );
        }
        mercadoPagoPaymentRepository.save(payMP);
    }

    @Override
    public Optional<com.floss.odontologia.model.MercadoPagoPayment> findByExternalReference(String externalRef) {
        return mercadoPagoPaymentRepository.findByExternalReference(externalRef);
    }
}
