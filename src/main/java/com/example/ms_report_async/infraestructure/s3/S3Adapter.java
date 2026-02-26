package com.example.ms_report_async.infraestructure.s3;

import com.example.ms_report_async.domain.repository.S3Port;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
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

    @Override
    public byte[] getPdfFromBucket2(String jobId) {
        String fileName = jobId + ".pdf"; // Supondo que você salvou com esse nome

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket("nome-do-bucket-2")
                .key(fileName)
                .build();

        try {
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();
        } catch (NoSuchKeyException e) {
            // Arquivo ainda não existe (processamento não terminou)
            return null;
        }
    }
}
