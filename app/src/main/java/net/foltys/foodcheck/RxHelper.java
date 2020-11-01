package net.foltys.foodcheck;

public class RxHelper {
    private int position;
    private Boolean value;

    public RxHelper(int position, Boolean value) {
        this.position = position;
        this.value = value;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
