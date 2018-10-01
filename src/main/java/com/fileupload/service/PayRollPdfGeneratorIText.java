package com.fileupload.service;

import com.config.Config;
import com.itext7.CustomDashedLineSeparator;
import com.itext7.NumbersToWords;
import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.Table;
import com.model.EmployeePayRoll;
import com.util.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Athul Ravindran on 9/18/2017.
 */
@Slf4j
@Service
public class PayRollPdfGeneratorIText {


    public static final String ACTUAL_WORKING_DAYS = "Actual Working Days";
    protected static final String FAX_NAS_BACKUP_FOLDER_KEY = "fax.nas.backup.folder";
    protected static final String PDF_EXTENSION = ".pdf";
    public static final String PAY_SLIP = "_PaySlip";
    public static final String EMPLOYER_COPY = "Employer Copy";
    public static final String EMPLOYEE_COPY = "Employee Copy";
    public static final String DISCLAIMER_MESSAGE = "*** This is a confidential document and should not be disclosed. If you are not the intented person, you should immediately destory this document along with any copies.";
    public static final String EMPLOYEE_INFORMATION = "Employee Information";
    public static final String EARNINGS = "Earnings";
    public static final String DEDUCTIONS = "Deductions";
    public static final String EMPLOYEE_NAME = "Employee Name";
    public static final String DESIGNATION = "Designation";
    public static final String UAN = "UAN";
    public static final String ESI_NUMBER = "ESI Number";
    public static final String EPF = "EPF";
    public static final String ESI = "ESI";
    public static final String IT = "IT";
    public static final String NA = "N/A";
    public static final String PT = "PT";
    public static final String BLANK = "";
    public static final String TOTAL_NO_OF_WORKING_DAYS = "Total No Of Working Days";
    public static final String DAILY_WAGES = "Daily Wages";
    public static final String BASIC = "Basic";
    public static final String EARNED_BASIC = "Earned Basic";
    public static final String DEARNESS_ALLOWANCE = "Dearness Allowance";
    public static final String HRA = "HRA";
    public static final String EARNED_HRA = "Earned HRA";
    public static final String CONVEYANCE = "Conveyance";
    public static final String EARNED_CONVEYANCE = "Earned Conveyance";
    public static final String OT_HOURS = "OT Hours";
    public static final String EARNED_OT = "OT Earned";
    public static final String EARNED_DEARNESS_ALLOWANCE = "Earned Dearness Allowance";
    public static final String OTHER_ALLOWANCE = "Other Allowance";
    public static final String EARNED_OTHER_ALLOWANCE = "Earned Other Allowance";
    public static final String TOTAL_EARNINGS = "Total Earnings";
    public static final String PAY_SLIP_FOR_THE_MONTH_OF = "PAY SLIP FOR THE MONTH OF ";
    public static final String ZIP = ".zip";
    public static final String PAY_SLIP_MATSER_PDF = "_pay_slip_matser.pdf";


    public void createPayRollPDf(List<EmployeePayRoll> employeePayRollList, String originalFileName) {
        createPayCheck(employeePayRollList, originalFileName);
    }

    private void createPayCheck(List<EmployeePayRoll> employeePayRollList, String originalFileName) {

        try {
            List<PdfDocument> pdfDocumentList = new ArrayList<>();
            for (EmployeePayRoll employeePayRoll : employeePayRollList) {
                if (employeePayRoll.getActualWorkingDays() > 0) {
                    String finalFileName = employeePayRoll.getEmployeeName().concat(PAY_SLIP).concat(PDF_EXTENSION);
                    pdfDocumentList.add(createPdfFile(employeePayRoll, finalFileName));
                }
            }

            mergeToMasterPdf(originalFileName);
            //zipItUp(originalFileName);

        } catch (Exception e) {
            log.error("Error in merging pdfs {}", e);
        }
    }

