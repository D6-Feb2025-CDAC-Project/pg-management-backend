package com.easypg.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easypg.custom_exceptions.DuplicateRecordFoundException;
import com.easypg.custom_exceptions.ResourceNotFoundException;
import com.easypg.dao.ComplaintDao;
import com.easypg.dao.RoomDao;
import com.easypg.dao.TenantDao;
import com.easypg.dao.UserDao;
import com.easypg.dto.AddTenantDTO;
import com.easypg.dto.ApiResponse;
import com.easypg.dto.LoginResponseDTO;
import com.easypg.dto.TenantResponseDTO;
import com.easypg.dto.UpdateTenantDTO;
import com.easypg.entities.Complaint;
import com.easypg.entities.Room;
import com.easypg.entities.Tenant;
import com.easypg.entities.User;
import com.easypg.enums.UserRole;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class TenantServiceImpl implements TenantService{
	private final TenantDao tenantDao;
	private final UserDao userDao;
	private final RoomDao roomDao;
	private final ComplaintDao complaintDao;
	
	private final ModelMapper mapper;

	// add new tenant
	@Override
	public ApiResponse addNewTenant(AddTenantDTO dto) {
	    // duplicate email checking
	    if (userDao.existsByEmail(dto.getEmail())) {
	        throw new DuplicateRecordFoundException("User with this email already exists!");
	    }

	    User user = mapper.map(dto, User.class);
	    user.setUserRole(UserRole.ROLE_USER);
	    User savedUser = userDao.save(user); // Generates shared ID
	    

	    Room room = roomDao.findById(dto.getRoomId())
	        .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + dto.getRoomId()));

	
	    Tenant tenant = mapper.map(dto, Tenant.class);
	    tenant.setUser(savedUser); // Shared PK
	    tenant.setRoom(room);
	    

	    Tenant savedTenant = tenantDao.save(tenant);

	    return new ApiResponse("Tenant added successfully with ID: " + savedTenant.getId());
	}

	
	// get all tenants
	@Override
	public List<TenantResponseDTO> getAllTenants() {
		List<Tenant> tenants = tenantDao.findAll();
		
		List<TenantResponseDTO> tenantsList = tenants.stream()
				                              .map((tenant) -> {
		TenantResponseDTO dto = mapper.map(tenant, TenantResponseDTO.class);
		
		Room room = tenant.getRoom();
				
		List<Complaint> complaints = complaintDao.findByTenantId(tenant.getId());
        int totalComplaints = complaints.size();
        int activeComplaints = (int) complaints.stream()
                .filter(c -> "IN_PROGRESS".equalsIgnoreCase(c.getComplaintStatus().toString()))
                .count();
      

        Period period = Period.between(tenant.getMoveInDate(), LocalDate.now());
        int tenureInMonths = period.getYears() * 12 + period.getMonths();
        dto.setEmail(tenant.getUser().getEmail());
        dto.setUsername(tenant.getUser().getUsername());
        dto.setRoomNumber(room.getRoomNo());
        dto.setRoomType(room.getRoomType().toString());
        dto.setTenureInMonths(tenureInMonths);
        dto.setTotalComplaints(totalComplaints);
        dto.setActiveComplaints(activeComplaints);

        return dto;
    }).collect(Collectors.toList());		                              
				                              
		return tenantsList;
	}


	@Override
	public ApiResponse updateTenant(Long tenantId, UpdateTenantDTO dto) {
        Tenant tenant = tenantDao.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        User user = tenant.getUser();
        if (dto.getEmail() != null) {
        	user.setEmail(dto.getEmail());
        }

        if (dto.getContactNumber() != null) {
        	tenant.setContactNumber(dto.getContactNumber());
        }
        
        if (dto.getRoomNumber() != null) {
        	 Room newRoom = roomDao.findByRoomNo(dto.getRoomNumber())
                     .orElseThrow(() -> new ResourceNotFoundException("Room not found with number: " + dto.getRoomNumber()));

             tenant.setRoom(newRoom); 
        }

        tenant.setUpdatedAt(LocalDateTime.now());

        tenantDao.save(tenant);
        
        return new ApiResponse("Tenant updated successfully with ID : "+tenant.getId());
    }


	@Override
	public ApiResponse deleteTenant(Long tenantId) {
	    Tenant tenant = tenantDao.findById(tenantId)
	            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + tenantId));

	    if (tenant.isDeleted()) {
	        return new ApiResponse("Tenant is already deleted with ID: " + tenantId);
	    }

	    tenant.setDeleted(true);  // soft delete -> only setting isDeleted to true
	    tenant.setUpdatedAt(LocalDateTime.now());

	    tenantDao.save(tenant);

	    return new ApiResponse("Tenant soft deleted successfully with ID: " + tenantId);
	}


	@Override
	public LoginResponseDTO authenticateUser(String identifier, String password) {
        Optional<User> optionalUser = userDao.findByUsernameOrEmail(identifier, password);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = optionalUser.get();

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Incorrect password");
        }

        return new LoginResponseDTO(user.getUsername());
    }

	
}
