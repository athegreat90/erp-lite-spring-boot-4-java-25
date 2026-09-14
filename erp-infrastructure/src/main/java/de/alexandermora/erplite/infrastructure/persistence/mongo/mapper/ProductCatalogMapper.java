package de.alexandermora.erplite.infrastructure.persistence.mongo.mapper;

import de.alexandermora.erplite.domain.views.ProductView;
import de.alexandermora.erplite.infrastructure.persistence.mongo.document.ProductInCatalogDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

/**
 * Maps the {@link ProductInCatalogDocument} (Infrastructure/Mongo) to the {@link ProductView}
 * (Domain), one-directional (Document -&gt; DTO) read model.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProductCatalogMapper {

    @Mapping(target = "money", expression = "java(toMoney(document.getPrice(), document.getCurrency()))")
    ProductView toView(ProductInCatalogDocument document);

    /**
     * Formats price and currency as {@code "<ISO currency code> <plain amount>"},
     * e.g. {@code "USD 12.99"}. Returns {@code null} if either input is missing.
     */
    default String toMoney(BigDecimal price, String currency) {
        if (price == null || currency == null) {
            return null;
        }
        return currency + " " + price.toPlainString();
    }
}
