package com.easypg.service;

import com.easypg.dto.RoomDTO;
import com.easypg.entities.Room;

import java.util.List;

public interface RoomService {
    List<Room> getAllVisibleRooms();
    Room getRoomById(Long id);
    RoomDTO addRoom(RoomDTO room);
    void updateRoom(Long id, RoomDTO roomDto);
    RoomDTO mapToDto(Room room);
    Room mapToEntity(RoomDTO dto);
    void hideRoom(Long id);     
    
}
