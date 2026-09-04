package de.alexandermora.erplite.domain.port;

import de.alexandermora.erplite.domain.product.ProductImage;

/*
* Port for storage and retrieving product images
* */
public interface ImageStorageService {

    ProductImage upload(String imageName, byte[] imageData);

    void delete(ProductImage img);

    byte[] download(ProductImage img);
}
