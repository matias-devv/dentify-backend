package com.floss.odontologia.enums;

public enum PaymentStatus{

    PENDING,    // created, not yet confirmed
    PARTIAL,    // partial payment (installments / advance)
    PAID,       // full payment confirmed

    FAILED,     // loading error / inconsistency
    CANCELLED,  // manually cancelled
    EXPIRED     // not paid within the valid time
}
