package com.floss.odontologia.repository;

import com.floss.odontologia.model.Pay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPayRepository extends JpaRepository<Pay, Long> {
}
