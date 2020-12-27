package com.example.test.demo.utils;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

public class UrlUtils {
    public static String getExtensionFromUrl(URL url) throws IOException {
        return ImageIO.getImageReaders(
                ImageIO.createImageInputStream(
                        url.openStream()
                )
        ).next().getFormatName().toLowerCase();
    }
}
