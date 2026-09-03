package de.alexandermora.erplite.infrastructure.rest.customer.mapper;

import de.alexandermora.erplite.domain.customer.CustomerInfo;
import de.alexandermora.erplite.infrastructure.rest.customer.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Maps the JSONPlaceholder {@link UserDTO} to the domain {@link CustomerInfo}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CustomerMapper {

    @Mapping(target = "address", source = "address.street")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "zipCode", source = "address.zipcode")
    @Mapping(target = "companyName", source = "company.name")
    CustomerInfo toCustomerInfo(UserDTO user);
}