package com.easypg.entities;

import java.time.LocalDate;

import com.easypg.enums.NoticeResponseStatus;

import jakarta.persistence.Column;

public class LeaveNotice extends BaseEntity{
	@Column(name="move_out_date")
	private LocalDate moveOutDate;
	
	@Column(length=100)
	private String reason;
	
	private NoticeResponseStatus noticeResponseStatus;
	
	private String reviewNotes; 
	
	
}
