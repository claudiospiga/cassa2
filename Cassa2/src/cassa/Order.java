package cassa;

import java.util.Date;
import java.util.List;

import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private Date orderDate;
    private List<OrderDetail> orderDetails;

    public Order(int id, Date orderDate, List<OrderDetail> orderDetails) {
        this.id = id;
        this.orderDate = orderDate;
        this.orderDetails = orderDetails;
    }

    public int getId() {
        return id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }
}
