import java.util.ArrayList;

/**
 * Represents an individual wheel of the slot machine.
 * Each wheel contains an ordered sequence of colored symbols,
 * keeps track of the currently visible symbol, and has a
 * graphical representation using shapes.
 *
 * @author Tu Nombre
 * @version 1.0
 */
public class Wheel {
    private ArrayList<String> symbols;
    private int currentIndex;
    private Rectangle shape;
    private boolean isVisible;
    private int xPosition;
    private int yPosition;

    private static final int DEFAULT_WIDTH = 60;
    private static final int DEFAULT_HEIGHT = 80;

    /**
     * Creates a wheel at a specific graphical position.
     *
     * @param x the horizontal position of the wheel
     * @param y the vertical position of the wheel
     */
    public Wheel(int x, int y) {
        symbols = new ArrayList<String>();
        currentIndex = 0;
        isVisible = false;

        xPosition = x;
        yPosition = y;

        shape = new Rectangle();
        shape.changeSize(DEFAULT_HEIGHT, DEFAULT_WIDTH);
        shape.changeColor("white");
        shape.moveTo(x, y);
    }

    /**
     * Creates a wheel at the default position.
     */
    public Wheel() {
        this(50, 50);
    }

    /**
     * Adds a symbol at the specified position.
     * Positions start at 1.
     *
     * @param pos the position where the symbol is added
     * @param color the CSS color of the symbol
     */
    public void addSymbol(int pos, String color) {
        if (color == null || color.trim().isEmpty()) {
            return;
        }

        if (pos < 1) {
            pos = 1;
        } else if (pos > symbols.size() + 1) {
            pos = symbols.size() + 1;
        }

        int index = pos - 1;
        symbols.add(index, color);

        updateVisual();
    }

    /**
     * Deletes the first occurrence of a symbol.
     *
     * @param symbol the color of the symbol to delete
     * @return true if the symbol was found and deleted
     */
    public boolean delSymbol(String symbol) {
        if (symbol == null) {
            return false;
        }

        int index = symbols.indexOf(symbol);

        if (index == -1) {
            return false;
        }

        symbols.remove(index);

        if (symbols.isEmpty()) {
            currentIndex = 0;
        } else if (currentIndex >= symbols.size()) {
            currentIndex = symbols.size() - 1;
        }

        updateVisual();
        return true;
    }

    /**
     * Moves the wheel to the next symbol.
     */
    public void spin() {
        if (!symbols.isEmpty()) {
            currentIndex = (currentIndex + 1) % symbols.size();
            updateVisual();
        }
    }

    /**
     * Places the wheel on the first occurrence of a symbol.
     *
     * @param symbol the color of the symbol
     * @return true if the symbol was found and selected
     */
    public boolean placeSymbol(String symbol) {
        if (symbol == null) {
            return false;
        }

        int index = symbols.indexOf(symbol);

        if (index == -1) {
            return false;
        }

        currentIndex = index;
        updateVisual();
        return true;
    }

    /**
     * Returns the currently visible symbol.
     *
     * @return the current symbol, or null if the wheel has no symbols
     */
    public String getCurrentSymbol() {
        if (symbols.isEmpty()) {
            return null;
        }

        return symbols.get(currentIndex);
    }

    /**
     * Returns all symbols in their current order.
     *
     * @return an array containing the symbols
     */
    public String[] getSymbols() {
        return symbols.toArray(new String[0]);
    }

    /**
     * Returns the number of symbols in the wheel.
     *
     * @return the number of symbols
     */
    public int countSymbols() {
        return symbols.size();
    }

    /**
     * Makes the wheel visible.
     */
    public void makeVisible() {
        isVisible = true;
        updateVisual();
    }

    /**
     * Makes the wheel invisible.
     */
    public void makeInvisible() {
        isVisible = false;
        shape.makeInvisible();
    }

    /**
     * Moves the wheel horizontally.
     *
     * @param distance the distance to move in pixels
     */
    public void moveHorizontal(int distance) {
        xPosition += distance;
        shape.moveHorizontal(distance);
    }

    /**
     * Moves the wheel vertically.
     *
     * @param distance the distance to move in pixels
     */
    public void moveVertical(int distance) {
        yPosition += distance;
        shape.moveVertical(distance);
    }

    /**
     * Moves the wheel to a specific coordinate.
     *
     * @param x the new horizontal position
     * @param y the new vertical position
     */
    public void moveTo(int x, int y) {
        xPosition = x;
        yPosition = y;
        shape.moveTo(x, y);
    }

    /**
     * Updates the graphical representation according to
     * the currently selected symbol.
     */
    private void updateVisual() {
        String current = getCurrentSymbol();

        if (current != null) {
            shape.changeColor(current);
        } else {
            shape.changeColor("white");
        }

        if (isVisible) {
            shape.makeVisible();
        }
    }
}
