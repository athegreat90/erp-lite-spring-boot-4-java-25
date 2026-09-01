package de.alexandermora.erplite.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductImage Domain Test")
class CoProductImageTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should throw IllegalArgumentException when imageUrl is null or blank")
    void shouldThrowIllegalArgumentExceptionWhenImageUrlIsNullOrBlank(String imageUrl) {
        var exception = assertThrows(IllegalArgumentException.class, () -> new ProductImage(imageUrl));
        assertEquals("imageUrl must not be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when imageUrl has an illegal character (URISyntaxException)")
    void shouldThrowIllegalArgumentExceptionOnUriSyntaxError() {
        var exception = assertThrows(IllegalArgumentException.class, () -> new ProductImage("not a url"));
        assertEquals("Invalid image URL: not a url", exception.getMessage());
        assertInstanceOf(java.net.URISyntaxException.class, exception.getCause());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when imageUrl is a relative (non-absolute) URI")
    void shouldThrowIllegalArgumentExceptionOnNonAbsoluteUri() {
        var exception = assertThrows(IllegalArgumentException.class, () -> new ProductImage("just-text"));
        assertEquals("Invalid image URL: just-text", exception.getMessage());
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }

    @Test
    @DisplayName("Should create ProductImage for a valid absolute URL")
    void shouldCreateProductImageForValidUrl() {
        var image = ProductImage.of("https://example.com/images/product.png");
        assertEquals("https://example.com/images/product.png", image.imageUrl());
    }

    @Test
    @DisplayName("Should return the full URL unchanged from getFullUrl")
    void shouldReturnFullUrlUnchanged() {
        var image = new ProductImage("https://example.com/images/product.png");
        assertEquals("https://example.com/images/product.png", image.getFullUrl());
    }

    @Test
    @DisplayName("Should return the substring after the last slash from getFileName")
    void shouldReturnFileNameAfterLastSlash() {
        var image = new ProductImage("https://example.com/images/product.png");
        assertEquals("product.png", image.getFileName());
    }

    @Test
    @DisplayName("Should return empty string from getFileName when URL ends with a slash")
    void shouldReturnEmptyFileNameWhenUrlEndsWithSlash() {
        var image = new ProductImage("https://example.com/images/");
        assertEquals("", image.getFileName());
    }

    @Test
    @DisplayName("Should return the full URL from getFileName when it contains no slash")
    void shouldReturnFullUrlFromGetFileNameWhenNoSlashPresent() {
        var image = new ProductImage("mailto:user@example.com");
        assertEquals("mailto:user@example.com", image.getFileName());
    }
}