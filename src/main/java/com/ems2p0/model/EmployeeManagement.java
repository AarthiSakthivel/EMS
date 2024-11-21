package com.ems2p0.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Entity(name = "emp_mgmt")
public class EmployeeManagement {

	@Id
	@Column(name = "empId")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long empId;

	@Column(name = "empserialno")
	private String empSerialNo;

	@Column(name = "empname")
	private String empName;

	@Column(name = "userName")
	private String userName;

	@Column(name = "password")
	private String password;

	@Column(name = "gender")
	private String gender;

	@Column(name = "designation")
	private String designation;

	@Column(name = "roletype")
	private String roleType;

	@Column(name = "others")
	private String others;

	@Column(name = "dept")
	private String dept;

	@Column(name = "companyname")
	private String companyName;

	@Column(name = "mothersmaidenname")
	private String mothersMaidenName;

	@Column(name = "dateofbirth")
	private String dateOfBirth;

	@Column(name = "emailid")
	private String emailId;

	@Column(name = "contactno")
	private String contactNo;

	@Column(name = "location")
	private String location;

	@Column(name = "city")
	private String city;

	@Column(name = "empgroup")
	private String empGroup;

	@Column(name = "empsubgroup")
	private String empSubGroup;

	@Column(name = "createddate")
	private LocalDateTime createdDate;

	@Column(name = "updateddate")
	private LocalDateTime updatedDate;

}
