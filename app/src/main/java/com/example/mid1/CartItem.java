package com.example.mid1;

/**
 * Model class representing a product in the shopping cart.
 * Wraps an items object with a quantity counter.
 */
public class CartItem {
    private items product;
    private int quantity;

    public CartItem(items product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public items getProduct() {
        return product;
    }

    public void setProduct(items product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
