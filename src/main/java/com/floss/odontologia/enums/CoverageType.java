package com.floss.odontologia.enums;

public enum CoverageType {

    SELF_PAY,          // No medical coverage
                       // Pays 100% of the treatment

    HEALTH_INSURANCE,  // Union/State coverage
                       // May involve future discounts

    PREPAID_INSURANCE, // Private coverage
                       // Prepared for agreements or co-payments

    OTHER              // Exceptional/unclassified cases
}
