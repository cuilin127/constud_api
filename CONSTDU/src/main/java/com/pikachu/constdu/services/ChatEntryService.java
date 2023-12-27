package com.pikachu.constdu.services;

import com.pikachu.constdu.dto.ChatEntryDto;
import com.pikachu.constdu.dto.ChatEntrySimpleDto;
import com.pikachu.constdu.dto.ChatRequestBodyDto;
import com.pikachu.constdu.dto.ResponseDto;
import com.pikachu.constdu.models.ChatEntry;
import com.pikachu.constdu.models.User;
import com.pikachu.constdu.repositories.ChatEntryRepository;
import com.pikachu.constdu.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Yuanfu Tian
 * Date: 2023-10-03
 */
@Service
public class ChatEntryService {


    @Autowired
    private ChatEntryRepository chatEntryRepository;

    @Autowired
    private Environment env;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<ResponseDto<List<ChatEntryDto>>> findAllByUser(User user) {
        List<String> roles = Arrays.asList("user", "assistant");
        List<ChatEntry> chatHistoryEntries = chatEntryRepository.findAllByUserAndRoleIn(user, roles);
        if (chatHistoryEntries.isEmpty()) {
            return ResponseEntity.ok(ResponseDto.<List<ChatEntryDto>>builder().data(new ArrayList<>()).build());
        }
        else{
            List<ChatEntryDto> chatEntryDtoList = chatHistoryEntries.stream().map((chatEntry -> ChatEntryDto.fromChatEntry(chatEntry))).toList();
            List<ChatEntryDto> res = chatEntryDtoList.stream().sorted(Comparator.comparing(ChatEntryDto::getTimestamp)).toList();

            return ResponseEntity.ok(ResponseDto.<List<ChatEntryDto>>builder().data(res).build());
        }
    }

    public ResponseEntity<ResponseDto<ChatEntryDto>> newSession() {
        Long requestTimeStamp = Instant.now().toEpochMilli();
        // initialize message
        String systemPrompt = "You are a Chatbot and you want to collect the following information: first name, last name, date of birth from your users in a friendly and formal language. Please ask user question by question. Now get started.";

        // preparation for sending request
        String baseURL = env.getProperty("constud.chatbot.api.base.url");
        String url = baseURL + "/newSession";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> accept = new ArrayList<>();
        accept.add(MediaType.ALL);
        headers.setAccept(accept);

        ChatEntryDto body = ChatEntryDto.builder()
                .role("system")
                .content(systemPrompt)
                .build();

        HttpEntity<ChatEntryDto> entity = new HttpEntity<>(body, headers);

        ChatEntryDto res = restTemplate.postForObject(url, entity, ChatEntryDto.class);
        System.out.println(res.getRole());
        System.out.println(res.getContent());

        // save the ChatEntry to database
        User user = userService.getUserByToken();

        ChatEntry systemChatEntry = ChatEntry.builder()
                .role("system")
                .content(systemPrompt)
                .timestamp(requestTimeStamp)
                .user(user)
                .build();
        chatEntryRepository.save(systemChatEntry);

        ChatEntry assistantChatEntry = ChatEntryDto.generateChatEntryByChatEntryDto(res, user);

        ChatEntry savedChatEntry = chatEntryRepository.save(assistantChatEntry);
        // convert the ChatEntry to ChatEntryDto for the response data.
        ChatEntryDto assistantChatEntryDto = ChatEntryDto.fromChatEntry(savedChatEntry);

        return ResponseEntity.ok(ResponseDto.<ChatEntryDto>builder()
                .data(assistantChatEntryDto)
                .build());
    }


    public ResponseEntity<ResponseDto<ChatEntryDto>> userAnswerQuestion(ChatEntryDto chatEntryDto) {
        // todo: check if the chatEntryDto is valid
        User user = userService.getUserByToken();
        ChatEntry chatEntry = ChatEntryDto.generateChatEntryByChatEntryDto(chatEntryDto, user);

        chatEntryRepository.save(chatEntry);

        // preparation for sending request
        String baseURL = env.getProperty("constud.chatbot.api.base.url");
        String url = baseURL + "/userAnswerQuestion";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> accept = new ArrayList<>();
        accept.add(MediaType.ALL);
        headers.setAccept(accept);

        List<ChatEntry> chatEntryList = chatEntryRepository.findAllByUser(user);
        List<ChatEntrySimpleDto> body = chatEntryList.stream().map((ChatEntrySimpleDto::fromChatEntry)).toList();
        ChatRequestBodyDto requestBody = ChatRequestBodyDto.builder().messages(body).build();
        HttpEntity<ChatRequestBodyDto> entity = new HttpEntity<>(requestBody, headers);

        ChatEntryDto res = restTemplate.postForObject(url, entity, ChatEntryDto.class);

        // todo: check if the res is valid

        ChatEntry resForSaving = ChatEntryDto.generateChatEntryByChatEntryDto(res, user);
        ChatEntry savedChatEntry = chatEntryRepository.save(resForSaving);

        ChatEntryDto responseBody = ChatEntryDto.fromChatEntry(savedChatEntry);

        return ResponseEntity.ok(ResponseDto.<ChatEntryDto>builder().data(responseBody).build());
    }

}
