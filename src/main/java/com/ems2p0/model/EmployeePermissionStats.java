package com.ems2p0.model;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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

@Entity(name = "emp_permission_statistics")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class EmployeePermissionStats {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "month")
	private String month;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime totalPermission = LocalTime.of(3, 0);

	@JsonFormat(pattern = "HH:mm")
	private LocalTime hoursTaken;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime remainingPermission;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime overduePermission;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "empId")
	private UserDetails userDetails;

    //	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, orphanRemoval = false)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, orphanRemoval = false)
    @JoinColumn(name = "empPermissionStatistics_id", referencedColumnName = "id")
	private List<EmployeePermissionDetails> permissionDetails;

}
