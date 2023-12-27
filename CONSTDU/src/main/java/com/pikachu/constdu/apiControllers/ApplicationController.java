package com.pikachu.constdu.apiControllers;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.xfa.XfaForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.pikachu.constdu.dto.ResponseDto;
import com.pikachu.constdu.dto.test.FormDto;
import com.pikachu.constdu.infrastructures.ConfigUtil;
import com.pikachu.constdu.infrastructures.PDFUtil;
import com.pikachu.constdu.models.User;
import com.pikachu.constdu.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Yuanfu Tian
 * Date: 2023-11-14
 */
@RestController
@RequestMapping("/application")
public class ApplicationController {

    private UserService userService;

    private ZipService zipService;

    private EmailService emailService;

    private FileService fileService;

    private ApplicationDateService applicationDateService;

    public ApplicationController(UserService userService, ZipService zipService, EmailService emailService, FileService fileService, ApplicationDateService applicationDateService) {
        this.userService = userService;
        this.zipService = zipService;
        this.emailService = emailService;
        this.fileService = fileService;
        this.applicationDateService = applicationDateService;
    }

    @PostMapping("/getPackage")
    public ResponseEntity fillOutFormAndDownload(@RequestBody FormDto form, HttpServletResponse response){
        PDFUtil pdfUtil = new PDFUtil();
        String now = String.valueOf(new Date().getTime());
        String sourceFile = "./Documents/SourceFiles/imm5709e.pdf";
        String destFile = "./Documents/Targets/imm5709e(Finished)_" + form.getGivenName() + "_" + form.getFamilyName() + "_" + now + ".pdf";
        String destZipFile = "./Documents/Targets/" + form.getGivenName() + "_" + form.getFamilyName() + "_Package_" + now + ".zip";
        PdfDocument doc = pdfUtil.getStampingPdfFile(sourceFile, destFile);

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(doc, true);
        XfaForm xfaForm = acroForm.getXfaForm();

        doc = pdfUtil.writeDataToField("FamilyName", form.getFamilyName(), xfaForm, doc);
        xfaForm = pdfUtil.getXfaFormFromPdfDocument(doc);

        doc = pdfUtil.writeDataToField("GivenName", form.getGivenName(), xfaForm, doc);
        xfaForm = pdfUtil.getXfaFormFromPdfDocument(doc);

        if (form.getSex().equals("Male")) {
            doc = pdfUtil.writeDataToField("Sex", "Male", xfaForm, doc);
            xfaForm = pdfUtil.getXfaFormFromPdfDocument(doc);
        } else if (form.getSex().equals("Female")) {
            doc = pdfUtil.writeDataToField("Sex", "Female", xfaForm, doc);
            xfaForm = pdfUtil.getXfaFormFromPdfDocument(doc);
        } else if (form.getSex().equals("Unknown")) {
            doc = pdfUtil.writeDataToField("Sex", "Unknow", xfaForm, doc);
            xfaForm = pdfUtil.getXfaFormFromPdfDocument(doc);
        } else if (form.getSex().equals("AnothorGender")) {
            doc = pdfUtil.writeDataToField("Sex", "Unspecified", xfaForm, doc);
            xfaForm = pdfUtil.getXfaFormFromPdfDocument(doc);
        }

        Calendar dobCalendar = pdfUtil.getDateFromString(form.getDob());

        doc = pdfUtil.writeDataToField("form1[0].Page1[0].PersonalDetails[0].q3-4-5[0].dob[0].DOBYear[0]", String.valueOf(dobCalendar.get(Calendar.YEAR)), xfaForm, doc);
        xfaForm = pdfUtil.getXfaFormFromPdfDocument(doc);

        doc = pdfUtil.writeDataToField("DOBMonth", String.valueOf(dobCalendar.get(Calendar.MONTH) + 1), xfaForm, doc);
        xfaForm = pdfUtil.getXfaFormFromPdfDocument(doc);

        doc = pdfUtil.writeDataToField("DOBDay", String.valueOf(dobCalendar.get(Calendar.DAY_OF_MONTH)), xfaForm, doc);
        xfaForm = pdfUtil.getXfaFormFromPdfDocument(doc);

        doc = pdfUtil.writeDataToField("Email", form.getEmail(), xfaForm, doc);
        xfaForm = pdfUtil.getXfaFormFromPdfDocument(doc);

        doc = pdfUtil.writeDataToField("PlaceBirthCity", form.getCityOfBirth(), xfaForm, doc);
        xfaForm = pdfUtil.getXfaFormFromPdfDocument(doc);

        doc = pdfUtil.writeDataToField("PlaceBirthCountry", new ConfigUtil().getCountryOfBirthCodeByContryNameOfBirth(form.getCountryOfBirth()), xfaForm, doc);
        xfaForm = pdfUtil.getXfaFormFromPdfDocument(doc);

        doc = pdfUtil.writeDataToField("PassportNum", form.getPassportNumber(), xfaForm, doc);
        xfaForm = pdfUtil.getXfaFormFromPdfDocument(doc);

        doc.close();

        ArrayList<String> sourceFiles = new ArrayList<>();
        sourceFiles.add(destFile);
        boolean isZipped = zipService.ZipFiles(sourceFiles, destZipFile);

        boolean isEmailSent = false;
        if (isZipped) {
            String subject = form.getGivenName() + "'s Package Is Ready to Submit!";
            String content = "Hi " + form.getGivenName() + ", your package is ready. \nPlease follow the instructions to submit you application.";
            isEmailSent = emailService.sendEmail(form.getEmail(), subject, content, new File(destZipFile).getName(), new File(destZipFile));
        }

        fileService.deleteFile(new String[]{destZipFile, destFile});
        if (!isEmailSent) {
            return ResponseEntity.badRequest().body(new ResponseDto("Email is invalid.", ""));
        }

        // save the application delivery date
        applicationDateService.registerApplicationDateAfterGettingPackage(userService.getUserByToken());

        return ResponseEntity.ok(new ResponseDto("Success", ""));
    }
}
