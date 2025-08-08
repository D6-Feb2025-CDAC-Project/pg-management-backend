
package com.easypg.dao;

import com.easypg.dto.FacilityDTO;
import com.easypg.entities.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FacilityDao extends JpaRepository<Facility, Long> {
    Optional<Facility> findByName(String name);
    List<Facility> findByIsDeletedFalse();
    
    @Query("SELECT new com.easypg.dto.FacilityDTO(MIN(f.id),f.name,MIN(f.category)) "
    		+ "FROM Facility f "
    		+ "WHERE f.isDeleted = false "
    		+ "GROUP BY f.name")
	List<FacilityDTO> getAvailableFacilities();
}
