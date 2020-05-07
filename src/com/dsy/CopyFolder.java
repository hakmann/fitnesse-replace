package com.dsy;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class CopyFolder {

    private static final String NEW_FOLDER_SUFFIX = "_DB_Function";

    public void copyFolder(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                copy(file, target.resolve(source.relativize(file)));
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public String makeDestFolderName(String src) {
        String[] split = src.split("/");
        String originalTenant = split[split.length - 1];
        return src.replace(originalTenant, originalTenant + NEW_FOLDER_SUFFIX);
    }

    private void copy(Path source, Path dest) {
        try {
            //Files.copy(source, dest, REPLACE_EXISTING);
            Files.copy(source, dest);
        } catch (Exception e) {
            //throw new RuntimeException(e.getMessage(), e);
            System.out.println(source.toString() + " file already exists. skip");
        }
    }

}
