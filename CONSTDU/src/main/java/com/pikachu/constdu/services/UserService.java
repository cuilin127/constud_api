package com.pikachu.constdu.services;

import com.google.gson.Gson;
import com.pikachu.constdu.dto.*;
import com.pikachu.constdu.infrastructures.AESEncryption;
import com.pikachu.constdu.models.Passport;
import com.pikachu.constdu.models.User;
import com.pikachu.constdu.models.VerificationCode;
import com.pikachu.constdu.repositories.PassportRepository;
import com.pikachu.constdu.repositories.RoleRepository;
import com.pikachu.constdu.repositories.UserRepository;
import com.pikachu.constdu.repositories.VerificationCodeRepository;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepositories;
    private final RoleRepository roleRepositories;
    private final PassportRepository passportRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final TokenService tokenService;
    private final EmailService emailService;
    public UserService(TokenService _tokenService, UserRepository _userRepositories,RoleRepository _roleRepositories,
                       VerificationCodeRepository _verificationCodeRepository, EmailService _emailService,
                       PassportRepository _passportRepository) {
        this.tokenService = _tokenService;
        this.roleRepositories = _roleRepositories;
        this.userRepositories = _userRepositories;
        this.verificationCodeRepository = _verificationCodeRepository;
        this.emailService = _emailService;
        this.passportRepository = _passportRepository;
    }

    @Transactional
    public ResponseEntity<ResponseDto> signUp(SignUpDto signUpDto) {
        try{
            //check if user exist;
            if (Objects.nonNull(userRepositories.findByEmail(signUpDto.getEmail()))) {
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(400)
                                .message("User Already Exist!")
                                .build()
                );
            }
            if (Objects.nonNull(userRepositories.findByPhoneNumber(signUpDto.getPhoneNumber()))) {
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(400)
                                .message("Phone Number Already Exist!")
                                .build()
                );
            }
            //save data
            User tempUser = new User();

            tempUser.setEmail(signUpDto.getEmail());
            tempUser.setPassword(signUpDto.getPassword());

            //Encode password
            tempUser.setPassword(tempUser.decodeUserPassword());

            //Setup user profile
            tempUser.setFirstName(signUpDto.getFirstName());
            tempUser.setLastName(signUpDto.getLastName());
            tempUser.setPhoneNumber(signUpDto.getPhoneNumber());
            tempUser.setRole(roleRepositories.findById(1));

            //Update DB
            userRepositories.save(tempUser);
            roleRepositories.findById(1).getUsers().add(tempUser);

            //Send greeting message
            String emailBody = generateGreetingMessage(tempUser);
            String emailSubject = "Welcome to CONSTUD";
            boolean sendEmailResult = emailService.sendEmail(tempUser.getEmail(),emailSubject,emailBody);
            if(!sendEmailResult){
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(304)
                                .message("Fail to send verification code by email.")
                                .build()
                );
            }

            return ResponseEntity.ok(new ResponseDto());
        }catch(Exception ex){
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .status(304)
                            .message(ex.getMessage())
                            .build()
            );
        }

    }
    @Transactional
    public ResponseEntity<ResponseDto> signIn(Authentication authentication) {
        try{
            User tempUser = userRepositories.findByEmail(authentication.getName());
            if (Objects.isNull(tempUser)) {
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(304)
                                .message("Internal service Error!")
                                .build()
                );
            }

            UserDto userDto = UserDto.builder()
                    .id(tempUser.getId())
                    .firstName(tempUser.getFirstName())
                    .lastName(tempUser.getLastName())
                    .email(tempUser.getEmail())
                    .phoneNumber(tempUser.getPhoneNumber())
                    .role(tempUser.getRole().getRoleName())
                    .build();
            HashMap<String, Object> res = new HashMap<String, Object>();
            res.put("user", userDto);
            res.put("jwt", tokenService.generateToken(authentication));

            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .data(res)
                            .build()
            );

        }catch(Exception ex){
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .status(304)
                            .message(ex.getMessage())
                            .build()
            );
        }

    }
    @Transactional
    public ResponseEntity<ResponseDto> updatePasswordWhenUserLoggedIn(Authentication authentication, UpdatePasswordWhenUserLoggedInDto updatePasswordWhenUserLoggedInDto) {
        try{
            User tempUser = userRepositories.findByEmail(authentication.getName());
            if (Objects.isNull(tempUser)) {
                ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(304)
                                .message("Internal service Error!")
                                .build()
                );
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if(encoder.matches(updatePasswordWhenUserLoggedInDto.getOldPassword(), tempUser.getPassword())){
                tempUser.setPassword(encoder.encode(updatePasswordWhenUserLoggedInDto.getNewPassword()));
                userRepositories.save(tempUser);
                return ResponseEntity.ok(new ResponseDto());
            }else{
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(400)
                                .message("Wrong previous password!")
                                .build()
                );
            }
        }catch(Exception ex){
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .status(304)
                            .message(ex.getMessage())
                            .build()
            );
        }
    }
    @Transactional
    public ResponseEntity<ResponseDto> updatePasswordWithoutLoggedIn(UpdatePasswordWithoutLoggedInDto updatePasswordWithoutLoggedInDto) {
        try{
            //Define sort by createTime
            Sort sort = Sort.by(Sort.Direction.DESC, "createTime");

            //Get all verification code associate with specific email
            ArrayList<VerificationCode> codes = (ArrayList<VerificationCode>) verificationCodeRepository.findByEmail(updatePasswordWithoutLoggedInDto.getEmail(),sort);
            if(!codes.isEmpty()){
                //Get the first one from List which is the newest one
                VerificationCode newestCode = codes.get(0);

                //Check if code expired
                Date currentTime = new Date();
                // Find the difference in milliseconds
                long differenceInMilliSeconds = Math.abs(currentTime.getTime() - newestCode.getCreateTime().getTime());

                // Convert the difference to seconds
                long differenceInSeconds = differenceInMilliSeconds / 1000;
                if(differenceInSeconds>60){
                    //Code expired
                    return ResponseEntity.ok(
                            ResponseDto.builder()
                                    .status(408)
                                    .message("Code expired.")
                                    .build()
                    );
                }
                //Verify code
                if(!newestCode.getCode().equals(updatePasswordWithoutLoggedInDto.getVerificationCode())){
                    return ResponseEntity.ok(
                            ResponseDto.builder()
                                    .status(400)
                                    .message("Wrong verification code.")
                                    .build()
                    );
                }

                //Actual update password
                User tempUser = userRepositories.findByEmail(updatePasswordWithoutLoggedInDto.getEmail());

                //Encode  password
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                tempUser.setPassword(encoder.encode(updatePasswordWithoutLoggedInDto.getNewPassword()));

                //Update in DB
                userRepositories.save(tempUser);
                return ResponseEntity.ok(new ResponseDto());
            }else{
                //No verification code found
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(400)
                                .message("Wrong verification code.")
                                .build()
                );
            }
        }catch(Exception ex){
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .status(304)
                            .message(ex.getMessage())
                            .build()
            );
        }
    }

    @Transactional
    public ResponseEntity<ResponseDto> addPassportToUser(Authentication authentication,PassportInfoDto passportInfoDto){
        try{
            Passport tempPassport = Passport.builder()
                    .name(AESEncryption.encrypt(passportInfoDto.getName()))
                    .surname(AESEncryption.encrypt(passportInfoDto.getSurname()))
                    .sex(AESEncryption.encrypt(passportInfoDto.getSex()))
                    .dateOfBirth(AESEncryption.encrypt(passportInfoDto.getDateOfBirth()))
                    .nationality(AESEncryption.encrypt(passportInfoDto.getNationality()))
                    .passportType(AESEncryption.encrypt(passportInfoDto.getPassportType()))
                    .passportNumber(AESEncryption.encrypt(passportInfoDto.getPassportNumber()))
                    .issuingCountry(AESEncryption.encrypt(passportInfoDto.getIssuingCountry()))
                    .expirationDate(AESEncryption.encrypt(passportInfoDto.getExpirationDate()))
                    .personalNumber(AESEncryption.encrypt(passportInfoDto.getPersonalNumber()))
                    .build();
            User tempUser = userRepositories.findByEmail(authentication.getName());
            if (Objects.isNull(tempUser)) {
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(304)
                                .message("Internal service Error!")
                                .build()
                );
            }
            tempPassport.setCustomer(tempUser);
            tempUser.setPassport(tempPassport);
            //Update In DB
            userRepositories.save(tempUser);
            passportRepository.save(tempPassport);
            return ResponseEntity.ok(new ResponseDto());

        }catch(Exception ex){
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .status(304)
                            .message(ex.getMessage())
                            .build()
            );
        }
    }
    public ResponseEntity<ResponseDto> getPassportInfoFromUser(Authentication authentication){
        try{
            User tempUser = userRepositories.findByEmail(authentication.getName());
            if (Objects.isNull(tempUser)) {
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(304)
                                .message("Internal service Error!")
                                .build()
                );
            }
            Passport tempPassport = tempUser.getPassport();
            if (Objects.isNull(tempPassport)) {
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(404)
                                .message("No passport info for user with email: "+ authentication.getName())
                                .build()
                );
            }
            PassportInfoDto tempPassportDto = PassportInfoDto.builder()
                    .name(AESEncryption.decrypt(tempPassport.getName()))
                    .surname(AESEncryption.decrypt(tempPassport.getSurname()))
                    .sex(AESEncryption.decrypt(tempPassport.getSex()))
                    .dateOfBirth(AESEncryption.decrypt(tempPassport.getDateOfBirth()))
                    .nationality(AESEncryption.decrypt(tempPassport.getNationality()))
                    .passportType(AESEncryption.decrypt(tempPassport.getPassportType()))
                    .passportNumber(AESEncryption.decrypt(tempPassport.getPassportNumber()))
                    .issuingCountry(AESEncryption.decrypt(tempPassport.getIssuingCountry()))
                    .expirationDate(AESEncryption.decrypt(tempPassport.getExpirationDate()))
                    .personalNumber(AESEncryption.decrypt(tempPassport.getPersonalNumber()))
                    .build();
            return ResponseEntity.ok(
                    ResponseDto.builder()
                    .data(tempPassportDto)
                    .build()
            );
        }catch (Exception ex){
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .status(304)
                            .message(ex.getMessage())
                            .build()
            );
        }
    }
    public ResponseEntity<ResponseDto> validateCode(ValidateCodeDto validateCodeDto){
        try{
            //Define sort by createTime
            Sort sort = Sort.by(Sort.Direction.DESC, "createTime");

            //Get all verification code associate with specific email
            ArrayList<VerificationCode> codes = (ArrayList<VerificationCode>) verificationCodeRepository.findByEmail(validateCodeDto.getEmail(),sort);
            if(!codes.isEmpty()) {
                //Get the first one from List which is the newest one
                VerificationCode newestCode = codes.get(0);
                //Check if code expired
                Date currentTime = new Date();
                // Find the difference in milliseconds
                long differenceInMilliSeconds = Math.abs(currentTime.getTime() - newestCode.getCreateTime().getTime());

                // Convert the difference to seconds
                long differenceInSeconds = differenceInMilliSeconds / 1000;
                if (differenceInSeconds > 60) {
                    //Code expired
                    return ResponseEntity.ok(
                            ResponseDto.builder()
                                    .status(408)
                                    .message("Code expired.")
                                    .build()
                    );
                }
                //Verify code
                if (!newestCode.getCode().equals(validateCodeDto.getVerificationCode())) {
                    return ResponseEntity.ok(
                            ResponseDto.builder()
                                    .status(400)
                                    .message("Wrong verification code.")
                                    .build()
                    );
                }
                return ResponseEntity.ok(new ResponseDto());
            }
            else{
                //No verification code found
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(400)
                                .message("Wrong verification code.")
                                .build()
                );
            }
        }catch (Exception ex){
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .status(304)
                            .message(ex.getMessage())
                            .build()
            );
        }
    }
    public ResponseEntity<ResponseDto> getUserProfile(Authentication authentication){
        try{
            User tempUser = userRepositories.findByEmail(authentication.getName());
            if (Objects.isNull(tempUser)) {
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(304)
                                .message("Internal service Error!")
                                .build()
                );
            }
            UserDto userProfile = UserDto.builder()
                    .email(tempUser.getEmail())
                    .firstName(tempUser.getFirstName())
                    .lastName(tempUser.getLastName())
                    .phoneNumber(tempUser.getPhoneNumber())
                    .build();
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .data(userProfile)
                            .build()
            );
        }catch(Exception ex){
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .status(304)
                            .message(ex.getMessage())
                            .build()
            );
        }
    }
    @Transactional
    public ResponseEntity<ResponseDto> updateUserProfile(Authentication authentication, UserDto userDto){
        try{
            User tempUser = userRepositories.findByEmail(authentication.getName());
            tempUser.setLastName(userDto.getLastName());
            tempUser.setFirstName(userDto.getFirstName());
            tempUser.setPhoneNumber(userDto.getPhoneNumber());

            //Update in DB
            userRepositories.save(tempUser);
            return  ResponseEntity.ok(new ResponseDto());
        }catch(Exception ex){
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .status(304)
                            .message(ex.getMessage())
                            .build()
            );
        }
    }
    @Transactional
    public ResponseEntity<ResponseDto> sendTempVerificationCode(SendTempVerificationCodeDto sendTempVerificationCodeDto){
        try{
            User tempUser = userRepositories.findByEmail(sendTempVerificationCodeDto.getEmail());
            if(tempUser==null){
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(400)
                                .message("User doesn't Exist!")
                                .build()
                );
            }
            String characters = "ABCDEFGHJKLMNOPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz0123456789";
            StringBuilder verificationCode = new StringBuilder();
            Random rnd = new Random();

            for (int i = 0; i < 6; i++) {
                int index = rnd.nextInt(characters.length());
                verificationCode.append(characters.charAt(index));
            }

            //Send Email to User
            String emailSubject = "Reset your CONSTDU Account Password";
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<html>");
            emailBody.append("<head>");
            emailBody.append("<title>Password Reset Verification</title>");
            emailBody.append("<style>");
            emailBody.append("body { font-family: Arial, sans-serif; margin: 0; padding: 0; }");
            emailBody.append(".container { padding: 20px; }");
            emailBody.append("h2 { color: #333366; }");
            emailBody.append("p { line-height: 1.6; }");
            emailBody.append(".code { padding: 10px; border: 1px solid #ccc; margin-top: 5px; margin-bottom: 5px; font-size: 18px; font-weight: bold; }");
            emailBody.append("</style>");
            emailBody.append("</head>");
            emailBody.append("<body>");
            emailBody.append("<div class=\"container\">");
            emailBody.append("<p>Dear ").append(tempUser.getFirstName()).append(",</p>");
            emailBody.append("<p>We have received a request to reset your password. If you did not make this request, please ignore this email.</p>");
            emailBody.append("<p>Here is your verification code:</p>");
            emailBody.append("<div class=\"code\">").append(verificationCode.toString()).append("</div>");
            emailBody.append("<p>Please enter this code in the provided field to proceed with resetting your password.</p>");
            emailBody.append("<p>Thank you,</p>");
            emailBody.append("<p>CONSTDU Team</p>");
            emailBody.append("</div>");
            emailBody.append("</body>");
            emailBody.append("</html>");

            boolean sendEmailResult = emailService.sendEmail(sendTempVerificationCodeDto.getEmail(),emailSubject,emailBody.toString());
            if(!sendEmailResult){
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(304)
                                .message("Fail to send verification code by email.")
                                .build()
                );
            }
            //Update in db
            VerificationCode verificationCodeObj = VerificationCode.builder()
                    .code(verificationCode.toString())
                    .email(tempUser.getEmail())
                    .build();

            verificationCodeRepository.save(verificationCodeObj);
            return ResponseEntity.ok(new ResponseDto());
        }catch (Exception ex){
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .status(304)
                            .message(ex.getMessage())
                            .build()
            );
        }
    }
    public User getUserByToken(String token) {
        User user = new User();
        try {
            StringTokenizer st = new StringTokenizer(token, ".");
            String header = st.nextToken();
            String payload = st.nextToken();

            String headerJson = new String(Base64.decodeBase64(header), StandardCharsets.UTF_8);
            String payloadJson = new String(Base64.decodeBase64(payload), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            JwtPayload info = gson.fromJson(payloadJson, JwtPayload.class);
            String userEmail = info.getSub();
            user = userRepositories.findByEmail(userEmail);
            if (Objects.isNull(user)) {
                return user;
            }
        } catch (Exception e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        }
        return user;
    }

    public User getUserByToken() {
        User user = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof Jwt) {
                    Jwt jwt = (Jwt) principal;
                    user = getUserByToken(jwt.getTokenValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
    public User findUserByEmail(String email) {
        return userRepositories.findByEmail(email);
    }
    public String generateGreetingMessage(User user){
        String userName = user.getFirstName() + " " + user.getLastName();
        String emailBody = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>Welcome to CONSTUD!</title>" +
                "<style>" +
                "    body { font-family: Arial, sans-serif; margin: 0; padding: 0; }" +
                "    .container { padding: 20px; }" +
                "    .header { background-color: #4CAF50; color: #ffffff; padding: 10px; text-align: center; }" +
                "    .content { margin-top: 20px; }" +
                "    .footer { margin-top: 30px; border-top: 1px solid #dcdcdc; padding-top: 10px; text-align: center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h1>Welcome to CONSTUD!</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <p>Dear " + userName + ",</p>" +
                "            <p>Thank you for signing up with CONSTUD! We are thrilled to have you on board. We specialize in helping international students like you extend their study permits in Canada.</p>" +
                "            <p>We are committed to providing you with accurate and timely information to make your study permit extension process as smooth as possible.</p>" +
                "            <p>Feel free to explore our app and utilize the resources available. If you have any questions or need assistance, please do not hesitate to reach out to us.</p>" +
                "            <p>We look forward to assisting you in your journey in Canada!</p>" +
                "            <p>Best Regards,<br>The CONSTUD Team</p>" +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <p>&copy; " + LocalDate.now().getYear() + " CONSTUD. All Rights Reserved.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
        return emailBody;
    }

    public ResponseEntity<ResponseDto> checkIfJwtIsExpired(Authentication authentication, HttpHeaders headers) {
        // this method is used to check if the jwt has expired.
        // Returns a UserDto for ios to confirm that the JWT has not expired.
        String jwt = headers.get("authorization").get(0);
        jwt = jwt.substring(7, jwt.length());
        try{
            User tempUser = userRepositories.findByEmail(authentication.getName());
            if (Objects.isNull(tempUser)) {
                return ResponseEntity.ok(
                        ResponseDto.builder()
                                .status(304)
                                .message("Internal service Error!")
                                .build()
                );
            }

            UserDto userDto = UserDto.builder()
                    .id(tempUser.getId())
                    .firstName(tempUser.getFirstName())
                    .lastName(tempUser.getLastName())
                    .email(tempUser.getEmail())
                    .phoneNumber(tempUser.getPhoneNumber())
                    .role(tempUser.getRole().getRoleName())
                    .build();
            HashMap<String, Object> res = new HashMap<String, Object>();
            res.put("user", userDto);
            res.put("jwt", jwt);

            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .data(res)
                            .build()
            );

        }catch(Exception ex){
            return ResponseEntity.ok(
                    ResponseDto.builder()
                            .status(304)
                            .message(ex.getMessage())
                            .build()
            );
        }
    }
}
