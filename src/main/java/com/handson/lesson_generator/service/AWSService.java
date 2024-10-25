package com.handson.lesson_generator.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;

@Service
public class AWSService {

    @Value("${bucket.url}")
    String bucket;

    @Autowired
    private AmazonS3 s3Client;

    private static final Logger logger = LoggerFactory.getLogger(AWSService.class);

    /**
     * Stores text content directly in S3
     * @param textContent The text content to store
     * @param path The path where the text should be stored in the bucket
     */
    public void putTextInBucket(String textContent, String path) {
        try {
            byte[] contentBytes = textContent.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(contentBytes);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("text/plain");
            metadata.setContentLength(contentBytes.length);

            s3Client.putObject(bucket, path, inputStream, metadata);

            inputStream.close();
        } catch (Exception e) {
            logger.error("Error uploading text to bucket: " + bucket + "/ " + path, e);
        }
    }

    /**
     * Retrieves text content from S3
     * @param path The path to the text file in the bucket
     * @return The text content, or null if there was an error
     */
    public String getTextContent(String path) {
        try {
            S3Object object = s3Client.getObject(bucket, path);
            String content = new String(object.getObjectContent().readAllBytes(), StandardCharsets.UTF_8);
            object.close();
            return content;
        } catch (Exception e) {
            logger.error("Error reading text from bucket: " + bucket + "/ " + path, e);
            return null;
        }
    }



    public void putUrlInBucket(String imageUrl, String path) {
        try {
            Path tempFile = Files.createTempFile("image-" + UUID.randomUUID(), ".png");
            URL url = new URL(imageUrl);
            Files.copy(url.openStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            saveAndSend(tempFile, path);
            Files.deleteIfExists(tempFile);
        } catch (Exception e) {
            logger.error("Error uploading file to bucket: " + bucket + "/ " + path, e);
        }
    }

    private void saveAndSend(Path uploadFile, String destPath) {
        PutObjectRequest request = new PutObjectRequest(bucket, destPath, uploadFile.toFile());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/png");
        request.setMetadata(metadata);
        s3Client.putObject(request);
    }


    public String generateLink(String fileUrl) {
        return generateLink(bucket, fileUrl);
    }

    public String generateLink(String bucketName, String fileUrl) {
        // Set the presigned URL to expire after one min.
        if (fileUrl == null) return null;
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        return generateLink(bucketName, fileUrl, expTimeMillis);
    }
    public String generateLink(String bucketName, String fileUrl, long expTimeMillis) {
        if (fileUrl.lastIndexOf(bucketName) >= 0) {
            fileUrl = fileUrl.substring(fileUrl.lastIndexOf(bucketName) + bucketName.length() + 1);
        }
        try {
            Date expiration = new Date();
            expiration.setTime(expTimeMillis);

            // Generate the presigned URL.
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, fileUrl)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            return url.toString();
        } catch (Exception e) {
            logger.error("Error generating presigned link", e);
        }
        return null;
    }

}
