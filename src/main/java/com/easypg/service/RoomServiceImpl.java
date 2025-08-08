package com.easypg.service;
import com.easypg.entities.Facility;
import com.easypg.entities.Room;
import com.easypg.enums.RoomType;
import com.easypg.enums.TenantType;
import com.easypg.custom_exceptions.ResourceNotFoundException;
import com.easypg.dao.FacilityDao;
import com.easypg.dao.RoomDao;
import com.easypg.dto.FacilityDTO;
import com.easypg.dto.RoomDTO;
import com.easypg.dto.RoomWithFacilitiesDTO;
import com.easypg.service.RoomService;



import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {


	
	

    @Autowired
    private RoomDao roomRepository;
    
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FacilityDao facilityRepository;
    
    @Value("${app.upload.dir:uploads/rooms}")
    private String uploadDir;
    
    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

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

        // Handle facilities for Many-to-Many relationship
        if (roomDTO.getFacilities() != null) {
            List<Facility> facilities = new ArrayList<>();
            for (FacilityDTO facilityDTO : roomDTO.getFacilities()) {
                // Check if facility already exists
                Facility existingFacility = facilityRepository.findByNameAndCategory(
                    facilityDTO.getName(), 
                    facilityDTO.getCategory()
                );
                
                if (existingFacility != null) {
                    facilities.add(existingFacility);
                } else {
                    // Create new facility
                    Facility newFacility = new Facility();
                    newFacility.setName(facilityDTO.getName());
                    newFacility.setCategory(facilityDTO.getCategory());
                    newFacility = facilityRepository.save(newFacility);
                    facilities.add(newFacility);
                }
            }
            room.setFacilities(facilities);
        }

        Room savedRoom = roomRepository.save(room);
        return modelMapper.map(savedRoom, RoomDTO.class);
    }

    public RoomDTO addRoomWithImage(RoomDTO roomDTO, MultipartFile imageFile) {
        try {
            System.out.println("Adding room with image. Facilities: " + roomDTO.getFacilities());
            
            // 1. Create Room entity
            Room room = new Room();
            room.setRoomNo(roomDTO.getRoomNo());
            room.setRoomType(roomDTO.getRoomType());
            room.setTenantType(roomDTO.getTenantType());
            room.setFloor(roomDTO.getFloor());
            room.setRentAmount(roomDTO.getRentAmount());
            room.setMaxOccupancy(roomDTO.getMaxOccupancy());
            room.setCurrentOccupancy(roomDTO.getCurrentOccupancy());
            room.setAvailable(true);
            
            // 2. Handle image upload
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = uploadImage(imageFile);
                room.setPhotoUrl(imageUrl);
                System.out.println("Image uploaded successfully: " + imageUrl);
            }
            
            // 3. Handle facilities for Many-to-Many
            if (roomDTO.getFacilities() != null && !roomDTO.getFacilities().isEmpty()) {
                List<Facility> facilities = new ArrayList<>();
                
                for (FacilityDTO facilityDTO : roomDTO.getFacilities()) {
                    System.out.println("Processing facility: " + facilityDTO.getName());
                    
                    // Check if facility already exists in database
                    Facility facility = facilityRepository.findByNameAndCategory(
                        facilityDTO.getName(), 
                        facilityDTO.getCategory()
                    );
                    
                    if (facility == null) {
                        // Create new facility
                        facility = new Facility();
                        facility.setName(facilityDTO.getName());
                        facility.setCategory(facilityDTO.getCategory());
                        facility = facilityRepository.save(facility);
                        System.out.println("Created new facility with ID: " + facility.getId());
                    } else {
                        System.out.println("Using existing facility with ID: " + facility.getId());
                    }
                    
                    facilities.add(facility);
                }
                
                room.setFacilities(facilities);
            }
            
            // 4. Save the room with facilities
            Room savedRoom = roomRepository.save(room);
            System.out.println("Room saved with ID: " + savedRoom.getId());
            
            return mapToDto(savedRoom);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add room: " + e.getMessage(), e);
        }
    }
    
    private String uploadImage(MultipartFile file) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String uniqueFilename = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + fileExtension;
        
        // Full file path
        Path filePath = uploadPath.resolve(uniqueFilename);
        
        // Copy file to destination
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Return URL path for accessing the image
        String imageUrl = "/uploads/rooms/" + uniqueFilename;
        System.out.println("Image uploaded to: " + filePath.toAbsolutePath());
        System.out.println("Image URL: " + imageUrl);
        
        return imageUrl;
    }
    
    public void deleteImage(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Convert URL back to file path
                String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(uploadDir, filename);
                
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    System.out.println("Deleted image: " + filePath);
                }
            }
        } catch (Exception e) {
            System.err.println("Error deleting image: " + e.getMessage());
        }
    }
    
    public String getFullImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        if (imageUrl.startsWith("http")) {
            return imageUrl; // Already full URL
        }
        
        return baseUrl + imageUrl;
    }
    
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
        dto.setPhotoUrl(getFullImageUrl(room.getPhotoUrl()));
        dto.setAvailable(room.isAvailable());
        
        // Map facilities
        if (room.getFacilities() != null) {
            List<FacilityDTO> facilityDTOs = new ArrayList<>();
            for (Facility facility : room.getFacilities()) {
                FacilityDTO facilityDTO = new FacilityDTO();
                facilityDTO.setId(facility.getId());
                facilityDTO.setName(facility.getName());
                facilityDTO.setCategory(facility.getCategory());
                facilityDTOs.add(facilityDTO);
            }
            dto.setFacilities(facilityDTOs);
        }
        
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

        // Handle facilities for Many-to-Many
        if (dto.getFacilities() != null && !dto.getFacilities().isEmpty()) {
            List<Facility> facilities = new ArrayList<>();
            for (FacilityDTO facilityDTO : dto.getFacilities()) {
                // Check if facility exists
                Facility existingFacility = facilityRepository.findByNameAndCategory(
                    facilityDTO.getName(), 
                    facilityDTO.getCategory()
                );
                
                if (existingFacility != null) {
                    facilities.add(existingFacility);
                } else {
                    // Create new facility
                    Facility newFacility = new Facility();
                    newFacility.setName(facilityDTO.getName());
                    newFacility.setCategory(facilityDTO.getCategory());
                    facilities.add(newFacility);
                }
            }
            room.setFacilities(facilities);
        } else {
            room.setFacilities(new ArrayList<>());
        }

        return room;
    }

    public void updateRoom(Long id, RoomDTO roomDto) {
        Room existingRoom = roomRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));

        // Update room properties
        existingRoom.setRoomNo(roomDto.getRoomNo());
        existingRoom.setRoomType(roomDto.getRoomType());
        existingRoom.setTenantType(roomDto.getTenantType());
        existingRoom.setFloor(roomDto.getFloor());
        existingRoom.setRentAmount(roomDto.getRentAmount());
        existingRoom.setMaxOccupancy(roomDto.getMaxOccupancy());
        existingRoom.setCurrentOccupancy(roomDto.getCurrentOccupancy());

        // Update facilities for Many-to-Many
        existingRoom.getFacilities().clear();
        if (roomDto.getFacilities() != null) {
            List<Facility> facilities = new ArrayList<>();
            for (FacilityDTO fDto : roomDto.getFacilities()) {
                // Check if facility exists
                Facility existingFacility = facilityRepository.findByNameAndCategory(
                    fDto.getName(), 
                    fDto.getCategory()
                );
                
                if (existingFacility != null) {
                    facilities.add(existingFacility);
                } else {
                    // Create new facility
                    Facility newFacility = new Facility();
                    newFacility.setName(fDto.getName());
                    newFacility.setCategory(fDto.getCategory());
                    newFacility = facilityRepository.save(newFacility);
                    facilities.add(newFacility);
                }
            }
            existingRoom.setFacilities(facilities);
        }

        roomRepository.save(existingRoom);
    }

    @Override
    public void hideRoom(Long id) {
        Room room = roomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        room.setHidden(true);
        roomRepository.save(room);
    }

			@Override
			public List<RoomWithFacilitiesDTO> findRoomWithFacilties() {
				 List<Room> rooms = roomRepository.findRoomsWithFacilities();

				 List<RoomWithFacilitiesDTO> roomsWithFacilties =  rooms.stream().map(r -> new RoomWithFacilitiesDTO(
						 r.getId(),
				            r.getRoomNo(),
				            r.getRoomType(),
				            r.getFloor(),
				            r.getSize(),
				            r.getRentAmount(),
				            r.getCurrentOccupancy(),
				            r.getMaintenanceCharges(),
				            r.getElectricityCharges(),
				            r.getDeposit(),
				            r.getTenantType(),
				            r.getPhotoUrl(),
				            r.getFacilities().stream()
				                    .filter(f -> !f.isDeleted())
				                    .map(f -> new FacilityDTO(f.getId(), f.getName(), f.getCategory()))
				                    .toList()
				    )).toList();
				 
		   return roomsWithFacilties;  
			}
	

}
