package com.easypg.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easypg.entities.Room;

public interface RoomDao extends JpaRepository<Room, Long>{
	public Optional<Room> findByRoomNo(String roomNo);
}
