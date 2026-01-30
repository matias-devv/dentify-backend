package com.floss.odontologia.repository;

import com.floss.odontologia.model.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDayRepository extends JpaRepository<Day, Long> {
}
