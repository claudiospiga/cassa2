package cassa;

import java.math.BigDecimal;

public class OrderDetail {
    private int id;
    private Product product;
    private int quantityOrdered;
    private BigDecimal priceEach;

    public OrderDetail(int id, Product product, int quantityOrdered, double priceEach) {
        this.id = id;
        this.product = product;
        this.quantityOrdered = quantityOrdered;
        this.priceEach = BigDecimal.valueOf(priceEach);
    }

    public int getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantityOrdered() {
        return quantityOrdered;
    }

    public BigDecimal getPriceEach() {
        return priceEach;
    }
}
    