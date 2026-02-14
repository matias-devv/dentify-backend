package com.dentify.domain.receipt.model;

import com.dentify.domain.pay.model.Pay;
import com.dentify.domain.receipt.enums.ReceiptType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Table ( name = "receipts") @Builder
public class Receipt {

    @Id @GeneratedValue ( strategy = GenerationType.IDENTITY)
    private Long id_receipt;

    @OneToOne
    @JoinColumn ( name = "id_pay", referencedColumnName = "id_pay", unique = true, nullable = false)
    private Pay pay;

    @Enumerated( EnumType.STRING)
    private ReceiptType receiptType;
    @Column(length = 500)
    private String url_pdf;
    private String filename;
    @Column(name = "issue_date")
    private LocalDateTime issueDate;
    private String issued_by;
    private Boolean sent_patient_email;
    private Boolean sent_email_dentist;

}
