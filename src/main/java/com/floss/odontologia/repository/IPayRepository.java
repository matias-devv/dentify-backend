package com.floss.odontologia.repository;

import com.floss.odontologia.enums.PaymentMethod;
import com.floss.odontologia.model.Pay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPayRepository extends JpaRepository<Pay, Long> {
}
