package com.dentify.domain.pay.repository;

import com.dentify.domain.pay.model.Pay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPayRepository extends JpaRepository<Pay, Long> {
}
