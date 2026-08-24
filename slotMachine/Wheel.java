import java.util.ArrayList;

/**
 * Represents a wheel of the slot machine.
 */
public class Wheel {
    private ArrayList<String> symbols;
    private int currentIndex;
    private Rectangle rectangle;
    private boolean isVisible;

    /** Creates an empty wheel with its default position. */
    public Wheel() {
        symbols = new ArrayList<>();
        currentIndex = 0;
        rectangle = new Rectangle();
        rectangle.changeSize(80, 60);
        rectangle.changeColor("white");
        isVisible = false;
    }

    /**
     * Creates an empty wheel at the specified position.
     * @param posx the horizontal position.
     * @param posy the vertical position.
     */
    public Wheel(int posx, int posy) {
        symbols = new ArrayList<>();
        currentIndex = 0;
        rectangle = new Rectangle();
        rectangle.changeSize(80, 60);
        rectangle.changeColor("white");
        rectangle.moveTo(posx, posy);
        isVisible = false;
    }

    /**
     * Adds a symbol at the specified position.
     * @param pos the symbol position.
     * @param color the symbol color.
     */
    public void addSymbol(int pos, String color) {
        if (symbols.isEmpty()) {
            symbols.add(color);
        } else {
            if (pos < 1) {
                pos = 1;
            } else if (pos > symbols.size() + 1) {
                pos = symbols.size() + 1;
            }

            symbols.add(pos - 1, color);
        }

        updateVisual();
    }

    /**
     * Deletes the first occurrence of a symbol.
     * @param color the symbol color.
     * @return true if the symbol was deleted.
     */
    public boolean delSymbol(String color) {
        int index = symbols.indexOf(color);

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
     * Places the wheel on the specified symbol.
     * @param color the symbol color.
     * @return true if the symbol exists.
     */
    public boolean placeSymbol(String color) {
        int index = symbols.indexOf(color);

        if (index == -1) {
            return false;
        }

        currentIndex = index;
        updateVisual();
        return true;
    }

    /**
     * Returns the currently visible symbol.
     * @return the current symbol, or null if the wheel is empty.
     */
    public String getCurrentSymbol() {
        if (!symbols.isEmpty()) {
            return symbols.get(currentIndex);
        }

        return null;
    }

    /**
     * Returns all symbols in their current order.
     * @return an array containing the symbol colors.
     */
    public String[] symbols() {
        return symbols.toArray(new String[0]);
    }

    /**
     * Returns the number of symbols in the wheel.
     * @return the number of symbols.
     */
    public int countSymbols() {
        return symbols.size();
    }

    /** Spins the wheel to the next symbol. */
    public void spin() {
        if (!symbols.isEmpty()) {
            currentIndex = (currentIndex + 1) % symbols.size();
            updateVisual();
        }
    }

    /** Updates the rectangle color according to the current symbol. */
    private void updateVisual() {
        String current = getCurrentSymbol();

        if (current != null) {
            rectangle.changeColor(current);
        } else {
            rectangle.changeColor("white");
        }
    }

    /** Makes the wheel visible. */
    public void makeVisible() {
        isVisible = true;
        rectangle.makeVisible();
    }

    /** Makes the wheel invisible. */
    public void makeInvisible() {
        isVisible = false;
        rectangle.makeInvisible();
    }

    /**
     * Moves the wheel to the specified position.
     * @param x the horizontal position.
     * @param y the vertical position.
     */
    public void moveTo(int x, int y) {
        rectangle.moveTo(x, y);
    }

    /**
     * Moves the wheel horizontally.
     * @param distance the horizontal distance.
     */
    public void moveHorizontal(int distance) {
        rectangle.moveHorizontal(distance);
    }

    /**
     * Moves the wheel vertically.
     * @param distance the vertical distance.
     */
    public void moveVertical(int distance) {
        rectangle.moveVertical(distance);
    }
}