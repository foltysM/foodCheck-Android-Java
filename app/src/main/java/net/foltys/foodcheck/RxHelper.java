package net.foltys.foodcheck;

public class RxHelper {
    private final int position;
    private final Boolean value;

    /**
     * Constructor of the helper class for passing data with ReactiveX
     *
     * @param position Position on a list of the element that has been changed
     * @param value    The value that the element should be changed to
     */
    public RxHelper(int position, Boolean value) {
        this.position = position;
        this.value = value;
    }

    /**
     * Getter of the position field
     *
     * @return Position of the element on a list
     */
    public int getPosition() {
        return position;
    }

    /**
     * Getter of the value field
     *
     * @return Value that the element should be change to
     */
    public Boolean getValue() {
        return value;
    }

}
