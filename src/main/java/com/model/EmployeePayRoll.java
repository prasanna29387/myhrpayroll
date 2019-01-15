package com.model;

import com.money.Money;
import com.money.MoneyFactory;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Athul Ravindran  on 9/15/2017.
 */
@Data
@Builder
@ToString(exclude = "data")
public class EmployeePayRoll implements Serializable {
	private static final long serialVersionUID = -2141230049167664121L;

	private String clientName;
	private String employeeName;
	private String employeeId;
	private String payRollMonth;
	private String transactionId;
	private String uploadedFileName;
	private String designation;
	private String uan;
	private String insuranceNumber;
	private String aadharNumber;
	private Money basicPay = MoneyFactory.fromString("0");
	private Money dearnessAllow = MoneyFactory.fromString("0");
	private Money allowance = MoneyFactory.fromString("0");
	private int numberOfWorkingDays;
	private double actualWorkingDays;
	private Money earnedBasic = MoneyFactory.fromString("0");
	private Money earnedDearnessAllowance = MoneyFactory.fromString("0");
	private Money earnedAllowance = MoneyFactory.fromString("0");
	private Money earnedBasicPlusDa = MoneyFactory.fromString("0");
	private Money earnedGross = MoneyFactory.fromString("0");
	private Money employeePf = MoneyFactory.fromString("0");
	private Money employeeEsi = MoneyFactory.fromString("0");
	private Money employerEpf = MoneyFactory.fromString("0");
	private Money employerEps = MoneyFactory.fromString("0");
	private Money totalDeductions = MoneyFactory.fromString("0");
	private Money netPay = MoneyFactory.fromString("0");
	private Money wage = MoneyFactory.fromString("0");
	private Money hra = MoneyFactory.fromString("0");
	private Money earnedHRA = MoneyFactory.fromString("0");
	private Money earnedConveyance = MoneyFactory.fromString("0");
	private Money conveyance = MoneyFactory.fromString("0");
	private double otHours;
	private Money otMoney = MoneyFactory.fromString("0");
	private String jobDescriptionId;
	private String pmrpy;


}
