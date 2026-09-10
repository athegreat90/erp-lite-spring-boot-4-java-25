package de.alexandermora.erplite.domain.port.service;

import de.alexandermora.erplite.domain.entity.product.ProductImage;

/*
* Port for storage and retrieving product images
* */
public interface ImageStorageServicePort {

    ProductImage upload(String imageName, byte[] imageData);

    void delete(ProductImage img);

    byte[] download(ProductImage img);
}
