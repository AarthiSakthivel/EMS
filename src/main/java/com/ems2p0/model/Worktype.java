package com.ems2p0.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "work_type")
public class Worktype {

	@Id
	private Integer workTypeId;

	@Column(name = "work_type")
	private String work_type;

	@Column(name = "createdDate")
	private LocalDateTime createdDate;

	@Column(name = "updatedDate")
	private LocalDateTime updatedDate;
}
