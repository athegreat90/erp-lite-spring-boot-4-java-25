package de.alexandermora.erplite;

import de.alexandermora.erplite.application.command.order.CancelOrderCommand;
import de.alexandermora.erplite.application.command.order.CreateOrderCommand;
import de.alexandermora.erplite.application.command.order.UpdateOrderStatusCommand;
import de.alexandermora.erplite.application.query.*;
import de.alexandermora.erplite.application.usecase.order.CancelOrderUseCase;
import de.alexandermora.erplite.application.usecase.order.CreateOrderUseCase;
import de.alexandermora.erplite.application.usecase.order.UpdateOrderStatusUseCase;
import de.alexandermora.erplite.commons.enums.CatalogType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class ErpLiteApplication implements CommandLineRunner {
    private final FindCatalogByTypeQuery findCatalogByTypeQuery;

    private final FindCatalogItemByCodeQuery findCatalogItemByCodeQuery;

    private final FindCatalogItemsByTypeQuery findCatalogItemsByTypeQuery;

    private final FindProductActiveQuery findProductActiveQuery;

    private final FindProductByCategoryQuery findProductByCategory;

    private final FindProductByIdQuery findProductByIdQuery;

    private final FindProductBySkuQuery findProductBySkuQuery;

    private final FindProductByTextQuery findProductByTextQuery;

    public static void main(String[] args) {
        SpringApplication.run(ErpLiteApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(findCatalogByTypeQuery.execute(CatalogType.PRODUCT_CATEGORIES));
        System.out.println("-------------------------");

        System.out.println(findCatalogItemByCodeQuery.execute(CatalogType.PRODUCT_CATEGORIES, "ELECTRONICS"));
        System.out.println("-------------------------");

        System.out.println(findCatalogItemsByTypeQuery.execute(CatalogType.ORDER_STATUSES));
        System.out.println("-------------------------");

        System.out.println(findProductActiveQuery.execute());
        System.out.println("-------------------------");

        System.out.println(findProductByCategory.execute("cat-electronics"));
        System.out.println("-------------------------");

        System.out.println(findProductByIdQuery.execute("11111111-1111-1111-1111-111111111111"));
        System.out.println("-------------------------");

        System.out.println(findProductBySkuQuery.execute("LAPTOP-001"));
        System.out.println("-------------------------");

        System.out.println(findProductByTextQuery.execute("laptop"));
        System.out.println("-------------------------");


    }
}
