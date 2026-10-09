package SLOTMACHINEFINAL;

/**
 * Represents a wildcard symbol that can match any color.
 */
public class WildcardSymbol extends Symbol {
    private static final int SIZE = 50;
    
    /**
     * Constructs a wildcard symbol.
     * @param color Visual color of the wildcard.
     */
    public WildcardSymbol(String color) {
        super(color,new Triangle(),SIZE,SIZE);
    }

    /**
     * Indicates whether this symbol is a wildcard.
     * @return True because this symbol can match any color.
     */
    @Override
    public boolean isWildcard() {
        return true;
    }

    /**
     * Moves the triangle to the center of the wheel.
     * @param x Horizontal reference position.
     * @param y Vertical reference position.
     */
    @Override
    public void moveTo(int x, int y) {
        super.moveTo(x + 23,y - 5);
    }
}