    private PdfDocument createPdfFile(EmployeePayRoll employeePayRoll, String finalFileName) {

        PdfDocument pdfDocument = null;
        Document payCheckDoc = null;
        try {

            String fileDestination = Config.getProperty(FAX_NAS_BACKUP_FOLDER_KEY).concat("/").
                    concat(finalFileName);

            pdfDocument = new PdfDocument(new PdfWriter(fileDestination));
            pdfDocument.addNewPage();

            PageSize pageSize = PageSize.A4;
            payCheckDoc = new Document(pdfDocument, pageSize);

            addBorder(pdfDocument);
            createCompanyHeaderInfo(payCheckDoc, employeePayRoll);
            addLineSeperator(payCheckDoc);
            addEmployerCopyHeader(payCheckDoc);

            getEmployeeInfoPara(payCheckDoc);
            getEmployeeInfoTable(employeePayRoll, payCheckDoc);
            getEarningsPara(payCheckDoc);
            getEarnings(employeePayRoll, payCheckDoc);

            getDeductionsPara(payCheckDoc);
            getDeductionsTable(employeePayRoll, payCheckDoc);

            addBlankPara(payCheckDoc);
            addSignature(payCheckDoc);
            addDashSeperator(payCheckDoc);
            addEmployeeCopyHeader(payCheckDoc);

            getEmployeeInfoPara(payCheckDoc);
            getEmployeeInfoTable(employeePayRoll, payCheckDoc);
            getEarningsPara(payCheckDoc);
            getEarnings(employeePayRoll, payCheckDoc);
            getDeductionsPara(payCheckDoc);
            getDeductionsTable(employeePayRoll, payCheckDoc);
            addBlankPara(payCheckDoc);
            createFooter(payCheckDoc);

        } catch (Exception e) {
            log.error("Error in creating a pdf {}", e);
        } finally {
            payCheckDoc.close();
            pdfDocument.close();
        }
        return pdfDocument;
    }

    private void getEarnings(EmployeePayRoll employeePayRoll, Document payCheckDoc) {
        if(Config.getProperty(employeePayRoll.getClientName().toLowerCase()+".earnings").equalsIgnoreCase("format1"))
        {
            getEarningsTableFormat1(employeePayRoll,payCheckDoc);
        }
        else if(Config.getProperty(employeePayRoll.getClientName().toLowerCase()+".earnings").equalsIgnoreCase("format2"))
        {
            getEarningsTableFormat2(employeePayRoll,payCheckDoc);

        }
        else if(Config.getProperty(employeePayRoll.getClientName().toLowerCase()+".earnings").equalsIgnoreCase("format3"))
        {
            getEarningsTableFormat3(employeePayRoll,payCheckDoc);

        }
    }

    private void addSignature(Document payCheckDoc) {

        try {
            Paragraph clientName = new Paragraph();
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
            clientName.setFont(font);
            clientName.setFontSize(9);
            clientName.setItalic();
            clientName.add(new Tab()).add(new Tab()).add(new Tab()).add(new Tab()).add("Date : " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).add(new Tab()).add("Signature : ");
            payCheckDoc.add(clientName);
        } catch (IOException e) {
            log.error("Error in creating signature area {}", e);
        }

    }

    private void addBlankPara(Document payCheckDoc) {
        Paragraph blankPara = new Paragraph();
        blankPara.setFontSize(9);
        blankPara.add("");
        payCheckDoc.add(blankPara);
        payCheckDoc.add(blankPara);
    }

    private void addEmployerCopyHeader(Document payCheckDoc) {
        try {
            Paragraph employerNamePara = new Paragraph();
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
            employerNamePara.setFont(font);
            employerNamePara.setFontSize(9);
            employerNamePara.setItalic();
            employerNamePara.add(EMPLOYER_COPY);
            payCheckDoc.add(employerNamePara);
        } catch (IOException e) {
            log.error("Error in creating employer header area {}", e);
        }
    }


    private void addEmployeeCopyHeader(Document payCheckDoc) {
        try {
            Paragraph employeeNamePara = new Paragraph();
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
            employeeNamePara.setFont(font);
            employeeNamePara.setFontSize(9);
            employeeNamePara.setItalic();
            employeeNamePara.add(EMPLOYEE_COPY);
            payCheckDoc.add(employeeNamePara);
        } catch (IOException e) {
            log.error("Error in creating employee header area {}", e);
        }
    }

    private void createFooter(Document payCheckDoc) {
        try {
            Paragraph footer = new Paragraph();
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
            footer.setFont(font);
            footer.setFontSize(6);
            footer.setItalic();
            footer.add(DISCLAIMER_MESSAGE);
            payCheckDoc.add(footer);
        } catch (IOException e) {
            log.error("Error in creating footer area {}", e);
        }

    }

