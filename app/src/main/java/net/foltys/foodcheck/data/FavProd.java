package net.foltys.foodcheck.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fav_prod_table")
public class FavProd {
    @NonNull
    @PrimaryKey()
    private String barcode;
    private String name;
    private double weight;
    private double energy;
    private double carbohydrates;
    private double protein;
    private double fat;
    private double saturates;
    private double sugars;
    private double fibre;
    private double salt;
    private String url;

    /**
     * The constructor of FavProd class
     *
     * @param barcode       Barcode of the scanned product
     * @param name          Name of the product
     * @param weight        Weight value of the product
     * @param energy        Energy value of the product
     * @param carbohydrates Carbohydrates value of the product
     * @param protein       Protein value of the product
     * @param fat           Fat value of the product
     * @param saturates     Saturates value of the product
     * @param sugars        Sugars value of the product
     * @param fibre         Fibre value of the product
     * @param salt          Salt value of the product
     * @param url           URL to the picture of the product
     */

    public FavProd(@NonNull String barcode, String name, double weight, double energy, double carbohydrates, double protein, double fat, double saturates, double sugars, double fibre, double salt, String url) {
        this.barcode = barcode;
        this.name = name;
        this.weight = weight;
        this.energy = energy;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fat = fat;
        this.saturates = saturates;
        this.sugars = sugars;
        this.fibre = fibre;
        this.salt = salt;
        this.url = url;
    }

    @NonNull
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(@NonNull String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getSaturates() {
        return saturates;
    }

    public void setSaturates(double saturates) {
        this.saturates = saturates;
    }

    public double getSugars() {
        return sugars;
    }

    public void setSugars(double sugars) {
        this.sugars = sugars;
    }

    public double getFibre() {
        return fibre;
    }

    public void setFibre(double fibre) {
        this.fibre = fibre;
    }

    public double getSalt() {
        return salt;
    }

    public void setSalt(double salt) {
        this.salt = salt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}




