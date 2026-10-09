package SLOTMACHINEFINAL;

/**
 * Symbol that alternates its visibility every time it is selected.
 */
public class ShySymbol extends Symbol {

    /**
     * Constructs a shy symbol with the specified color.
     * @param color CSS name or hex code of the symbol.
     */
    public ShySymbol(String color) {
        super(color);
    }

    /**
     * Alternates whether the symbol can be displayed.
     */
    @Override
    public void onSelected() {
        setDisplayEnabled(!isDisplayEnabled());
    }
}
