package com.dentify.domain.pay.enums;

public enum PaymentStatus{

    PENDING,    // created, not yet confirmed
    AWAITING_PAYMENT, // code generated (RapiPago, PagoFacil), waiting for payment
    PARTIAL,    // partial payment (installments / advance)
    PAID,       // full payment confirmed

    FAILED,     // loading error / inconsistency
    CANCELLED,  // manually cancelled
    EXPIRED     // not paid within the valid time

}
