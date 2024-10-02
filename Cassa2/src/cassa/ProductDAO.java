package cassa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection connection;

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    // Metodo per ottenere tutti i prodotti
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            int id = resultSet.getInt("product_id");
            String name = resultSet.getString("product_name");
            double price = resultSet.getDouble("product_price");
            int quantityAvailable = resultSet.getInt("quantity_available");

            products.add(new Product(id, name, price, quantityAvailable));
        }

        return products;
    }

    // Metodo per aggiungere un nuovo prodotto
    public void addProduct(Product product) throws SQLException {
        String query = "INSERT INTO products (product_name, product_price, quantity_available) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, product.getName());
        preparedStatement.setDouble(2, product.getPrice());
        preparedStatement.setInt(3, product.getQuantityAvailable());
        preparedStatement.executeUpdate();
    }

    // Metodo per aggiornare un prodotto esistente
    public void updateProduct(Product product) throws SQLException {
        String query = "UPDATE products SET product_name = ?, product_price = ?, quantity_available = ? WHERE product_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, product.getName());
        preparedStatement.setDouble(2, product.getPrice());
        preparedStatement.setInt(3, product.getQuantityAvailable());
        preparedStatement.setInt(4, product.getId());
        preparedStatement.executeUpdate();
    }

    // Metodo per eliminare un prodotto
    public void deleteProduct(int productId) throws SQLException {
        String query = "DELETE FROM products WHERE product_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, productId);
        preparedStatement.executeUpdate();
    }

    // Metodo per trovare un prodotto tramite ID
    public Product findProductById(int productId) throws SQLException {
        String query = "SELECT * FROM products WHERE product_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, productId);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String name = resultSet.getString("product_name");
            double price = resultSet.getDouble("product_price");
            int quantityAvailable = resultSet.getInt("quantity_available");

            return new Product(productId, name, price, quantityAvailable);
        }

        return null;
    }
}
