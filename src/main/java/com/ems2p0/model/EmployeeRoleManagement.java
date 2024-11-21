package com.ems2p0.model;

import com.ems2p0.enums.OfficialRole;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "emp_role_mgmt")
@NoArgsConstructor
public class EmployeeRoleManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "dept_name")
    private String departmentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "official_role")
    private OfficialRole officialRole;


}
