package com.example.ms_report_async.infraestructure.s3;

import com.example.ms_report_async.domain.repository.S3Port;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Component
public class S3Adapter implements S3Port{

    private final S3Client s3Client;

    public S3Adapter(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public InputStream download(String bucket, String key) {
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        return s3Client.getObject(req);

    }

    @Override
    public void upload(String bucket, String key, byte[] content, String contentType) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(req, RequestBody.fromBytes(content));

    }
}
