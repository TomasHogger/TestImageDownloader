package com.example.test.demo.dto;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class Base64ImageDto {
    private List<String> files;

    public void setFile(String file) {
        files = Collections.singletonList(file);
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
