package com.example.test.demo.service;

import com.example.test.demo.dto.Base64ImageDto;
import com.example.test.demo.dto.Dto;
import com.example.test.demo.dto.ErrorDto;
import com.example.test.demo.dto.FilePathsDto;
import com.example.test.demo.utils.DirUtils;
import com.example.test.demo.utils.FileUtils;
import com.example.test.demo.utils.UrlUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageDownloaderService {

    @Value("${upload.path.full.image}")
    private String pathFullImage;
    @Value("${upload.path.preview.image}")
    private String pathPreviewImage;
    @Value("${size.preview.image}")
    private Integer sizePreviewImage;
    @Value("${preview.image.prefix}")
    private String previewPrefix;

    public ResponseEntity<?> saveImageFromUrl(String urlPath) {
        try {
            URL url = new URL(urlPath);
            String extension = UrlUtils.getExtensionFromUrl(url);
            BufferedImage image = ImageIO.read(url);

            List<Dto> response = saveBufferedImages(
                    new BufferedImageWithExtension(
                            image,
                            extension,
                            urlPath));
            return new ResponseEntity<>(response.get(0), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(new ErrorDto(urlPath, "Данного пути не существует"), HttpStatus.OK);
        }
    }

    public void saveBase64Images(Base64ImageDto files) {
        for (String file : files.getFiles()) {
            byte[] imageBytes = Base64.decodeBase64(file);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        }
    }

    public ResponseEntity<?> saveMultipartImages(List<MultipartFile> files) {
        List<Dto> response = new ArrayList<>();
        BufferedImageWithExtension[] images = new BufferedImageWithExtension[files.size()];
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            try {
                String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                BufferedImage image = ImageIO.read(file.getInputStream());
                images[i] = new BufferedImageWithExtension(image, extension, file.getOriginalFilename());
            } catch (Exception e) {
                response.add(new ErrorDto(file.getOriginalFilename(), "Проблема с файлом"));
            }
        }

        response.addAll(saveBufferedImages(images));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private List<Dto> saveBufferedImages(BufferedImageWithExtension... files) {
        DirUtils.createDirIfNotExists(pathFullImage, pathPreviewImage);
        List<Dto> response = new ArrayList<>();

        for (BufferedImageWithExtension file : files) {
            try {
                if (file == null) {
                    continue;
                }

                String imageName = FileUtils.randomFileName(file.extension);
                String imagePath = FileUtils.addDirToFileName(pathFullImage, imageName);

                ImageIO.write(
                        file.image,
                        file.extension,
                        new File(imagePath));

                String previewImageName;
                String previewImagePath;

                if (file.image.getHeight() <= sizePreviewImage && file.image.getWidth() <= sizePreviewImage) {
                    previewImagePath = imagePath;
                } else {
                    previewImageName = previewPrefix + imageName;
                    previewImagePath = FileUtils.addDirToFileName(pathPreviewImage, previewImageName);

                    BufferedImage previewImage = Scalr.resize(file.image, sizePreviewImage);

                    ImageIO.write(
                            previewImage,
                            file.extension,
                            new File(previewImagePath));
                }



                response.add(new FilePathsDto(imagePath, previewImagePath, file.fileNameOrUrl));
            } catch (Exception e) {
                response.add(new ErrorDto(file.fileNameOrUrl, "Передано не изображение"));
            }
        }

        return response;
    }


    @AllArgsConstructor
    private static class BufferedImageWithExtension {
        BufferedImage image;
        String extension;
        String fileNameOrUrl;
    }
}
