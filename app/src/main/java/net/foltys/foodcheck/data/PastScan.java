package net.foltys.foodcheck.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "past_scans_table")
public class PastScan implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String barcode;
    private String name;
    private double weight;
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minutes;
    private double energy;
    private double carbohydrates;
    private double protein;
    private double fat;
    private double saturates;
    private double sugars;
    private double fibre;
    private double salt;
    private String url;
    private double percentEaten;

    public PastScan(String barcode, String name, double weight, int day, int month, int year, int hour, int minutes, double energy, double carbohydrates, double protein, double fat, double saturates, double sugars, double fibre, double salt, String url, double percentEaten) {
        this.barcode = barcode;
        this.name = name;
        this.weight = weight;
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minutes = minutes;
        this.energy = energy;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fat = fat;
        this.saturates = saturates;
        this.sugars = sugars;
        this.fibre = fibre;
        this.salt = salt;
        this.url = url;
        this.percentEaten = percentEaten;
    }

    @Ignore
    public PastScan() {

    }

    public static final Creator<PastScan> CREATOR = new Creator<PastScan>() {
        @Override
        public PastScan createFromParcel(Parcel in) {
            return new PastScan(in);
        }

        @Override
        public PastScan[] newArray(int size) {
            return new PastScan[size];
        }
    };

    protected PastScan(Parcel in) {
        id = in.readInt();
        barcode = in.readString();
        name = in.readString();
        weight = in.readDouble();
        day = in.readInt();
        month = in.readInt();
        year = in.readInt();
        hour = in.readInt();
        minutes = in.readInt();
        energy = in.readDouble();
        carbohydrates = in.readDouble();
        protein = in.readDouble();
        fat = in.readDouble();
        saturates = in.readDouble();
        sugars = in.readDouble();
        fibre = in.readDouble();
        salt = in.readDouble();
        url = in.readString();
        percentEaten = in.readDouble();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPercentEaten() {
        return percentEaten;
    }

    public void setPercentEaten(double percentEaten) {
        this.percentEaten = percentEaten;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(barcode);
        dest.writeString(name);
        dest.writeDouble(weight);
        dest.writeInt(day);
        dest.writeInt(month);
        dest.writeInt(year);
        dest.writeInt(hour);
        dest.writeInt(minutes);
        dest.writeDouble(energy);
        dest.writeDouble(carbohydrates);
        dest.writeDouble(protein);
        dest.writeDouble(fat);
        dest.writeDouble(saturates);
        dest.writeDouble(sugars);
        dest.writeDouble(fibre);
        dest.writeDouble(salt);
        dest.writeString(url);
        dest.writeDouble(percentEaten);
    }
}
