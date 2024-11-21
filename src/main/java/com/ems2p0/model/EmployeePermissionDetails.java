package com.ems2p0.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.ems2p0.enums.Ems2p0Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity(name = "emp_permission_details")
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class EmployeePermissionDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "month")
	private String month;

	@JsonFormat(pattern = "dd-MM-yyyy")
	@Column(name = "date")
	private LocalDate date;

	@JsonFormat(pattern = "HH:mm")
	@Column(name = "start_time")
	private LocalTime startTime;

	@JsonFormat(pattern = "HH:mm")
	@Column(name = "end_time")
	private LocalTime endTime;

	@Column(name = "permission_status")
	private Ems2p0Status status;

	@Column(name = "permission_reason")
	private String reason;

	@Column(name = "is_over_due")
	private Boolean isOverDue;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_date_time")
	private LocalDateTime createdDateTime;

	@Column(name = "modified_by")
	private String modifiedBy;

	@Column(name = "modified_date_time")
	private LocalDateTime modifiedDateTime;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "userCredentialEmp_id")
	private UserDetails userDetails;

}
