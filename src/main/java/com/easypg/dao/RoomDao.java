package com.easypg.dao;

import com.easypg.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomDao extends JpaRepository<Room, Long> {
	@Query("SELECT DISTINCT r FROM Room r LEFT JOIN FETCH r.facilities WHERE r.hidden = false")
    List<Room> findByHiddenFalse(); // Fetch only visible rooms
  
  public Optional<Room> findByRoomNo(String roomNo);
}
