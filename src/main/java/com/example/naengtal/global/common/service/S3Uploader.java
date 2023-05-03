package com.example.naengtal.global.common.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.naengtal.global.error.RestApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.example.naengtal.global.error.CommonErrorCode.IMAGE_UPLOAD_FAIL;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // MultipartFile 을 전달받아 File 로 전환한 후 S3에 업로드
    public String upload(MultipartFile multipartFile) {
        String s3FileName = createFileName();

        try {
            ObjectMetadata objMeta = new ObjectMetadata();
            objMeta.setContentLength(multipartFile.getInputStream().available());
            objMeta.setContentType(multipartFile.getContentType());

            // s3 버킷에 업로드
            amazonS3Client.putObject(
                    new PutObjectRequest(bucket + "/image", s3FileName, multipartFile.getInputStream(), objMeta)
                            .withCannedAcl(CannedAccessControlList.PublicRead) // 업로드되는 파일에 public read 권한 부여
            );
            multipartFile.getInputStream().close();
        } catch (Exception e) {
            throw new RestApiException(IMAGE_UPLOAD_FAIL);
        }

        return amazonS3Client.getUrl(bucket + "/image", s3FileName).toString();
    }

    public void deleteFile(String fileName) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName.substring(62)));
    }

    // 파일 이름 생성(중복 피하기)
    private String createFileName() {
        return UUID.randomUUID().toString();
    }
}
