package cassa;

import java.sql.*;
import java.util.List;

import java.sql.*;
import java.util.List;

import java.sql.*;
import java.util.List;

public class OrderDAO {
    private Connection connection;

    public OrderDAO(Connection connection) {
        this.connection = connection;
    }

    // Aggiunge un nuovo ordine nel database
    public void addOrder(Order order) throws SQLException {
        String orderQuery = "INSERT INTO orders (order_date) VALUES (CURRENT_TIMESTAMP)";
        String orderDetailQuery = "INSERT INTO order_details (order_id, product_id, quantity_ordered, price_each) VALUES (?, ?, ?, ?)";

        try (PreparedStatement orderStmt = connection.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement orderDetailStmt = connection.prepareStatement(orderDetailQuery)) {

            // Inserisci l'ordine nella tabella `orders`
            orderStmt.executeUpdate();

            // Ottieni l'ID generato per l'ordine
            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int orderId = generatedKeys.getInt(1);

                // Inserisci i dettagli dell'ordine nella tabella `order_details`
                for (OrderDetail detail : order.getOrderDetails()) {
                    orderDetailStmt.setInt(1, orderId);
                    orderDetailStmt.setInt(2, detail.getProduct().getId());
                    orderDetailStmt.setInt(3, detail.getQuantityOrdered());
                    orderDetailStmt.setBigDecimal(4, detail.getPriceEach());
                    orderDetailStmt.executeUpdate();

                    // Aggiorna la quantità disponibile del prodotto nella tabella `products`
                    String updateProductQuantityQuery = "UPDATE products SET quantity_available = quantity_available - ? WHERE product_id = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateProductQuantityQuery)) {
                        updateStmt.setInt(1, detail.getQuantityOrdered());
                        updateStmt.setInt(2, detail.getProduct().getId());
                        updateStmt.executeUpdate();
                    }
                }
            }
        }
    }
}
