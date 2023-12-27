package com.pikachu.constdu.apiControllers;

import com.google.gson.Gson;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.xfa.XfaForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.pikachu.constdu.dto.GetInfoFromPassportDto;
import com.pikachu.constdu.dto.ResponseDto;
import com.pikachu.constdu.dto.SignUpDto;
import com.pikachu.constdu.dto.test.FormDto;
import com.pikachu.constdu.dto.test.TestSignInDto;
import com.pikachu.constdu.infrastructures.ConfigUtil;
import com.pikachu.constdu.infrastructures.PDFUtil;
import com.pikachu.constdu.models.User;
import com.pikachu.constdu.repositories.RoleRepository;
import com.pikachu.constdu.repositories.UserRepository;
import com.pikachu.constdu.services.EmailService;
import com.pikachu.constdu.services.FileService;
import com.pikachu.constdu.services.OcrService;
import com.pikachu.constdu.services.ZipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@RequestMapping("test")
@RestController
public class TestController {

    @Autowired
    private UserRepository userRepositories;

    @Autowired
    private RoleRepository roleRepositories;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ZipService zipService;

    @Autowired
    private FileService fileService;
    @Autowired
    private OcrService ocrService;
    @PostMapping("/testSignIn")
    public ResponseEntity<String> signIn(@RequestBody TestSignInDto user){
        System.out.println(new Gson().toJson(user));
        User tempUser = userRepositories.findByEmail(user.userName);
        ResponseEntity<String> response;
        ResponseDto responseDto;
        if(Objects.isNull(tempUser)){
            responseDto = new ResponseDto("User Not Exist!");
            response = new ResponseEntity<String>(new Gson().toJson(responseDto), HttpStatus.UNAUTHORIZED);
        }
        else if(!tempUser.getPassword().equals(user.password)){
            responseDto = new ResponseDto("Wrong Password!");
            response = new ResponseEntity<String>(new Gson().toJson(responseDto),HttpStatus.UNAUTHORIZED);
        }else{
            responseDto = new ResponseDto("Success");
            response = new ResponseEntity<String>(new Gson().toJson(responseDto),HttpStatus.OK);
        }
        return response;
    }

    @PostMapping("/testSignUp")
    public ResponseEntity<String> signUp(@RequestBody SignUpDto signUpDto){
        System.out.println(signUpDto.getEmail());
        ResponseEntity<String> response;
        ResponseDto responseDto;
        if(Objects.nonNull(userRepositories.findByEmail(signUpDto.getEmail()))){
            responseDto = new ResponseDto("Email Exist!");
            response = new ResponseEntity<String>(new Gson().toJson(responseDto),HttpStatus.NO_CONTENT);
        }
        else if(Objects.nonNull(userRepositories.findByPhoneNumber(signUpDto.getPhoneNumber()))){
            responseDto = new ResponseDto("Phone Number Exist!");
            response = new ResponseEntity<String>(new Gson().toJson(responseDto),HttpStatus.NO_CONTENT);
        }else{
            String encryptedPassword = signUpDto.getPassword();
            User tempUser = new User();
            tempUser.setEmail(signUpDto.getEmail());
            tempUser.setPassword(encryptedPassword);
            tempUser.setFirstName(signUpDto.getFirstName());
            tempUser.setLastName(signUpDto.getLastName());
            tempUser.setPhoneNumber(signUpDto.getPhoneNumber());
            tempUser.setRole(roleRepositories.findById(1));
            userRepositories.save(tempUser);
            roleRepositories.findById(1).getUsers().add(tempUser);
            responseDto = new ResponseDto("Success");
            response = new ResponseEntity<String>(new Gson().toJson(responseDto),HttpStatus.OK);
        }
        return response;
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

        return ResponseEntity.ok(new ResponseDto("Success", ""));
    }

    @GetMapping("/sendPackage")
    public ResponseEntity sendPackageByEmail(@RequestParam String clientEmail){
        emailService.sendEmail(clientEmail, "Package Delivered", "Hi, this is the package", "Test.txt", new File("./Documents/SourceFiles/a.txt"));
        return ResponseEntity.ok("Sent");
    }

    @GetMapping("/zipFile")
    public ResponseEntity zipFiles(){
        ArrayList<String> sourceFilePaths = new ArrayList<>();
        sourceFilePaths.add("./Documents/SourceFiles/a.txt");
        sourceFilePaths.add("./Documents/SourceFiles/b.txt");
        sourceFilePaths.add("./Documents/SourceFiles/imm5709e.pdf");

        boolean isFinished = zipService.ZipFiles(sourceFilePaths, "./Documents/Targets/Finished.zip");

        return ResponseEntity.ok(String.valueOf(isFinished));
    }

}
