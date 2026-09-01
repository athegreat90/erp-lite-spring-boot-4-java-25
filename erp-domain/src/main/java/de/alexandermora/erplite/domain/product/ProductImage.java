package de.alexandermora.erplite.domain.product;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.MalformedURLException;

/**
 * Product image URL stored in AWS S3.
 */
public record ProductImage(String imageUrl) {

    public ProductImage {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("imageUrl must not be blank");
        }
        try {
            new URI(imageUrl).toURL();
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid image URL: " + imageUrl, e);
        }
    }

    public static ProductImage of(String imageUrl) {
        return new ProductImage(imageUrl);
    }

    public String getFullUrl() {
        return imageUrl;
    }

    public String getFileName() {
        int idx = imageUrl.lastIndexOf('/');
        return idx >= 0 ? imageUrl.substring(idx + 1) : imageUrl;
    }
}