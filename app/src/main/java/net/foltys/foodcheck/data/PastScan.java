package net.foltys.foodcheck.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "past_scans_table")
public class PastScan {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String barcode;
    private String name;
    private double weight;
    private String date;
    private String hour;
    private double energy;
    private double carbohydrates;
    private double protein;
    private double fat;
    private double saturates;
    private double sugars;
    private double fibre;
    private double salt;

    public PastScan(int id, String barcode, String name, double weight, String date, String hour, double energy, double carbohydrates, double protein, double fat, double saturates, double sugars, double fibre, double salt) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.weight = weight;
        this.date = date;
        this.hour = hour;
        this.energy = energy;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fat = fat;
        this.saturates = saturates;
        this.sugars = sugars;
        this.fibre = fibre;
        this.salt = salt;
    }

    public int getId() {
        return id;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public String getDate() {
        return date;
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

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
