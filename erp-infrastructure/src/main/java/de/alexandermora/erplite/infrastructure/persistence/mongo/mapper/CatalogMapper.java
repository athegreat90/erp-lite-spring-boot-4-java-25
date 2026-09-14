package de.alexandermora.erplite.infrastructure.persistence.mongo.mapper;

import de.alexandermora.erplite.domain.views.CatalogView;
import de.alexandermora.erplite.domain.views.ItemsView;
import de.alexandermora.erplite.infrastructure.persistence.mongo.document.CatalogDocument;
import de.alexandermora.erplite.infrastructure.persistence.mongo.document.CatalogItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Maps the {@link CatalogDocument} (Infrastructure/Mongo) to the {@link CatalogView}
 * (Domain), one-directional (Document -&gt; DTO) read model.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CatalogMapper {

    @Mapping(source = "catalogType", target = "type")
    CatalogView toView(CatalogDocument document);

    ItemsView toItemView(CatalogItem item);
}
