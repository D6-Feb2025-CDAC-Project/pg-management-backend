package com.easypg.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.easypg.dto.AddNoticeBoardDTO;
import com.easypg.dto.NoticeBoardRespDTO;
import com.easypg.service.NoticeBoardService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/tenant/notices")

@AllArgsConstructor
@Validated
public class AdminNoticeBoardController {
	
	private final NoticeBoardService noticeBoardService;

	/*
	 * Request handling method (REST API end point) 
	 * URL - http://host:port/notices/ 
	 * Method - GET 
	 * Payload - none 
	 * Resp - in case of empty list - SC204 (NO_CONTENT) 
	 * o.w SC 200 + list of notices -> JSON []
	 */
	@GetMapping
	@Operation(description = "Get all active notices")
	public ResponseEntity<?> getAllNotices() {
		System.out.println("in get all notices");
		List<NoticeBoardRespDTO> notices = noticeBoardService.getAllNotices();
		if (notices.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		return ResponseEntity.ok(notices);
	}

	
}