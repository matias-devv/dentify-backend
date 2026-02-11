package com.floss.odontologia.model;

import com.floss.odontologia.enums.ReceiptType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
    private LocalDateTime issue_date;
    private String issued_by;
    private Boolean sent_patient_email;
    private Boolean sent_email_dentist;

}
