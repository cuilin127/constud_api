package com.pikachu.constdu.apiControllers;

import com.pikachu.constdu.dto.GetInfoFromPassportDto;
import com.pikachu.constdu.dto.ResponseDto;
import com.pikachu.constdu.services.OcrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("ocr")
@RestController
public class OcrController {
    private final OcrService ocrService;
    public OcrController(OcrService _ocrService){
        this.ocrService = _ocrService;
    }
    @PostMapping("/getPassportInfo")
    public ResponseEntity<ResponseDto> getPassportInfo(@RequestBody GetInfoFromPassportDto getInfoFromPassportDto){
        return ocrService.getPassportInfo(getInfoFromPassportDto);
    }
}
