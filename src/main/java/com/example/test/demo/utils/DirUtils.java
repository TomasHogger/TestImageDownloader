package com.example.test.demo.utils;

import java.io.File;

public class DirUtils {
    public static void createDirIfNotExists(String... dirs) {
        for (String dir : dirs) {
            File file = new File(dir);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }
}
