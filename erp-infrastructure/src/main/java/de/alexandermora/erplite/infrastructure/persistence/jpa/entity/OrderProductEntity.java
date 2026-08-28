package de.alexandermora.erplite.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Maps the {@code order_products} table (order detail lines).
 * {@code product_name} is a snapshot of the product name at order-creation time and
 * is kept as a plain column alongside the {@code product} association.
 */
@Entity
@Table(name = "order_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    /**
     * Derives the snapshot {@code productName} and the {@code subtotal} before insert.
     * Each assignment is guarded by a null check; the {@code @Column(nullable = false)}
     * mappings and the DB {@code CHECK} constraints still reject a truly incomplete row.
     */
    @PrePersist
    void applyDerivedValues() {
        if (product != null && (product.getName() != null && !product.getName().isBlank())) {
            productName = product.getName();
        }
        if (unitPrice != null && quantity > 0) {
            subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}