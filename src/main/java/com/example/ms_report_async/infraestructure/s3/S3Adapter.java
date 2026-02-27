package com.example.ms_report_async.infraestructure.s3;

import com.example.ms_report_async.domain.repository.S3Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(S3Adapter.class);
    private final S3Client s3Client;

    public S3Adapter(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public InputStream download(String bucket, String key) {
        logger.info("Iniciando download do S3. Bucket: {}, Key: {}", bucket, key);
        try {
            GetObjectRequest req = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            InputStream stream = s3Client.getObject(req);
            logger.debug("Download do S3 iniciado com sucesso. Bucket: {}, Key: {}", bucket, key);
            return stream;
        } catch (Exception e) {
            logger.error("Erro ao fazer download do S3. Bucket: {}, Key: {}", bucket, key, e);
            throw e;
        }
    }

    @Override
    public void upload(String bucket, String key, byte[] content, String contentType) {
        logger.info("Iniciando upload para S3. Bucket: {}, Key: {}, ContentType: {}, TamanhoBytes: {}",
                bucket, key, contentType, content.length);
        try {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(req, RequestBody.fromBytes(content));
            logger.info("Upload para S3 concluído com sucesso. Bucket: {}, Key: {}", bucket, key);
        } catch (Exception e) {
            logger.error("Erro ao fazer upload para S3. Bucket: {}, Key: {}", bucket, key, e);
            throw e;
        }
    }

    @Override
    public byte[] getPdfFromBucket2(String jobId) {
        logger.debug("Recuperando PDF do S3. JobId: {}", jobId);
        String fileName = jobId + ".pdf"; // Supondo que você salvou com esse nome

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket("nome-do-bucket-2")
                .key(fileName)
                .build();

        try {
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            logger.info("PDF recuperado com sucesso. JobId: {}, TamanhoBytes: {}", jobId, objectBytes.asByteArray().length);
            return objectBytes.asByteArray();
        } catch (NoSuchKeyException e) {
            logger.warn("PDF não encontrado no S3. JobId: {}", jobId);
            return null;
        } catch (Exception e) {
            logger.error("Erro ao recuperar PDF do S3. JobId: {}", jobId, e);
            throw e;
        }
    }
}
