package com.pikachu.constdu.apiControllers;

import com.pikachu.constdu.dto.*;
import com.pikachu.constdu.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("user")
@RestController
public class UserController {
    private final UserService userService;
    public UserController(UserService _userService){
        this.userService = _userService;
    }

    @GetMapping("/")
    public String homeTest(){
        return "Hello Tester";
    }
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signUp(@RequestBody SignUpDto user){
        return userService.signUp(user);
    }
    @PostMapping("/signIn")
    public ResponseEntity<ResponseDto> signIn(Authentication authentication){
        return userService.signIn(authentication);
    }
    @PostMapping("/updatePasswordWhenUserLoggedIn")
    public ResponseEntity<ResponseDto> updatePasswordWhenUserLoggedIn(Authentication authentication, @RequestBody UpdatePasswordWhenUserLoggedInDto updatePasswordWhenUserLoggedInDto){
        return userService.updatePasswordWhenUserLoggedIn(authentication, updatePasswordWhenUserLoggedInDto);
    }
    @PostMapping("/updatePasswordWithoutLoggedIn")
    public ResponseEntity<ResponseDto> updatePasswordWithoutLoggedIn(@RequestBody UpdatePasswordWithoutLoggedInDto updatePasswordWithoutLoggedInDto){
        return userService.updatePasswordWithoutLoggedIn(updatePasswordWithoutLoggedInDto);
    }
    @PostMapping("/validateCode")
    public ResponseEntity<ResponseDto> validateCode(@RequestBody ValidateCodeDto validateCodeDto){
        return userService.validateCode(validateCodeDto);
    }
    @PostMapping("/getUserProfile")
    public ResponseEntity<ResponseDto> getUserProfile(Authentication authentication){
        return userService.getUserProfile(authentication);
    }
    @PostMapping("/updateUserProfile")
    public ResponseEntity<ResponseDto> updateUserProfile(Authentication authentication, @RequestBody UserDto userDto){
        return userService.updateUserProfile(authentication, userDto);
    }
    @PostMapping("/sendTempVerificationCode")
    public ResponseEntity<ResponseDto> sendTempVerificationCode(@RequestBody SendTempVerificationCodeDto sendTempVerificationCodeDto){
        return userService.sendTempVerificationCode(sendTempVerificationCodeDto);
    }
    @PostMapping("/addPassportToUser")
    public ResponseEntity<ResponseDto> addPassportToUser(Authentication authentication, @RequestBody PassportInfoDto passportInfoDto){
        return userService.addPassportToUser(authentication,passportInfoDto);
    }
    @PostMapping("/getPassportInfoFromUser")
    public ResponseEntity<ResponseDto> getPassportInfoFromUser(Authentication authentication){
        return userService.getPassportInfoFromUser(authentication);
    }
    @GetMapping("/checkIfJwtIsExpired")
    public ResponseEntity<ResponseDto> checkIfJwtIsExpired(Authentication authentication, @RequestHeader HttpHeaders headers) {
        return userService.checkIfJwtIsExpired(authentication, headers);
    }

}
