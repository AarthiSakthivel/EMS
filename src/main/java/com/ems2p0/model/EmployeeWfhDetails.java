package com.ems2p0.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.ems2p0.enums.Ems2p0Status;
import com.ems2p0.enums.WorkFromHomeSessionMsg;
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
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Entity(name = "emp_wfh_details")
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class EmployeeWfhDetails {
         

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "no_of_days")
    private double noOfDays;
    
    @Column(name = "requested_session")
    private WorkFromHomeSessionMsg requestedSession;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "start_date")
    private LocalDate startDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "wfh_reason")
    private String reason;

	@Column(name = "month")
	private String month; 

    @Column(name = "wfh_status")
    private Ems2p0Status status;

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

//    @Transient
//    private double daysTaken;

}
