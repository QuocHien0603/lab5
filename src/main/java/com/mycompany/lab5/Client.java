/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lab5;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Admin
 */
public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            // Get the search string from the user
            System.out.print("Nhap chuoi can tim : ");
            String searchString = scanner.nextLine();

            // Get the list of file paths from the user
            System.out.println("Nhap duong dan file:");
            String inputPaths = scanner.nextLine();
            List<String> filePaths = Arrays.asList(inputPaths.split("\\s*,\\s*"));

            // Send the search string and file list to the server
            oos.writeObject(searchString);
            oos.writeObject(filePaths);

            // Receive the results from the server
            Map<String, Integer> results = (Map<String, Integer>) ois.readObject();

            // Display the results
            if (results.isEmpty()) {
                System.out.println("No occurrences found.");
            } else {
                results.forEach((filePath, count) -> 
                    System.out.println("File: " + filePath + ", Tim thay : " + count));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
