package com.model;

import com.money.Money;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by xeccwrj on 9/15/2017.
 */
@Data
@Builder
@ToString(exclude = "data")
public class EmployeePayRoll implements Serializable {
	private static final long serialVersionUID = -2141230049167664121L;

	private String clientName;
	private String employeeName;
	private String employeeId;
	private String transactionId;
	private String uploadedFileName;

	private Money basicPay;
	private Money dearnessAllow;
	private Money overTime;
	private Money employeePf;
	private Money netPay;


}
