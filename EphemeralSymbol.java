package SLOTMACHINEFINAL;

/**
 * Symbol that decreases its circular size after every wheel spin.
 */
public class EphemeralSymbol extends Symbol {
    private static final int INITIAL_DIAMETER = 42;
    private static final int DECREASE = 7;
    private static final int MIN_DIAMETER = 10;

    /**
     * Constructs an ephemeral circular symbol.
     * @param color CSS name or hex code of the symbol.
     */
    public EphemeralSymbol(String color) {
        super(color,new Circle(),INITIAL_DIAMETER,INITIAL_DIAMETER);
    }

    /**
     * Decreases the diameter after a wheel spin.
     */
    @Override
    public void onSpin() {
        decreaseSize();
    }

    /**
     * Decreases the diameter while keeping a minimum point.
     */
    private void decreaseSize() {
        int newDiameter = Math.max(MIN_DIAMETER,getShapeWidth() - DECREASE);
        changeShapeSize(newDiameter,newDiameter);
    }
}
