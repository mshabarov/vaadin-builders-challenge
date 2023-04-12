package org.teamhq.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.teamhq.data.entity.MealChoice;

public interface MealChoiceRepository extends JpaRepository<MealChoice, Long>, JpaSpecificationExecutor<MealChoice> {

}
