package cassa;

public class Product {
    private int id;
    private String name;
    private double price;
    private int quantityAvailable;

    // Costruttore
    public Product(int id, String name, double price, int quantityAvailable) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantityAvailable = quantityAvailable;
    }

    // Getter e Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    @Override
    public String toString() {
        return name + " - " + price + "€";
    }
}
