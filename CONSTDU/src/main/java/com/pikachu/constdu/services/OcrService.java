package com.pikachu.constdu.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pikachu.constdu.dto.GetInfoFromPassportDto;
import com.pikachu.constdu.dto.PassportInfoDto;
import com.pikachu.constdu.dto.ResponseDto;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OcrService {
    private final Environment env;
    public OcrService(Environment _env){
        this.env = _env;
    }
    public ResponseEntity<ResponseDto> getPassportInfo(GetInfoFromPassportDto getInfoFromPassportDto){
        try{
            String ocr_api_url = env.getProperty("constud.ocr.api-url");
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> ocrResponse = restTemplate.postForEntity(ocr_api_url, getInfoFromPassportDto, String.class);

            //Now response body is in String format
            String responseBody = ocrResponse.getBody();
            //Cast from Json to PassportInfoDto
            Gson gson = new Gson();
            // Parse JSON string to JsonObject
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            //Check result
            int responseCode = jsonObject.get("code").getAsInt();
            if(responseCode == 200){
                //Success
                PassportInfoDto passportInfoDto = gson.fromJson(jsonObject.get("object"), PassportInfoDto.class);
                return ResponseEntity.ok(ResponseDto.builder()
                        .data(passportInfoDto)
                        .build());
            }else{
                return ResponseEntity.ok(ResponseDto.builder()
                        .status(jsonObject.get("code").getAsInt())
                        .message(jsonObject.get("message").getAsString())
                        .build());
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

}
