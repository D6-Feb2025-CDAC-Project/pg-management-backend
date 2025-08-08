package com.easypg.dao;

import com.easypg.dto.RoomWithFacilitiesDTO;
import com.easypg.entities.Room;
import com.easypg.enums.RoomType;
import com.easypg.enums.TenantType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomDao extends JpaRepository<Room, Long> {
	@Query("SELECT DISTINCT r FROM Room r LEFT JOIN FETCH r.facilities WHERE r.hidden = false")
    List<Room> findByHiddenFalse(); // Fetch only visible rooms
  
  public Optional<Room> findByRoomNo(String roomNo);
  
  @Query("SELECT DISTINCT r FROM Room r LEFT JOIN FETCH r.facilities f WHERE r.hidden = false AND (f.isDeleted = false OR f IS NULL) AND r.currentOccupancy <= r.maxOccupancy")
  List<Room> findRoomsWithFacilities();

}
