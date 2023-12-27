package com.pikachu.constdu.services;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService {

    public boolean ZipFiles(ArrayList<String> sourceFilePaths, String destPath) {
        ZipOutputStream zipOutputStream = null;
        try {
            FileOutputStream fos = new FileOutputStream(destPath);
            zipOutputStream = new ZipOutputStream(fos);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        for (String sourceFilePath : sourceFilePaths) {
            try {
                zipOutputStream.putNextEntry(new ZipEntry(new File(sourceFilePath).getName()));
                FileInputStream fileInputStream = new FileInputStream(sourceFilePath);
                IOUtils.copy(fileInputStream, zipOutputStream);
                fileInputStream.close();
                zipOutputStream.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
