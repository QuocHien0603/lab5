/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.lab5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author Admin
 */
public class Lab5 {

     public static void main(String[] args) {
        // Directory to save downloaded files
        String downloadDirectory = "D:\\Download";

        // Ensure the download directory exists
        Path downloadDirPath = Paths.get(downloadDirectory);
        if (!Files.exists(downloadDirPath)) {
            try {
                Files.createDirectories(downloadDirPath);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // List of URLs to download
        List<String> fileUrls = List.of(
                "https://www.gutenberg.org/files/11/11-0.txt",
                "https://www.gutenberg.org/files/1342/1342-0.txt",
                "https://www.gutenberg.org/files/84/84-0.txt",
                "https://www.gutenberg.org/files/2600/2600-0.txt",
                "https://www.gutenberg.org/files/5200/5200-0.txt"
        );

        // Create an ExecutorService with a fixed thread pool of size 5
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        try {
            // Submit file downloading tasks to the ExecutorService
            List<Future<String>> futures = fileUrls.stream()
                    .map(fileUrl -> executorService.submit(new FileDownloaderTask(fileUrl, downloadDirectory)))
                    .toList();

            // Retrieve and print the status of each download
            for (Future<String> future : futures) {
                try {
                    System.out.println(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            // Shut down the ExecutorService
            executorService.shutdown();
        }
    }
}

class FileDownloaderTask implements Callable<String> {
    private final String fileUrl;
    private final String downloadDirectory;

    public FileDownloaderTask(String fileUrl, String downloadDirectory) {
        this.fileUrl = fileUrl;
        this.downloadDirectory = downloadDirectory;
    }

    @Override
    public String call() {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        Path filePath = Paths.get(downloadDirectory, fileName);
        try (InputStream in = new URL(fileUrl).openStream();
             OutputStream out = Files.newOutputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return "Downloaded: " + fileName + " to " + downloadDirectory;
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to download: " + fileName;
        }
    }
}
