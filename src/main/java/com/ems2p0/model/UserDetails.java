package com.ems2p0.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.ems2p0.pushnotification.model.Notification;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "user_credential")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDetails implements org.springframework.security.core.userdetails.UserDetails, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "empId")
    private Integer empId;

    @Column(name = "empName")
    private String empName;

    @Column(name = "userName")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "updatedLoginTime")
    private LocalDateTime updatedLoginTime; 

    @Column(name = "updatedLogoutTime")
    private LocalDateTime updatedLogoutTime;
    
    @Column(name = "is_notification_enable", nullable = false)
    private Boolean isNotificationEnable;


    //	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, orphanRemoval = false)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, orphanRemoval = false)
    @JoinColumn(name = "userCredentialEmp_id", referencedColumnName = "empId")
    private List<EmployeePermissionStats> permissionStats;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private EmployeeRoleManagement employeeRoleManagement;

    @OneToOne(fetch = FetchType.LAZY)
    private MultiFactorAuthentication multiFactorAuthentication;
    
    @OneToMany(fetch = FetchType.LAZY)
    private List<Notification> notification;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(employeeRoleManagement.getOfficialRole().name()));
    }
    	@Column(name = "device_token")
    	private String empDeviceToken;
    @Override 
    public String getUsername() {
        return userName;
    }
    
    
}
