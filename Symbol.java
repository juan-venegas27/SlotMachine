/**
 * Represents an individual visual symbol displayed inside a wheel slot.
 */
public class Symbol {
    private String color;
    private Rectangle shape;
    private boolean isVisible;

    /**
     * Constructs a symbol with the specified color.
     * @param color CSS name or hex code of the symbol.
     */
    public Symbol(String color) {
        this.color = color;
        this.shape = new Rectangle();
        this.shape.changeSize(74, 46);
        this.shape.changeColor(color);
        this.isVisible = false;
    }

    /**
     * Gets the color value of this symbol.
     * @return Color name or hex code.
     */
    public String getColor() {
        return color;
    }

    /**
     * Changes the visual color of this symbol.
     * @param newColor New CSS name or hex code.
     */
    public void changeColor(String newColor) {
        this.color = newColor;
        this.shape.changeColor(newColor);
    }

    /**
     * Moves the symbol to the specified coordinates.
     * @param x Horizontal pixel position.
     * @param y Vertical pixel position.
     */
    public void moveTo(int x, int y) {
        shape.moveTo(x, y);
    }

    /**
     * Makes this symbol visible on canvas.
     */
    public void makeVisible() {
        isVisible = true;
        shape.makeVisible();
    }

    /**
     * Hides this symbol from the canvas.
     */
    public void makeInvisible() {
        isVisible = false;
        shape.makeInvisible();
    }

    /**
     * Checks if this symbol matches a given color string.
     * @param otherColor Color string to check against.
     * @return True if colors match ignoring case.
     */
    public boolean matches(String otherColor) {
        return this.color.equalsIgnoreCase(otherColor);
    }
}