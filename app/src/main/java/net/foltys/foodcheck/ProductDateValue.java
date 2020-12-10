package net.foltys.foodcheck;

public class ProductDateValue {
    private final String date;
    private double value;

    /**
     * Constructor of ProductDateValue
     *
     * @param date  Date of product scan
     * @param value Parameter of scan
     */
    public ProductDateValue(String date, double value) {
        this.date = date;
        this.value = value;
    }

    /**
     * Getter of Date field
     *
     * @return Date of scan
     */
    public String getDate() {
        return date;
    }

    /**
     * Getter of Value field
     *
     * @return Product parameter value
     */
    public double getValue() {
        return value;
    }

    /**
     * Setter of Value field
     *
     * @param value Product parameter value
     */
    public void setValue(double value) {
        this.value = value;
    }
}
