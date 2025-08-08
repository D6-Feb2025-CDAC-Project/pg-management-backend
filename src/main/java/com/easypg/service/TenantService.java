package com.easypg.service;

import java.util.List;

import com.easypg.dto.AddTenantDTO;
import com.easypg.dto.ApiResponse;
import com.easypg.dto.LoginResponseDTO;
import com.easypg.dto.TenantResponseDTO;
import com.easypg.dto.UpdateTenantDTO;

public interface TenantService {
	public ApiResponse addNewTenant(AddTenantDTO transientEntity);

	public List<TenantResponseDTO> getAllTenants();

	public ApiResponse updateTenant(Long tenantId, UpdateTenantDTO requestDTO);
	
	public ApiResponse deleteTenant(Long tenantId);

}
