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
@RequestMapping("/admin/notices")

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
	
	/*
	 * Request handling method (REST API end point) 
	 * - desc - Add new notice 
	 * URL - http://host:port/notices 
	 * Method - POST 
	 * Payload - JSON representation of notice 
	 * Resp - success - SC 201 + ApiResp - success mesg
	 */
	@PostMapping
	@Operation(description = "Add New Notice")
	public ResponseEntity<?> addNewNotice(@RequestBody @Valid AddNoticeBoardDTO dto) {
		System.out.println("in add notice " + dto);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(noticeBoardService.addNewNotice(dto));
	}

	/*
	 * REST API end point - 
	 * desc - update notice details by id
	 * URL - http://host:port/notices/{noticeId} 
	 * Method - PUT 
	 * Payload - JSON representation of Notice
	 * Resp - ApiResp
	 */
	@PutMapping("/{noticeId}")
	@Operation(description = "Update notice details (Partial or Complete)")
	public ResponseEntity<?> updateNoticeDetails(@PathVariable Long noticeId,
			@RequestBody @Valid AddNoticeBoardDTO dto) {
		System.out.println("in update notice " + noticeId + " " + dto);
		return ResponseEntity.ok(noticeBoardService.updateNoticeDetails(noticeId, dto));
	}
	
	@DeleteMapping("/{noticeId}")
	@Operation(description = "Soft delete notice")
	public ResponseEntity<?> deleteNotice(@PathVariable @Min(1) Long noticeId) {
		System.out.println("in delete notice " + noticeId);
		return ResponseEntity.ok(noticeBoardService.deleteNotice(noticeId));
	}

	
}