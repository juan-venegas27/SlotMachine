/**
 * Represents an individual visual symbol displayed inside a wheel slot.
 */
public class Symbol {
    protected static final int INITIAL_HEIGHT = 74;
    protected static final int INITIAL_WIDTH = 46;
    private String color;
    private Figure shape;
    private boolean isVisible;
    private boolean displayEnabled;
    private int height;
    private int width;
    private int centerX;
    private int centerY;
    /**
     * Constructs a normal rectangular symbol.
     * @param color CSS name or hex code of the symbol.
     */
    public Symbol(String color) {
        this(color,new Rectangle(),INITIAL_HEIGHT,INITIAL_WIDTH);
    }

    /**
     * Constructs a symbol using a specific figure.
     * @param color CSS name or hex code of the symbol.
     * @param shape Figure used to display the symbol.
     * @param height Initial height.
     * @param width Initial width.
     */
    protected Symbol(String color,Figure shape,int height,int width) {
        this.color = color;
        this.shape = shape;
        this.height = height;
        this.width = width;
        centerX = 0;
        centerY = 0;
        isVisible = false;
        displayEnabled = true;
        this.shape.changeColor(color);
        this.shape.resize(height, width);
    }

    /**
     * Returns the symbol color.
     * @return Symbol color.
     */
    public String getColor() {
        return color;
    }

    /**
     * Changes the symbol color.
     * @param newColor New color.
     */
    public void changeColor(String newColor) {
        color = newColor;
        shape.changeColor(newColor);
    }

    /**
     * Moves the symbol using its center point.
     * @param x Center horizontal position.
     * @param y Center vertical position.
     */
    public void moveTo(int x, int y) {
        centerX = x;
        centerY = y;
        moveShapeToCenter();
    }

    /**
     * Moves the graphical figure while preserving the current center.
     */
    private void moveShapeToCenter() {
        int left = centerX - width / 2;
        int top = centerY - height / 2;
        shape.moveTo(left, top);
    }

    /**
     * Makes the symbol visible.
     */
    public void makeVisible() {
        isVisible = true;
        shape.makeVisible();
    }

    /**
     * Hides the symbol.
     */
    public void makeInvisible() {
        isVisible = false;
        shape.makeInvisible();
    }

    /**
     * Indicates whether the symbol is visible.
     * @return True when visible.
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Handles symbol selection.
     * Subclasses may override this method.
     */
    public void onSelected() {
    }

    /**
     * Handles one complete wheel spin.
     * Subclasses may override this method.
     */
    public void onSpin() {
    }

    /**
     * Updates symbol visibility.
     * @param selected True when the symbol is selected.
     * @param wheelVisible True when the wheel is visible.
     */
    protected void updateVisibility(boolean selected, boolean wheelVisible) {
        if (selected && wheelVisible && displayEnabled) {
            makeVisible();
        } else {
            makeInvisible();
        }
    }

    /**
     * Changes whether the symbol can be displayed.
     * @param enabled True to allow display.
     */
    protected void setDisplayEnabled(boolean enabled) {
        displayEnabled = enabled;
    }

    /**
     * Indicates whether the symbol may be displayed.
     * @return True when display is enabled.
     */
    protected boolean isDisplayEnabled() {
        return displayEnabled;
    }

    /**
     * Changes the graphical size while preserving the symbol center.
     * @param newHeight New height.
     * @param newWidth New width.
     */
    protected void changeShapeSize(int newHeight,int newWidth) {
        height = newHeight;
        width = newWidth;
        shape.resize(newHeight, newWidth);
        moveShapeToCenter();
    }

    /**
     * Returns the current symbol height.
     * @return Current height.
     */
    protected int getShapeHeight() {
        return height;
    }

    /**
     * Returns the current symbol width.
     * @return Current width.
     */
    protected int getShapeWidth() {
        return width;
    }

    /**
     * Checks if the symbol matches a color.
     * @param otherColor Color to compare.
     * @return True when the colors match ignoring case.
     */
    public boolean matches(String otherColor) {
        return otherColor != null && color.equalsIgnoreCase(otherColor);
    }
    
    /**
     * Indicates whether this symbol is a wildcard.
     * @return False for normal symbols.
     */
    public boolean isWildcard() {
        return false;
    }
}