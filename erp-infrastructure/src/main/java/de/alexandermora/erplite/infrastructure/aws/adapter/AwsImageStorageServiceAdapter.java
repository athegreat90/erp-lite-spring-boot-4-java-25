package de.alexandermora.erplite.infrastructure.aws.adapter;

import de.alexandermora.erplite.domain.exception.MyBusinessException;
import de.alexandermora.erplite.domain.port.ImageStorageService;
import de.alexandermora.erplite.domain.product.ProductImage;
import de.alexandermora.erplite.infrastructure.aws.model.AwsConfigModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsImageStorageServiceAdapter implements ImageStorageService {
    private final S3Client awsClient;
    private final AwsConfigModel awsConfig;

    @Override
    public ProductImage upload(String imageName, byte[] imageData) {
        try {
            final var key = "products/" + imageName;

            final var contentType = determineContentType(imageName);

            awsClient.putObject(builder ->
                            builder.bucket(awsConfig.bucketName()).key(key).contentType(contentType).contentLength(((long) imageData.length)),
                    RequestBody.fromBytes(imageData));

            final var imageUrl = buildUrlImg(key);

            log.info("Uploaded image: {} to S3 bucket: {}", key, awsConfig.bucketName());

            return new ProductImage(imageUrl);

        } catch (S3Exception e) {
            log.error("Error uploading image to S3: {}", e.awsErrorDetails().errorMessage(), e);
            throw new MyBusinessException("Error uploading image to S3", e);
        } catch (Exception e) {
            log.error("Unexpected error uploading image to S3: {}", e.getMessage(), e);
            throw new MyBusinessException("Unexpected error uploading image to S3", e);
        }
    }

    @Override
    public void delete(ProductImage img) {
        try {
            final var key = getKeyFromUrl(img.imageUrl());
            awsClient.deleteObject(builder -> builder.bucket(awsConfig.bucketName()).key(key));
            log.info("Deleted image: {}", key);
        } catch (S3Exception e) {
            log.error("Error deleting image from S3: {}", e.awsErrorDetails().errorMessage(), e);
            throw new MyBusinessException("Error deleting image from S3", e);
        } catch (Exception e) {
            log.error("Unexpected error deleting image from S3: {}", e.getMessage(), e);
            throw new MyBusinessException("Unexpected error deleting image from S3", e);
        }
    }

    @Override
    public byte[] download(ProductImage img) {
        try {
            final var key = getKeyFromUrl(img.imageUrl());
            final var getObjectRequest = GetObjectRequest.builder()
                    .bucket(awsConfig.bucketName())
                    .key(key)
                    .build();
            final var bytes = awsClient.getObjectAsBytes(getObjectRequest).asByteArray();
            log.info("Downloaded image: {} bytes", bytes.length);
            return bytes;
        } catch (S3Exception e) {
            log.error("Error downloading image from S3: {}", e.awsErrorDetails().errorMessage(), e);
            throw new MyBusinessException("Error downloading image from S3", e);
        } catch (Exception e) {
            log.error("Unexpected error downloading image from S3: {}", e.getMessage(), e);
            throw new MyBusinessException("Unexpected error downloading image from S3", e);
        }
    }

    private String getKeyFromUrl(String url) {
        var bucketNAme = awsConfig.bucketName();
        var parts = url.split("/" + bucketNAme + "/");
        if (parts.length == 2) {
            return parts[1];
        }
        log.warn("No bucket name found in URL: {}", url);
        return url;
    }

    private String buildUrlImg(String key) {
        return String.format("%s/%s/%s", awsConfig.endpoint(), awsConfig.bucketName(), key);
    }

    private String determineContentType(String filename) {
        final var extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            default -> "application/octet-stream"; // Default binary type
        };
    }

}
