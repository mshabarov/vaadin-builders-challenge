package org.teamhq.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.teamhq.data.entity.MealVendor;

public interface MealVendorRepository extends JpaRepository<MealVendor, Long>, JpaSpecificationExecutor<MealVendor> {

}
