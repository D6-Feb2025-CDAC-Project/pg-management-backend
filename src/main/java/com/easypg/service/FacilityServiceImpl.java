package com.easypg.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easypg.dao.FacilityDao;
import com.easypg.dto.FacilityDTO;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class FacilityServiceImpl implements FacilityService{
	private final FacilityDao facilityDao;
	private final ModelMapper mapper;

	@Override
	public List<FacilityDTO> getAvailableFacilities() {
		List<FacilityDTO> facilities = facilityDao.findByIsDeletedFalse()
		.stream()
		.map(facility -> mapper.map(facility, FacilityDTO.class))
		.toList();
		return facilities;
	}
	
	
}
