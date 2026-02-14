package com.dentify.domain.mercadopagopayment.service;

import com.dentify.domain.pay.model.Pay;
import com.mercadopago.resources.payment.Payment;
import com.dentify.domain.pay.enums.PaymentMethod;
import com.dentify.domain.pay.enums.PaymentStatus;
import com.dentify.domain.mercadopagopayment.repository.IMercadoPagoPaymentRepository;
import com.dentify.domain.mercadopagopayment.model.MercadoPagoPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MercadoPagoPaymentService implements IMercadoPagoPaymentService {

    private final IMercadoPagoPaymentRepository mercadoPagoPaymentRepository;

    @Override
    public void updatePayMercadoPagoData(MercadoPagoPayment payMP, Payment payment) {
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
    public Optional<MercadoPagoPayment> findByExternalReference(String externalRef) {
        return mercadoPagoPaymentRepository.findByExternalReference(externalRef);
    }

    @Override
    public Pay findPendingPayWithMercadoPago(List<Pay> pays) {
        return pays.stream().filter(p ->  p.getPayment_status() == PaymentStatus.PENDING ||
                                              p.getPayment_status() == PaymentStatus.AWAITING_PAYMENT)
                            .filter(p ->  p.getPayment_method() == PaymentMethod.MERCADO_PAGO)
                            .findFirst()
                            .orElse(null);
    }
}
