package com.example.test.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUrlDto {
    private String fileUrl;
    private FilePathsDto filePaths;
}
