package de.alexandermora.erplite.application.command;

import de.alexandermora.erplite.application.command.product.*;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class ProductCommandsTest {

    private static final String UUID_STR = UUID.randomUUID().toString();

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void createProductCommand_validPayloadPassesValidationAndHelpers() {
        CreateProductCommand cmd = new CreateProductCommand(
                "LAPTOP-001", "Gaming Laptop", "16GB RAM",
                new BigDecimal("999.99"), "USD", 10, "cat-electronics",
                new byte[1], "image.png", "alice");

        assertThat(validator.validate(cmd)).isEmpty();
        assertThat(cmd.hasImage()).isTrue();
    }

    @Test
    void createProductCommand_reportsConstraintViolations() {
        CreateProductCommand cmd = new CreateProductCommand(
                "bad", "ab", null,
                BigDecimal.ZERO, "USD", -1, " ",
                null, null, " ");

        assertThat(validator.validate(cmd))
                .extracting(v -> v.getPropertyPath().toString())
                .contains("sku", "name", "price", "stock", "categoryId", "createdBy");
        assertThat(cmd.hasImage()).isFalse();
    }

    @Test
    void createProductCommand_flagsMalformedCurrency() {
        assertThat(validator.validate(new CreateProductCommand(
                "LAPTOP-001", "Gaming Laptop", null, BigDecimal.ONE, "us", 1,
                "cat-electronics", null, null, "alice")))
                .extracting(v -> v.getPropertyPath().toString())
                .contains("currency");
    }

    @Test
    void createProductCommand_compactConstructorRejectsMissingImageName() {
        assertThatIllegalArgumentException().isThrownBy(() -> new CreateProductCommand(
                "LAPTOP-001", "Gaming Laptop", null, BigDecimal.ONE, "USD", 1,
                "cat-electronics", new byte[1], null, "alice"));
    }

    @Test
    void updateProductCommand_partialUpdateHelpers() {
        UpdateProductCommand cmd = new UpdateProductCommand(
                UUID_STR, "New name", null, null, null, null, null);

        assertThat(validator.validate(cmd)).isEmpty();
        assertThat(cmd.shouldUpdateName()).isTrue();
        assertThat(cmd.shouldUpdatePrice()).isFalse();
        assertThat(cmd.shouldUpdateCategory()).isFalse();
        assertThat(cmd.hasImage()).isFalse();
        assertThat(cmd.productId()).isEqualTo(UUID_STR);
    }

    @Test
    void updateProductCommand_rejectsNoOpAndAllowsPriceOnlyOrNonUuidId() {
        assertThatIllegalArgumentException().isThrownBy(() -> new UpdateProductCommand(
                UUID_STR, null, null, null, null, null, null));

        assertThatCode(() -> new UpdateProductCommand(
                UUID_STR, null, null, new BigDecimal("5.00"), null, null, null))
                .doesNotThrowAnyException();

        assertThatCode(() -> new UpdateProductCommand(
                "not-a-uuid", "New name", null, null, null, null, null))
                .doesNotThrowAnyException();
    }

    @Test
    void updateStockCommand_directionHelpers() {
        UpdateStockCommand inc = new UpdateStockCommand(UUID_STR, StockOperation.INCREMENT, 5, "restock");

        assertThat(validator.validate(inc)).isEmpty();
        assertThat(inc.isIncrement()).isTrue();
        assertThat(inc.isDecrement()).isFalse();

        assertThat(validator.validate(
                new UpdateStockCommand(UUID_STR, StockOperation.DECREMENT, -1, "")))
                .extracting(v -> v.getPropertyPath().toString())
                .contains("quantity", "reason");
    }

    @Test
    void deactivateProductCommand_validatesUuid() {
        assertThatCode(() -> new DeactivateProductCommand(UUID_STR)).doesNotThrowAnyException();
        assertThat(validator.validate(new DeactivateProductCommand(" ")))
                .extracting(v -> v.getPropertyPath().toString())
                .contains("productId");
    }
}