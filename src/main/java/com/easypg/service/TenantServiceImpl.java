package com.easypg.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
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
import com.easypg.dto.TenantResponseDTO;
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
		
		Room room = roomDao.findById(tenant.getRoom().getId()).orElseThrow(() -> new ResourceNotFoundException("No such room exists"));
				
		List<Complaint> complaints = complaintDao.findByTenantId(tenant.getId());
        int totalComplaints = complaints.size();
        int activeComplaints = (int) complaints.stream()
                .filter(c -> "Active".equalsIgnoreCase(c.getComplaintStatus().toString()))
                .count();
        
        LocalDate moveInDate = tenant.getMoveInDate();
        LocalDate currentDate = LocalDate.now();

        Period period = Period.between(moveInDate, currentDate);
        int tenureInMonths = period.getYears() * 12 + period.getMonths();

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
	
	
}
