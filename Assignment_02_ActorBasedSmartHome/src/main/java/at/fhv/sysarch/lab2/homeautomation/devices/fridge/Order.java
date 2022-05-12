package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

public class Order {
    private Product product;
    private int quantity;

    public Order(Product aProduct, int aQuantity) {
        this.product = aProduct;
        this.quantity = aQuantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}
