package com.easypg.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

public class Settlement extends BaseEntity{
	
	@OneToOne
	@JoinColumn(name="leave_notice_id")
	private LeaveNotice leaveNotice;
	
	@Column(name="move_out_date")
	private LocalDate moveOutDate;
	
	@Column(length=100)
	private String reason;
}
