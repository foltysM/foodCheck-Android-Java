package net.foltys.foodcheck;

public class ProductDateValue {
    private String date;
    private double value;

    public ProductDateValue(String date, double value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
