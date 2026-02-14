package com.dentify.domain.mercadopagopayment.service;

import com.dentify.domain.mercadopagopayment.model.MercadoPagoPayment;
import com.dentify.domain.pay.model.Pay;
import com.mercadopago.resources.payment.Payment;

import java.util.List;
import java.util.Optional;

public interface IMercadoPagoPaymentService {

    public void updatePayMercadoPagoData(MercadoPagoPayment payMP, Payment payment);

    Optional<MercadoPagoPayment> findByExternalReference(String externalRef);

    Pay findPendingPayWithMercadoPago(List<Pay> pays);
}
