
package com.easypg.dao;

import com.easypg.dto.FacilityDTO;
import com.easypg.entities.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FacilityDao extends JpaRepository<Facility, Long> {
    Optional<Facility> findByName(String name);
    List<Facility> findByIsDeletedFalse();
	
	@Query("SELECT f FROM Facility f WHERE f.name = :name AND f.category = :category")
    Facility findByNameAndCategory(@Param("name") String name, @Param("category") String category);
    
	
Optional<Facility> findByNameAndCategoryIgnoreCase(String name, String category);
    
    /**
     * Find facility by name only (if you don't want to use category)
     */
    Optional<Facility> findByNameIgnoreCase(String name);
    
    /**
     * Check if facility exists by name and category
     */
    boolean existsByNameAndCategoryIgnoreCase(String name, String category);
 @Query("SELECT new com.easypg.dto.FacilityDTO(MIN(f.id),f.name,MIN(f.category)) "
    		+ "FROM Facility f "
    		+ "WHERE f.isDeleted = false "
    		+ "GROUP BY f.name")
	List<FacilityDTO> getAvailableFacilities();
}
