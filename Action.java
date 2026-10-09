package SLOTMACHINEFINAL;

/**
 * Represents one rotation action performed on the slot machine.
 */
public class Action {
    private int wheel;
    private int steps;

    /**
     * Creates an action.
     * @param wheel Number of the wheel to rotate.
     * @param steps Number of rotation steps.
     */
    public Action(int wheel, int steps) {
        this.wheel = wheel;
        this.steps = steps;
    }

    /**
     * Returns the wheel number.
     * @return Wheel number.
     */
    public int getWheel() {
        return wheel;
    }

    /**
     * Returns the number of steps.
     * @return Number of steps.
     */
    public int getSteps() {
        return steps;
    }
}
