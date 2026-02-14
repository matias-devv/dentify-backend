package com.dentify.domain.packproducts.repository;

import com.dentify.domain.packproducts.model.PackProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPackProductRepository extends JpaRepository<PackProduct, Long> {
}
