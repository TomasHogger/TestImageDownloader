package com.example.test.demo.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class FileUtils {
    public static String randomFileName(String extension) {
        return System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(10) + "." + extension;
    }

    public static String addDirToFileName(String dir, String name) {
        return dir + "/" + name;
    }
}
