package SLOTMACHINEFINAL;

/**
 * Wheel that copies the current state of the wheel immediately to its left.
 */
public class LeftyWheel extends Wheel {

    /**
     * Creates a lefty wheel at the default position.
     */
    public LeftyWheel() {
        super();
        setFrameColor(unlockedFrameColor());
    }

    /**
     * Creates a lefty wheel at a specific position.
     * @param posx Initial horizontal position.
     * @param posy Initial vertical position.
     */
    public LeftyWheel(int posx, int posy) {
        super(posx, posy);
        setFrameColor(unlockedFrameColor());
    }

    /**
     * Copies the current state of the wheel to the left.
     */
    @Override
    protected void afterSpinStep() {
        Wheel left = getLeftWheel();
        if (left != null) {
            copyCurrentSymbol(left);
        }
    }

    /**
     * Returns the visual color used for a lefty wheel.
     * @return Lefty frame color.
     */
    @Override
    protected String unlockedFrameColor() {
        return "#5C6BC0";
    }
}
