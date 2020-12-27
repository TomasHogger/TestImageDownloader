package com.example.test.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileNumberDto {
    private Integer number;
    private FilePathsDto filePaths;
}
