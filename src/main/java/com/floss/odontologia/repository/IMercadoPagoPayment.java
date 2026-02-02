package com.floss.odontologia.repository;

import com.floss.odontologia.model.MercadoPagoPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IMercadoPagoPayment extends JpaRepository< MercadoPagoPayment ,Long> {
    Optional<MercadoPagoPayment> findByExternalReference(String externalReference);
}
