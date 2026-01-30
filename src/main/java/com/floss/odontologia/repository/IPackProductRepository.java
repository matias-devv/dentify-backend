package com.floss.odontologia.repository;

import com.floss.odontologia.model.PackProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPackProductRepository extends JpaRepository<PackProduct, Long> {
}
