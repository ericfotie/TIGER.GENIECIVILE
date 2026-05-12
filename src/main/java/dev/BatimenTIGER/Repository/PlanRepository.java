package dev.BatimenTIGER.Repository;

import dev.BatimenTIGER.Model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    // Lister les plans par type (ex: tous les plans de "Structure")
    List<Plan> findByTypeTechnique(String type);
}
