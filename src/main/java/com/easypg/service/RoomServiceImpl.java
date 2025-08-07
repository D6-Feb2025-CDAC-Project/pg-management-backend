package com.easypg.service;
import com.easypg.entities.Facility;
import com.easypg.entities.Room;
import com.easypg.custom_exceptions.ResourceNotFoundException;
import com.easypg.dao.FacilityDao;
import com.easypg.dao.RoomDao;
import com.easypg.dto.FacilityDTO;
import com.easypg.dto.RoomDTO;
import com.easypg.service.RoomService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomDao roomRepository;
    
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FacilityDao facilityRepository;

    @Override
    public List<Room> getAllVisibleRooms() {
        return roomRepository.findByHiddenFalse();
    }

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + id));
    }


    
    @Override
    public RoomDTO addRoom(RoomDTO roomDTO) {
        Room room = modelMapper.map(roomDTO, Room.class);

        // Manually map facilities and set back reference
        List<Facility> facilities = new ArrayList<>();
        if (roomDTO.getFacilities() != null) {
            for (FacilityDTO facilityDTO : roomDTO.getFacilities()) {
                Facility facility = modelMapper.map(facilityDTO, Facility.class);
                facility.setRoom(room); 
                facilities.add(facility);
            }
        }
        room.setFacilities(facilities);

        Room savedRoom = roomRepository.save(room);

       
        return modelMapper.map(savedRoom, RoomDTO.class);
    }

    @Override
    public RoomDTO mapToDto(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setRoomNo(room.getRoomNo());
        dto.setRoomType(room.getRoomType());
        dto.setTenantType(room.getTenantType());
        dto.setFloor(room.getFloor());
        dto.setRentAmount(room.getRentAmount());
        dto.setMaxOccupancy(room.getMaxOccupancy());
        dto.setCurrentOccupancy(room.getCurrentOccupancy());

        // mapping for facilities
        List<FacilityDTO> facilityDTOs = room.getFacilities().stream()
            .map(facility -> {
                FacilityDTO fDto = new FacilityDTO();
                fDto.setId(facility.getId());
                fDto.setName(facility.getName());
                fDto.setCategory(facility.getCategory()); 
                return fDto;
            })
            .collect(Collectors.toList());

        dto.setFacilities(facilityDTOs);
        return dto;
    }

    
    @Override
    public Room mapToEntity(RoomDTO dto) {
        Room room = new Room();
        room.setId(dto.getId()); 
        room.setRoomNo(dto.getRoomNo());
        room.setRoomType(dto.getRoomType());
        room.setTenantType(dto.getTenantType());
        room.setFloor(dto.getFloor());
        room.setRentAmount(dto.getRentAmount());
        room.setMaxOccupancy(dto.getMaxOccupancy());
        room.setCurrentOccupancy(dto.getCurrentOccupancy());
        room.setAvailable(true); 

        // Convert facility DTOs to Facility entities
        if (dto.getFacilities() != null && !dto.getFacilities().isEmpty()) {
            List<Facility> facilities = dto.getFacilities().stream().map(facilityDTO -> {
                Facility facility = new Facility();
                facility.setName(facilityDTO.getName());
                facility.setCategory(facilityDTO.getCategory());
                facility.setRoom(room); 
                return facility;
            }).collect(Collectors.toList());

            room.setFacilities(facilities);
        } else {
            room.setFacilities(new ArrayList<>());
        }

        return room;
    }


    public void updateRoom(Long id, RoomDTO roomDto) {
        Room existingRoom = roomRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));

      
        existingRoom.setRoomNo(roomDto.getRoomNo());
        existingRoom.setRoomType(roomDto.getRoomType());
        existingRoom.setTenantType(roomDto.getTenantType());
        existingRoom.setFloor(roomDto.getFloor());
        existingRoom.setRentAmount(roomDto.getRentAmount());
        existingRoom.setMaxOccupancy(roomDto.getMaxOccupancy());
        existingRoom.setCurrentOccupancy(roomDto.getCurrentOccupancy());

      
        existingRoom.getFacilities().clear();
        if (roomDto.getFacilities() != null) {
            for (FacilityDTO fDto : roomDto.getFacilities()) {
                Facility facility = new Facility();
                facility.setName(fDto.getName());
                facility.setCategory(fDto.getCategory()); 
                facility.setRoom(existingRoom);
                existingRoom.getFacilities().add(facility);
            }
        }

        roomRepository.save(existingRoom);
    }

  

    // Utility method to resolve facilities from DB 
    private void resolveFacilities(Room room) {
        if (room.getFacilities() != null) {
            List<Facility> updatedFacilities = room.getFacilities().stream()
                    .map(facility -> {
                        Facility newFacility = new Facility();
                        newFacility.setName(facility.getName());
                        newFacility.setRoom(room);  
                        return newFacility;
                    })
                    .collect(Collectors.toList());
            room.setFacilities(updatedFacilities);
        }
    }

               @Override
              public void hideRoom(Long id) {
               Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        room.setHidden(true);
        roomRepository.save(room);
    }
	
}
