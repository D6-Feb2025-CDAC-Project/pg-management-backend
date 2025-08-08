package com.easypg.service;

import com.easypg.dto.RoomDTO;
import com.easypg.entities.Room;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface RoomService {
    List<Room> getAllVisibleRooms();
    Room getRoomById(Long id);
    RoomDTO addRoom(RoomDTO room);
    void updateRoom(Long id, RoomDTO roomDto);
    RoomDTO mapToDto(Room room);
    Room mapToEntity(RoomDTO dto);
    void hideRoom(Long id);     
    RoomDTO addRoomWithImage(RoomDTO roomDto, MultipartFile imageFile);
    
}
