package com.ems2p0.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Entity(name = "emp_wfh_statistics")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class EmployeeWfhStats {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "month")
	private String month;

	@Column(name = "totalWfhDays")
	private Integer totalWfhDays;

	@Column(name = "daysTaken")
	private double daysTaken;

	@Column(name = "overdueWfhDays")
	private double overdueWfhDays; 

	@Column(name = "remainingWfhDays")
	private double remainingWfhDays;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "userCredentialEmp_id")
	private UserDetails userDetails;

    //@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, orphanRemoval = false)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, orphanRemoval = false)
    @JoinColumn(name = "empWfhStatistics_id", referencedColumnName = "id")
	private List<EmployeeWfhDetails> wfhDetails;
	
}
