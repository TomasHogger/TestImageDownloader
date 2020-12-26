package com.example.test.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilePathsDto implements Dto {
    private String imagePath;
    private String previewImagePath;
    private String fileNameOrUrl;
}
