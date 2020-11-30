package net.foltys.foodcheck;

public class RxHelper {
    private final int position;
    private final Boolean value;

    public RxHelper(int position, Boolean value) {
        this.position = position;
        this.value = value;
    }

    public int getPosition() {
        return position;
    }

    public Boolean getValue() {
        return value;
    }

}
