package com.ems2p0.model;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity(name = "empattendance")
@Data
@Accessors(chain = true)
public class EmployeeAttendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer registerid;

	@Column(name = "empid")
	private String empid;

	@Column(name = "name")
	private String name;

	@JsonFormat(pattern = "dd-MM-yyyy")
	@Column(name = "indate")
	private String indate;

	@JsonFormat(pattern = "HH:mm:ss")
	@Column(name = "intime")
	private LocalTime intime;

	@JsonFormat(pattern = "dd-MM-yyyy")
	@Column(name = "outdate")
	private String outdate;

	@JsonFormat(pattern = "HH:mm:ss")
	@Column(name = "outtime")
	private LocalTime outtime;

//	@Column(name = "localtionIn")
	private String locationIn;

//	@Column(name = "localtionOut")
	private String locationOut;

	@Column(name = "permission")
	private LocalTime permission;

	@Column(name = "overtime")
	private LocalTime overtime;

	@Column(name = "workinghours")
	private Float workinghours;
	
	@Column(name = "totalworkinghours")
	private Float totalworkinghours;

	@Column(name = "workingIn")
	private String workingIn;

}
