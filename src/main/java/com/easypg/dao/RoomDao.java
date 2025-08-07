package com.easypg.dao;

import com.easypg.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomDao extends JpaRepository<Room, Long> {
    List<Room> findByHiddenFalse(); // Fetch only visible rooms
  
  public Optional<Room> findByRoomNo(String roomNo);
}
