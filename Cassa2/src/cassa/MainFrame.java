package cassa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainFrame extends JFrame {
    private ProductDAO productDAO;
    private OrderDAO orderDAO;  // Aggiungiamo il DAO per gli ordini
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JTextField nameField;
    private JTextField priceField;
    private JTextField quantityField;
    
    // Elementi per la gestione degli ordini
    private JComboBox<Product> productComboBox;
    private JTextField orderQuantityField;
    private DefaultTableModel orderTableModel;
    private JLabel totalLabel;

    private List<OrderDetail> currentOrderDetails;  // Per tenere traccia dell'ordine corrente

    public MainFrame(Connection connection) {
        // Configura il layout base
        setTitle("Sistema di Cassa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Inizializza i DAO
        productDAO = new ProductDAO(connection);
        orderDAO = new OrderDAO(connection);  // Inizializziamo il DAO per gli ordini

        // Lista dei dettagli dell'ordine corrente
        currentOrderDetails = new ArrayList<>();

        // Pannello Prodotti
        JPanel productPanel = createProductPanel();
        add(productPanel, BorderLayout.CENTER);

        // Pannello per Aggiungere/Modificare Prodotti
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);

        // Pannello per pulsanti operazioni
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Pannello per gestire gli ordini
        JPanel orderPanel = createOrderPanel();
        add(orderPanel, BorderLayout.EAST);

        // Carica i dati dei prodotti nella tabella
        loadProductData();
        loadProductComboBox();
    }

    // Crea il pannello per visualizzare e gestire i prodotti
    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Modello per la tabella dei prodotti
        productTableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Prezzo", "Quantità"}, 0);
        productTable = new JTable(productTableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Crea il pannello per aggiungere e modificare i prodotti
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        // Campo nome prodotto
        panel.add(new JLabel("Nome Prodotto:"));
        nameField = new JTextField();
        panel.add(nameField);

        // Campo prezzo prodotto
        panel.add(new JLabel("Prezzo:"));
        priceField = new JTextField();
        panel.add(priceField);

        // Campo quantità disponibile
        panel.add(new JLabel("Quantità:"));
        quantityField = new JTextField();
        panel.add(quantityField);

        return panel;
    }

    // Crea il pannello dei pulsanti
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Pulsante Aggiungi prodotto
        JButton addButton = new JButton("Aggiungi Prodotto");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });
        panel.add(addButton);

        // Pulsante Modifica prodotto
        JButton updateButton = new JButton("Modifica Prodotto");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProduct();
            }
        });
        panel.add(updateButton);

        // Pulsante Elimina prodotto
        JButton deleteButton = new JButton("Elimina Prodotto");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
            }
        });
        panel.add(deleteButton);

        return panel;
    }

    // Crea il pannello per la gestione degli ordini
    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Sezione per selezionare prodotti e quantità
        JPanel orderInputPanel = new JPanel(new GridLayout(3, 2));
        panel.add(orderInputPanel, BorderLayout.NORTH);

        orderInputPanel.add(new JLabel("Prodotto:"));
        productComboBox = new JComboBox<>();
        orderInputPanel.add(productComboBox);

        orderInputPanel.add(new JLabel("Quantità:"));
        orderQuantityField = new JTextField();
        orderInputPanel.add(orderQuantityField);

        // Pulsante per aggiungere un prodotto all'ordine
        JButton addOrderButton = new JButton("Aggiungi all'Ordine");
        addOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProductToOrder();
            }
        });
        orderInputPanel.add(addOrderButton);

        // Tabella per mostrare i dettagli dell'ordine
        orderTableModel = new DefaultTableModel(new Object[]{"Prodotto", "Quantità", "Prezzo Unitario", "Totale"}, 0);
        JTable orderTable = new JTable(orderTableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Pannello per visualizzare il totale dell'ordine
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Totale: 0.0€");
        totalPanel.add(totalLabel);

        // Pulsante per confermare l'ordine
        JButton confirmOrderButton = new JButton("Conferma Ordine");
        confirmOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmOrder();
            }
        });
        totalPanel.add(confirmOrderButton);

        panel.add(totalPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Carica i prodotti nel JComboBox per la selezione durante la creazione di un ordine
    private void loadProductComboBox() {
        try {
            List<Product> products = productDAO.getAllProducts();
            for (Product product : products) {
                productComboBox.addItem(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Aggiunge un prodotto all'ordine corrente
    private void addProductToOrder() {
        Product selectedProduct = (Product) productComboBox.getSelectedItem();
        if (selectedProduct != null) {
            int quantity = Integer.parseInt(orderQuantityField.getText());
            
            if (quantity > selectedProduct.getQuantityAvailable()) {
                JOptionPane.showMessageDialog(this, "Quantità non disponibile per il prodotto selezionato.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double priceEach = selectedProduct.getPrice();
            double totalPrice = priceEach * quantity;

            // Aggiungi i dettagli dell'ordine alla lista
            currentOrderDetails.add(new OrderDetail(0, selectedProduct, quantity, priceEach));

            // Aggiorna la tabella dell'ordine
            orderTableModel.addRow(new Object[]{
                    selectedProduct.getName(),
                    quantity,
                    priceEach,
                    totalPrice
            });

            // Aggiorna il totale dell'ordine
            updateTotal();
        }
    }

 // Aggiorna il totale dell'ordine corrente
    private void updateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderDetail detail : currentOrderDetails) {
            // Moltiplica il prezzo unitario (BigDecimal) per la quantità (int)
            BigDecimal priceEach = detail.getPriceEach();
            BigDecimal quantityOrdered = BigDecimal.valueOf(detail.getQuantityOrdered());
            total = total.add(priceEach.multiply(quantityOrdered));
        }
        totalLabel.setText("Totale: " + total + "€");
    }

    // Conferma e salva l'ordine nel database
    private void confirmOrder() {
        Order order = new Order(0, new Date(), currentOrderDetails);
        try {
            orderDAO.addOrder(order);
            JOptionPane.showMessageDialog(this, "Ordine confermato con successo.", "Successo", JOptionPane.INFORMATION_MESSAGE);
            
            // Reset dell'ordine corrente
            currentOrderDetails.clear();
            orderTableModel.setRowCount(0);
            updateTotal();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore durante il salvataggio dell'ordine.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Carica i dati dei prodotti nella tabella
    private void loadProductData() {
        try {
            List<Product> products = productDAO.getAllProducts();
            for (Product product : products) {
                productTableModel.addRow(new Object[]{
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getQuantityAvailable()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Aggiungi un nuovo prodotto
    private void addProduct() {
        String name = nameField.getText();
        double price = Double.parseDouble(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());

        Product product = new Product(0, name, price, quantity);
        try {
            productDAO.addProduct(product);
            loadProductData();  // Aggiorna la tabella
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modifica un prodotto esistente
    private void updateProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            int productId = (int) productTableModel.getValueAt(selectedRow, 0);
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            Product product = new Product(productId, name, price, quantity);
            try {
                productDAO.updateProduct(product);
                loadProductData();  // Aggiorna la tabella
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleziona un prodotto da modificare.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Elimina un prodotto esistente
    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            int productId = (int) productTableModel.getValueAt(selectedRow, 0);
            try {
                productDAO.deleteProduct(productId);
                loadProductData();  // Aggiorna la tabella
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleziona un prodotto da eliminare.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
 // Metodo main per avviare l'applicazione
    public static void main(String[] args) {
        // Impostiamo la GUI affinché utilizzi il look and feel di sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configuriamo la connessione al database
        Connection connection = null;
        try {
            // Modifica i parametri con i tuoi dati del database
            String url = "jdbc:mysql://localhost:3306/cash_register";
            String user = "root";
            String password = "marina97!";
            
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore durante la connessione al database", "Errore", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Esce dall'applicazione se la connessione fallisce
        }

        // Crea e mostra la finestra principale
        MainFrame frame = new MainFrame(connection);
        frame.setVisible(true);
    }
}