    private void getEmployeeInfoPara(Document payCheckDoc) {
        try {
            Paragraph personalInfoPara = new Paragraph();
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
            personalInfoPara.setFont(font);
            personalInfoPara.setFontSize(9);
            personalInfoPara.add(EMPLOYEE_INFORMATION);
            payCheckDoc.add(personalInfoPara);
        } catch (IOException e) {
            log.error("Error in creating employee information area {}", e);
        }
    }

    private void getEarningsPara(Document payCheckDoc) {
        try {
            Paragraph earningsPara = new Paragraph();
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
            earningsPara.setFont(font);
            earningsPara.add(EARNINGS);
            earningsPara.setFontSize(9);
            payCheckDoc.add(earningsPara);
        } catch (IOException e) {
            log.error("Error in creating earnings area {}", e);
        }
    }

    private void getDeductionsPara(Document payCheckDoc) {
        try {
            Paragraph earningsPara = new Paragraph();
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
            earningsPara.setFont(font);
            earningsPara.add(DEDUCTIONS);
            earningsPara.setFontSize(9);
            payCheckDoc.add(earningsPara);
        } catch (IOException e) {
            log.error("Error in deductions earnings area {}", e);
        }
    }


    private void getEmployeeInfoTable(EmployeePayRoll employeePayRoll, Document payCheckDoc) {
        Table table = new Table(4);
        table.setFontSize(8);
        table.addCell(EMPLOYEE_NAME);
        table.addCell(employeePayRoll.getEmployeeName());
        table.addCell(DESIGNATION);
        table.addCell(employeePayRoll.getDesignation());
        table.addCell(UAN);
        table.addCell(employeePayRoll.getUan());
        table.addCell(ESI_NUMBER);
        table.addCell(employeePayRoll.getInsuranceNumber());
        payCheckDoc.add(table);
    }

    private void getDeductionsTable(EmployeePayRoll employeePayRoll, Document payCheckDoc) {
        Table table = new Table(4);
        table.setFontSize(8);
        table.addCell(EPF);
        table.addCell(employeePayRoll.getEmployeePf().toString());
        table.addCell(ESI);
        table.addCell(employeePayRoll.getEmployeeEsi().toString());
        table.addCell(IT);
        table.addCell(NA);
        table.addCell(PT);
        table.addCell(NA);
        payCheckDoc.add(table);
        table = new Table(1);
        table.setFontSize(10);
        table.addCell(BLANK);
        table.addCell("Net Pay:  Rs " + employeePayRoll.getNetPay() + " (Rupees " + NumbersToWords.convert((long) (employeePayRoll.getNetPay().truncate(0).toDouble())) + " Only )");
        payCheckDoc.add(table);
    }

    private void getEarningsTableFormat2(EmployeePayRoll employeePayRoll, Document payCheckDoc) {
        Table table = new Table(4);
        table.setFontSize(8);
        table.addCell(TOTAL_NO_OF_WORKING_DAYS);
        table.addCell(String.valueOf(employeePayRoll.getNumberOfWorkingDays()));
        table.addCell(ACTUAL_WORKING_DAYS);
        table.addCell(String.valueOf(employeePayRoll.getActualWorkingDays()));
        table.addCell(DAILY_WAGES);
        table.addCell(employeePayRoll.getWage().toString());
        table.addCell(BLANK);
        table.addCell(BLANK);
        payCheckDoc.add(table);
        table = new Table(4);
        table.setFontSize(8);
        table.addCell(BASIC);
        table.addCell(employeePayRoll.getBasicPay().toString());
        table.addCell(EARNED_BASIC);
        table.addCell(employeePayRoll.getEarnedBasic().toString());
        table.addCell(DEARNESS_ALLOWANCE);
        table.addCell(employeePayRoll.getDearnessAllow().toString());
        table.addCell(EARNED_DEARNESS_ALLOWANCE);
        table.addCell(employeePayRoll.getEarnedDearnessAllowance().toString());
        table.addCell(OTHER_ALLOWANCE);
        table.addCell(employeePayRoll.getAllowance().toString());
        table.addCell(EARNED_OTHER_ALLOWANCE);
        table.addCell(employeePayRoll.getEarnedAllowance().toString());
        payCheckDoc.add(table);
        table = new Table(2);
        table.setFontSize(8);
        table.addCell(TOTAL_EARNINGS);
        table.addCell(employeePayRoll.getEarnedGross().toString());
        payCheckDoc.add(table);
    }

