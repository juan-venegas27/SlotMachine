package SLOTMACHINEFINAL;

/**
 * Wheel that cannot be locked, exchanged or deleted.
 */
public class RebelWheel extends Wheel {
    /**
     * Creates a rebel wheel at the default position.
     */
    public RebelWheel() {
        super();
        setFrameColor(unlockedFrameColor());
    }

    /**
     * Creates a rebel wheel at a specific position.
     * @param posx Initial horizontal position.
     * @param posy Initial vertical position.
     */
    public RebelWheel(int posx, int posy) {
        super(posx, posy);
        setFrameColor(unlockedFrameColor());
    }

    /**
     * Indicates whether the wheel can be locked.
     * @return Always false.
     */
    @Override
    public boolean canLock() {
        return false;
    }

    /**
     * Indicates whether the wheel can be exchanged.
     * @return Always false.
     */
    @Override
    public boolean canSwap() {
        return false;
    }

    /**
     * Indicates whether the wheel can be deleted.
     * @return Always false.
     */
    @Override
    public boolean canDelete() {
        return false;
    }

    /**
     * Returns the visual color used for a rebel wheel.
     * @return Rebel frame color.
     */
    @Override
    protected String unlockedFrameColor() {
        return "#C62828";
    }
}
