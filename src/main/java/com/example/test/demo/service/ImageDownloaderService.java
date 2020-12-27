package com.example.test.demo.service;

import com.example.test.demo.dto.*;
import com.example.test.demo.utils.DirUtils;
import com.example.test.demo.utils.FileUtils;
import com.example.test.demo.utils.UrlUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    private String baseUrl = null;

    @Value("${upload.path.full.image}")
    private String pathFullImage;
    @Value("${upload.path.preview.image}")
    private String pathPreviewImage;
    @Value("${size.preview.image}")
    private Integer sizePreviewImage;
    @Value("${preview.image.prefix}")
    private String previewPrefix;

    public FileUrlDto saveImageFromUrl(String urlPath) {
        FilePathsDto filePathsDto;
        try {
            URL url = new URL(urlPath);
            String extension = UrlUtils.getExtensionFromUrl(url);
            BufferedImage image = ImageIO.read(url);

            filePathsDto = saveBufferedImages(image, extension);
        } catch (IOException e) {
            filePathsDto = null;
        }
        return new FileUrlDto(urlPath, filePathsDto);
    }

    public List<FileNumberDto> saveBase64Images(Base64ImageDto files) {
        List<FileNumberDto> response = new ArrayList<>();
        for (int i = 0; i < files.getFiles().size(); i++) {
            String file = files.getFiles().get(i);
            FilePathsDto filePathsDto;
            try {
                String metaInfo = "data:image/";
                String extension;
                String imageStr;
                if (file.contains(metaInfo)) {
                    imageStr = file.split(",")[1];
                    extension = file.replace(metaInfo, "").replaceAll(";.+", "");
                } else {
                    imageStr = file;
                    extension = "bmp";
                }


                byte[] imageBytes = Base64.decodeBase64(imageStr);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                BufferedImage image = ImageIO.read(bis);

                filePathsDto = saveBufferedImages(image, extension);
            } catch (Exception e) {
                filePathsDto = null;
            }
            response.add(new FileNumberDto(i, filePathsDto));
        }
        return response;
    }

    public List<FileNameDto> saveMultipartImages(List<MultipartFile> files) {
        List<FileNameDto> response = new ArrayList<>();
        for (MultipartFile file : files) {
            FilePathsDto filePathsDto;
            try {
                String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                BufferedImage image = ImageIO.read(file.getInputStream());
                filePathsDto = saveBufferedImages(image, extension);
            } catch (Exception e) {
                filePathsDto = null;
            }
            response.add(new FileNameDto(file.getOriginalFilename(), filePathsDto));
        }

        return response;
    }

    private FilePathsDto saveBufferedImages(BufferedImage image, String extension) {
        DirUtils.createDirIfNotExists(pathFullImage, pathPreviewImage);

        try {
            String imageName = FileUtils.randomFileName(extension);
            String imagePath = FileUtils.addDirToFileName(pathFullImage, imageName);

            ImageIO.write(image, extension, new File(imagePath));

            String previewImageName;
            String previewImagePath;

            if (image.getHeight() <= sizePreviewImage && image.getWidth() <= sizePreviewImage) {
                previewImagePath = imagePath;
            } else {
                previewImageName = previewPrefix + imageName;
                previewImagePath = FileUtils.addDirToFileName(pathPreviewImage, previewImageName);

                BufferedImage previewImage = Scalr.resize(image, sizePreviewImage);

                ImageIO.write(previewImage, extension, new File(previewImagePath));
            }

            return new FilePathsDto(
                    addBaseUrl(imagePath),
                    addBaseUrl(previewImagePath));
        } catch (Exception e) {
            return null;
        }
    }

    private String addBaseUrl(String url) {
        if (baseUrl == null) {
            baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        }

        return baseUrl + "/" + url;
    }
}