    private void getEarningsTableFormat1(EmployeePayRoll employeePayRoll, Document payCheckDoc) {
        Table table = new Table(4);
        table.setFontSize(8);
        table.addCell(TOTAL_NO_OF_WORKING_DAYS);
        table.addCell(String.valueOf(employeePayRoll.getNumberOfWorkingDays()));
        table.addCell(ACTUAL_WORKING_DAYS);
        table.addCell(String.valueOf(employeePayRoll.getActualWorkingDays()));
        payCheckDoc.add(table);
        table = new Table(4);
        table.setFontSize(8);
        table.addCell(BASIC);
        table.addCell(employeePayRoll.getBasicPay().toString());
        table.addCell(EARNED_BASIC);
        table.addCell(employeePayRoll.getEarnedBasic().toString());
        table.addCell(DEARNESS_ALLOWANCE);
        table.addCell(employeePayRoll.getDearnessAllow().toString());
        table.addCell(EARNED_DEARNESS_ALLOWANCE);
        table.addCell(employeePayRoll.getEarnedDearnessAllowance().toString());
        table.addCell(OTHER_ALLOWANCE);
        table.addCell(employeePayRoll.getAllowance().toString());
        table.addCell(EARNED_OTHER_ALLOWANCE);
        table.addCell(employeePayRoll.getEarnedAllowance().toString());
        payCheckDoc.add(table);
        table = new Table(2);
        table.setFontSize(8);
        table.addCell(TOTAL_EARNINGS);
        table.addCell(employeePayRoll.getEarnedGross().toString());
        payCheckDoc.add(table);
    }

    private void getEarningsTableFormat3(EmployeePayRoll employeePayRoll, Document payCheckDoc) {
        Table table = new Table(4);
        table.setFontSize(8);
        table.addCell(TOTAL_NO_OF_WORKING_DAYS);
        table.addCell(String.valueOf(employeePayRoll.getNumberOfWorkingDays()));
        table.addCell(ACTUAL_WORKING_DAYS);
        table.addCell(String.valueOf(employeePayRoll.getActualWorkingDays()));
        payCheckDoc.add(table);
        table = new Table(4);
        table.setFontSize(8);
        table.addCell(BASIC);
        table.addCell(employeePayRoll.getBasicPay().toString());
        table.addCell(EARNED_BASIC);
        table.addCell(employeePayRoll.getEarnedBasic().toString());
        table.addCell(DEARNESS_ALLOWANCE);
        table.addCell(employeePayRoll.getDearnessAllow().toString());
        table.addCell(EARNED_DEARNESS_ALLOWANCE);
        table.addCell(employeePayRoll.getEarnedDearnessAllowance().toString());
        table.addCell(HRA);
        table.addCell(employeePayRoll.getHra().toString());
        table.addCell(EARNED_HRA);
        table.addCell(employeePayRoll.getEarnedHRA().toString());
        table.addCell(CONVEYANCE);
        table.addCell(employeePayRoll.getConveyance().toString());
        table.addCell(EARNED_CONVEYANCE);
        table.addCell(employeePayRoll.getEarnedConveyance().toString());
        table.addCell(OT_HOURS);
        table.addCell(String.valueOf(employeePayRoll.getOtHours()));
        table.addCell(EARNED_OT);
        table.addCell(employeePayRoll.getOtMoney().toString());
        table.addCell(OTHER_ALLOWANCE);
        table.addCell(employeePayRoll.getAllowance().toString());
        table.addCell(EARNED_OTHER_ALLOWANCE);
        table.addCell(employeePayRoll.getEarnedAllowance().toString());
        payCheckDoc.add(table);
        table = new Table(2);
        table.setFontSize(8);
        table.addCell(TOTAL_EARNINGS);
        table.addCell(employeePayRoll.getEarnedGross().toString());
        payCheckDoc.add(table);
    }

    private void addLineSeperator(Document payCheckDoc) {
        CustomDashedLineSeparator separator = new CustomDashedLineSeparator();
        separator.setDash(10);
        separator.setGap(0);
        separator.setLineWidth(1);
        payCheckDoc.add(new LineSeparator(separator));
    }

