package net.foltys.foodcheck.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fav_prod_table")
public class FavProd {
    @NonNull
    @PrimaryKey(autoGenerate = false)
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

    public FavProd(@NonNull String barcode, String name, double weight, double energy, double carbohydrates, double protein, double fat, double saturates, double sugars, double fibre, double salt) {
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
    }

    @NonNull
    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public double getEnergy() {
        return energy;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public double getProtein() {
        return protein;
    }

    public double getFat() {
        return fat;
    }

    public double getSaturates() {
        return saturates;
    }

    public double getSugars() {
        return sugars;
    }

    public double getFibre() {
        return fibre;
    }

    public double getSalt() {
        return salt;
    }
}


