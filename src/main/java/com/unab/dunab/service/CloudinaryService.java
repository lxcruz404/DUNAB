package com.unab.dunab.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

@Service
@Slf4j
public class CloudinaryService {

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    private boolean isConfigured() {
        return cloudName != null && !cloudName.isBlank()
                && apiKey != null && !apiKey.isBlank()
                && apiSecret != null && !apiSecret.isBlank();
    }

    public String upload(MultipartFile file, String folder) throws IOException {
        if (isConfigured()) {
            try {
                return uploadToCloudinary(file, folder);
            } catch (Exception e) {
                log.warn("Cloudinary upload failed, saving locally: {}", e.getMessage());
                return saveLocally(file);
            }
        }
        return saveLocally(file);
    }

    private String uploadToCloudinary(MultipartFile file, String folder) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String publicFolder = "dunab/" + folder;
        String toSign = "folder=" + publicFolder + "&timestamp=" + timestamp + apiSecret;
        String signature = sha1Hex(toSign);

        String boundary = "----FormBoundary" + UUID.randomUUID().toString().replace("-", "");
        String uploadUrl = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload";

        URL url = new URL(uploadUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(30000);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (OutputStream os = conn.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true)) {

            addField(writer, boundary, os, "api_key", apiKey);
            addField(writer, boundary, os, "timestamp", timestamp);
            addField(writer, boundary, os, "signature", signature);
            addField(writer, boundary, os, "folder", publicFolder);

            // File part
            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.getOriginalFilename()).append("\"").append("\r\n");
            writer.append("Content-Type: ").append(file.getContentType()).append("\r\n\r\n");
            writer.flush();
            os.write(file.getBytes());
            os.flush();
            writer.append("\r\n");
            writer.append("--").append(boundary).append("--").append("\r\n");
            writer.flush();
        }

        int status = conn.getResponseCode();
        if (status != 200) throw new RuntimeException("Cloudinary error: " + status);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            String response = sb.toString();
            // Extract secure_url from JSON response
            int idx = response.indexOf("\"secure_url\":\"");
            if (idx < 0) throw new RuntimeException("No URL in response");
            int start = idx + 14;
            int end = response.indexOf("\"", start);
            String secureUrl = response.substring(start, end).replace("\\/", "/");
            log.info("Uploaded to Cloudinary: {}", secureUrl);
            return secureUrl;
        }
    }

    private void addField(PrintWriter writer, String boundary, OutputStream os, String name, String value) throws IOException {
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append("\r\n\r\n");
        writer.append(value).append("\r\n");
        writer.flush();
    }

    private String sha1Hex(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] bytes = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private String saveLocally(MultipartFile file) throws IOException {
        String ext = "jpg";
        String orig = file.getOriginalFilename();
        if (orig != null && orig.contains("."))
            ext = orig.substring(orig.lastIndexOf('.') + 1).toLowerCase();
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path uploadDir = Paths.get("uploads").toAbsolutePath();
        Files.createDirectories(uploadDir);
        Files.copy(file.getInputStream(), uploadDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        log.info("Saved locally: /uploads/{}", filename);
        return "/uploads/" + filename;
    }
}
