package net.foltys.foodcheck;

import org.jetbrains.annotations.NotNull;

public class FoodProduct {
    private int id;
    private String name;
    private String barcode;
    private int energy;
    private double fat;
    private double saturates;
    private double carbohydrates;
    private double sugar;
    private double fibre;
    private double protein;
    private double salt;
    private double weight;
    private String url;
    private boolean favorite;

    /**
     * Constructor of FoodProduct class. Object of this class is being created while receiving response from REST API. Structure equal to database structure
     *
     * @param id            Product's id from the database
     * @param name          Product's name
     * @param barcode       Product's barcode
     * @param energy        Product's energy
     * @param fat           Product's fat
     * @param saturates     Product's saturates
     * @param carbohydrates Product's carbohydrates
     * @param sugar         Product's sugar
     * @param fibre         Product's fibre
     * @param protein       Product's protein
     * @param salt          Product's salt
     * @param weight        Product's weight
     * @param url           Product's image URL
     * @param favorite      Defines if the product is on the favorite list
     */
    public FoodProduct(int id, String name, String barcode, int energy, double fat, double saturates, double carbohydrates, double sugar, double fibre, double protein, double salt, double weight, String url, boolean favorite) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.energy = energy;
        this.fat = fat;
        this.saturates = saturates;
        this.carbohydrates = carbohydrates;
        this.sugar = sugar;
        this.fibre = fibre;
        this.protein = protein;
        this.salt = salt;
        this.weight = weight;
        this.url = url;
        this.favorite = favorite;
    }

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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
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

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    public double getFibre() {
        return fibre;
    }

    public void setFibre(double fibre) {
        this.fibre = fibre;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getSalt() {
        return salt;
    }

    public void setSalt(double salt) {
        this.salt = salt;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    /**
     * Method joins all fields into one JSON String
     *
     * @return String of joined data in JSON format
     */
    @NotNull
    @Override
    public String toString() {
        return "FoodProduct{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", barcode='" + barcode + '\'' +
                ", energy=" + energy +
                ", fat=" + fat +
                ", saturates=" + saturates +
                ", carbohydrates=" + carbohydrates +
                ", sugar=" + sugar +
                ", fibre=" + fibre +
                ", protein=" + protein +
                ", salt=" + salt +
                ", weight=" + weight +
                ", url='" + url + '\'' +
                ", favorite=" + favorite +
                '}';
    }
}


