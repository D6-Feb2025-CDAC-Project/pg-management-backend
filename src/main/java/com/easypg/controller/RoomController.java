package com.easypg.controller;

import com.easypg.dto.RoomDTO;
import com.easypg.entities.Room;
import com.easypg.service.RoomService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private ModelMapper modelMapper;

    //  Get all visible rooms
    @GetMapping
    public ResponseEntity<?> getAllVisibleRooms() {
        List<Room> rooms = roomService.getAllVisibleRooms();

        if (rooms.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<RoomDTO> roomDTOs = rooms.stream()
                .map(roomService::mapToDto) 
                .collect(Collectors.toList());

        return ResponseEntity.ok(roomDTOs);
    }

    //  Get room by ID
    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long id) {
        Room room = roomService.getRoomById(id);
        RoomDTO dto = roomService.mapToDto(room);
        return ResponseEntity.ok(dto);
    }

   
    //Add new room
    @PostMapping
    public ResponseEntity<RoomDTO> addRoom(@RequestBody RoomDTO roomDTO) {
        RoomDTO savedRoom = roomService.addRoom(roomDTO);
        return new ResponseEntity<>(savedRoom, HttpStatus.CREATED);
    }

    // Update room
    @PutMapping("/{id}")
    public ResponseEntity<String> updateRoom(@PathVariable Long id, @RequestBody RoomDTO roomDto) {
        roomService.updateRoom(id, roomDto);
        return ResponseEntity.ok("Room updated successfully");
    }


    //  Hide room instead of deleting
    @PutMapping("/{id}/hide")
    public ResponseEntity<String> hideRoom(@PathVariable Long id) {
        roomService.hideRoom(id);
        return ResponseEntity.ok("Room hidden successfully");
    }
}
