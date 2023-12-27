package com.pikachu.constdu.apiControllers;

import com.pikachu.constdu.dto.ChatEntryDto;
import com.pikachu.constdu.dto.ResponseDto;
import com.pikachu.constdu.models.User;
import com.pikachu.constdu.services.ChatEntryService;
import com.pikachu.constdu.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Yuanfu Tian
 * Date: 2023-10-03
 */
@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    @Autowired
    private ChatEntryService chatEntryService;

    @Autowired
    private UserService userService;


    @GetMapping("/history")
    public ResponseEntity<ResponseDto<List<ChatEntryDto>>> getAllHistory(){
        User user = userService.getUserByToken();
        return chatEntryService.findAllByUser(user);
    }

    @PostMapping("/newSession")
    public ResponseEntity<ResponseDto<ChatEntryDto>> initializeSession(){
        System.out.println("Initialize Session");
        return chatEntryService.newSession();
    }

    @PostMapping("/userAnswerQuestion")
    public ResponseEntity<ResponseDto<ChatEntryDto>> userAnswerQuestion(@RequestBody ChatEntryDto chatEntryDto){
        return chatEntryService.userAnswerQuestion(chatEntryDto);
    }

}
