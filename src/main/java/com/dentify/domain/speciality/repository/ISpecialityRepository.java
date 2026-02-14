package com.dentify.domain.speciality.repository;

import com.dentify.domain.speciality.model.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISpecialityRepository extends JpaRepository<Speciality, Long> {
}
