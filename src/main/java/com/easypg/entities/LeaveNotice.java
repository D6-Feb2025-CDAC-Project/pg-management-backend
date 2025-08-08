package com.easypg.entities;

import java.time.LocalDate;

import com.easypg.enums.NoticeResponseStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="leave_notices")
@ToString(callSuper = true, exclude = {"settlement"})
public class LeaveNotice extends BaseEntity{
	
	@Column(name="move_out_date")
	private LocalDate moveOutDate;
	
	@Column(name="leaveReason", length=100)
	private String reasonOfLeave;
	
	@Column(name="notice_response_status")
	@Enumerated(EnumType.STRING)
	private NoticeResponseStatus noticeResponseStatus;
	
	@Column(name="additional_tenant_notes", length=100)
	private String additionalTenantNotes; 
	
	@Column(name="review_notes", length=200)
	private String reviewNotes;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "tenant_id") // FK column in leave_notice table
	private Tenant tenant;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "settlement_id") // FK column in leave_notice table
	private Settlement settlement;
	
}
