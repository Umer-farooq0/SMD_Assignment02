package com.example.mid1;

public class items {
    String name;
    String price;
    int image;
    String description;
    String details;

    public items(String name, String price, int image, String description, String details, boolean isFav) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.description = description;
        this.details = details;
        this.isFav = isFav;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    boolean isFav;
}
