package com.easypg.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude= {"user","room"})
public class Tenant extends BaseEntity {
	@OneToOne
	@MapsId
	@JoinColumn(name = "id") // This serves as both PK and FK
	private BaseUser user;

	@ManyToOne
	@JoinColumn(name="room_id")
	private Room room;

	@Column(name = "move_in_date", nullable = false)
	private LocalDate moveInDate;

	@Column(name = "contact_number", nullable = false)
	private String contactNumber;

	@Column(nullable = false)
	private String gender;
	
//	@OneToOne(mappedBy = "tenant")
//	private LeaveNotice leaveNotice;
	
}