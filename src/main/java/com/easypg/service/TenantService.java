package com.easypg.service;

import java.util.List;

import com.easypg.dto.AddTenantDTO;
import com.easypg.dto.ApiResponse;
import com.easypg.dto.TenantResponseDTO;

public interface TenantService {
	public ApiResponse addNewTenant(AddTenantDTO transientEntity);

	public List<TenantResponseDTO> getAllTenants();
}
