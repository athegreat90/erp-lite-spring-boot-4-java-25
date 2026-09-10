package de.alexandermora.erplite.application.command.product;

/**
 * Direction of a stock adjustment requested via {@link UpdateStockCommand}.
 */
public enum StockOperation {
    INCREMENT,
    DECREMENT
}