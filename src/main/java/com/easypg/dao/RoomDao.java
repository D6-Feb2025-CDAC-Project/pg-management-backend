package com.easypg.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easypg.entities.Room;

public interface RoomDao extends JpaRepository<Room, Long>{

}