    private void addDashSeperator(Document payCheckDoc) {
        CustomDashedLineSeparator separator = new CustomDashedLineSeparator();
        separator.setDash(10);
        separator.setGap(2);
        separator.setLineWidth(1);
        payCheckDoc.add(new LineSeparator(separator));
    }

    private void addBorder(PdfDocument pdfDocument) {
        float width = pdfDocument.getDefaultPageSize().getWidth();
        float height = pdfDocument.getDefaultPageSize().getHeight();
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getFirstPage());
        canvas.rectangle(20, 20, width - 40, height - 40);
        canvas.setStrokeColor(Color.BLACK);
        canvas.stroke();
    }

    private void createCompanyHeaderInfo(Document document, EmployeePayRoll employeePayRoll) {

        try {
            Paragraph clientName = new Paragraph();
            PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
            clientName.setFont(font);
            clientName.setFontSize(10);
            clientName.add(Config.getProperty(employeePayRoll.getClientName().toLowerCase() + ".name")).add(new Tab()).add(new Tab()).add(PAY_SLIP_FOR_THE_MONTH_OF + employeePayRoll.getPayRollMonth().toUpperCase());
            document.add(clientName);
            font = PdfFontFactory.createFont(FontConstants.TIMES_ITALIC);
            clientName = new Paragraph();
            clientName.setFont(font);
            clientName.setFontSize(7);
            clientName.add(Config.getProperty(employeePayRoll.getClientName().toLowerCase() + ".address")).add(new Tab());
            document.add(clientName);

        } catch (IOException e) {
            log.error("Error in creating company header area {}", e);
        }
    }

    private void zipItUp(String originalFileName) {

        String zipFileName = Config.getProperty(FAX_NAS_BACKUP_FOLDER_KEY).concat("/").concat(FileHelper.getBaseNameFromFileName(originalFileName)).concat(ZIP);
        byte[] buffer = new byte[1024];

        try {

            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);

            log.info("Output to Zip : " + zipFileName);

            File[] sourceFiles = new File(Config.getProperty(FAX_NAS_BACKUP_FOLDER_KEY)).listFiles();

            for (File file : sourceFiles) {
                if (!file.getName().endsWith(".zip")) {
                    writeToZip(file.getAbsolutePath(), zos);
                }
            }

            zos.close();
            fos.close();
        } catch (IOException ex) {
            log.error("Error in zipping up {}", ex);
        }
    }

    private void writeToZip(String path, ZipOutputStream zipStream) {
        FileInputStream fis = null;
        File aFile = null;
        try {
            System.out.println("Writing file : '" + path + "' to zip file");
            aFile = new File(path);
            fis = new FileInputStream(aFile);
            ZipEntry zipEntry = new ZipEntry(path);
            zipStream.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipStream.write(bytes, 0, length);
            }
            zipStream.closeEntry();
            fis.close();
        } catch (Exception e) {
            log.error("Error in creating zip files {}", e);
        } finally {
            aFile.delete();
        }

    }

    private void mergeToMasterPdf(String originalFileName) throws FileNotFoundException {
        PdfDocument pdfDocument = null;
        try {

            pdfDocument = new PdfDocument(new PdfWriter(Config.getProperty(FAX_NAS_BACKUP_FOLDER_KEY).concat("/").concat(FileHelper.getBaseNameFromFileName(originalFileName))
                    .concat(PAY_SLIP_MATSER_PDF)));
            File backUpDirectory = new File(Config.getProperty(FAX_NAS_BACKUP_FOLDER_KEY));
            if (backUpDirectory.isDirectory() && backUpDirectory.exists()) {
                File[] files = new File(Config.getProperty(FAX_NAS_BACKUP_FOLDER_KEY)).listFiles();
                for (File file : files) {
                    if (file.getName().endsWith(PDF_EXTENSION) && !file.getName().contains(PAY_SLIP_MATSER_PDF)) {
                        PdfDocument currentDoc = new PdfDocument(new PdfReader(file.getAbsolutePath()));
                        currentDoc.copyPagesTo(1, 1, pdfDocument, new PdfPageFormCopier());
                        currentDoc.close();
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error in merging pdfs {}", e);
        } finally {
            pdfDocument.close();
        }
    }

}

