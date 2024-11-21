package com.ems2p0.projections;

import java.math.BigInteger;

public interface UserProjection {

	public BigInteger getEmpId();

	public String getEmpName();

	public String getUserName();

	public String getPassword();

	public String getUpdatedLoginTime();

	public String getUpdatedLogoutTime();
}
