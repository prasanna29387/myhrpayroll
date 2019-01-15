package com.util;

import com.config.Config;
import com.fileupload.service.PayRollPdfGeneratorIText;
import com.model.EmployeePayRoll;
import com.money.MoneyFactory;

import java.util.ArrayList;
import java.util.List;

public class PdfGeneratorUtil {

    public static void main(String args[]) {
        Config.kickOffConfig();
        List<EmployeePayRoll> employeePayRolls = new ArrayList<>();

        EmployeePayRoll employeePayRoll = EmployeePayRoll.builder()
                .clientName("ufs_kone")
                .designation("HR")
                .employeeName("Yamini Shankar")
                .payRollMonth("November 2017")
                .uan("12312313")
                .insuranceNumber("12qeasdasdad")
                .basicPay(MoneyFactory.fromString("1000.19"))
                .dearnessAllow(MoneyFactory.fromString("2000.0"))
                .allowance(MoneyFactory.fromString("100"))
                .numberOfWorkingDays(20)
                .actualWorkingDays(10)
                .earnedBasic(MoneyFactory.fromString("1231"))
                .earnedDearnessAllowance(MoneyFactory.fromString("12312"))
                .earnedAllowance(MoneyFactory.fromString("12312"))
                .earnedGross(MoneyFactory.fromString("1231"))
                .employeePf(MoneyFactory.fromString("123"))
                .employeeEsi(MoneyFactory.fromString("123"))
                .totalDeductions(MoneyFactory.fromString("123123"))
                .netPay(MoneyFactory.fromString("123")).build();

        employeePayRolls.add(employeePayRoll);

        PayRollPdfGeneratorIText payRollPdfGeneratorIText = new PayRollPdfGeneratorIText();
        payRollPdfGeneratorIText.createPayRollPDf(employeePayRolls, "MyCompany");
    }
}
