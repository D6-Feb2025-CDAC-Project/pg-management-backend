
package com.easypg.dao;

import com.easypg.entities.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacilityDao extends JpaRepository<Facility, Long> {
    Optional<Facility> findByName(String name);
}
