package de.alexandermora.erplite.domain.port;

import de.alexandermora.erplite.domain.product.ProductImage;

public interface ImageStorageService {
    ProductImage upload(String imageName, byte[] imageData);

    void delete(ProductImage img);

    byte[] download(ProductImage img);
}
