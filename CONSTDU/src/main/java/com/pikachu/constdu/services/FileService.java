package com.pikachu.constdu.services;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileService {
    public boolean deleteFile(String[] paths){
        boolean isDeleted = false;
        for (String path : paths) {
            File file = new File(path);
            isDeleted = file.delete();
            if(!isDeleted){
                break;
            }
        }
        return isDeleted;
    }
}
