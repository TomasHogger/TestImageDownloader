package com.example.test.demo.controller;

import com.example.test.demo.dto.Base64ImageDto;
import com.example.test.demo.dto.FileNameDto;
import com.example.test.demo.dto.FileNumberDto;
import com.example.test.demo.dto.FileUrlDto;
import com.example.test.demo.service.ImageDownloaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ImageDownloaderController {

    private final ImageDownloaderService service;

    @Autowired
    public ImageDownloaderController(ImageDownloaderService service) {
        this.service = service;
    }

    @RequestMapping(value = "/image_downloader", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<?> downloaderMultipartImages(@RequestParam("files") List<MultipartFile> files) {
        List<FileNameDto> list = service.saveMultipartImages(files);
        return new ResponseEntity<>(list,
                list.stream().anyMatch(e -> e.getFilePaths() != null) ?
                        HttpStatus.OK :
                        HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/image_downloader", method = RequestMethod.POST, consumes = {"application/json"})
    public ResponseEntity<?> downloaderBase64Images(@RequestBody Base64ImageDto files) {
        List<FileNumberDto> list = service.saveBase64Images(files);
        return new ResponseEntity<>(list,
                list.stream().anyMatch(e -> e.getFilePaths() != null) ?
                        HttpStatus.OK :
                        HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/image_downloader", method = RequestMethod.GET)
    public ResponseEntity<?> downloaderUrlImage(@RequestParam("file") String file) {
        FileUrlDto fileUrlDto = service.saveImageFromUrl(file);
        return new ResponseEntity<>(fileUrlDto,
                fileUrlDto.getFilePaths() == null ?
                        HttpStatus.BAD_REQUEST :
                        HttpStatus.OK);
    }

}
