import java.util.ArrayList;
import java.util.Random;

/**
 * Represents a slot machine wheel holding a sequence of symbols.
 */
public class Wheel {
    private ArrayList<Symbol> symbols;
    private int currentIndex;
    private int xPosition;
    private int yPosition;
    private boolean isVisible;
    private boolean isLocked;
    private Rectangle wheelFrame;
    private Rectangle wheelViewport;
    private static final Random random = new Random();

    /**
     * Default constructor placing the wheel at base coordinates.
     */
    public Wheel() {
        this(40, 55);
    }

    /**
     * Constructs a wheel at given coordinates.
     * @param posx Initial horizontal coordinate.
     * @param posy Initial vertical coordinate.
     */
    public Wheel(int posx, int posy) {
        symbols = new ArrayList<>();
        currentIndex = 0;
        xPosition = posx;
        yPosition = posy;
        isVisible = false;
        isLocked = false;
        wheelFrame = new Rectangle();
        wheelFrame.changeSize(100, 70);
        wheelFrame.changeColor("#A8A9AD");
        wheelViewport = new Rectangle();
        wheelViewport.changeSize(84, 54);
        wheelViewport.changeColor("#FDFDFD");
        updatePositions();
    }

    /**
     * Adds a symbol at the specified 1-based position.
     * @param pos 1-based index position.
     * @param color CSS or hex color of the symbol.
     */
    public void addSymbol(int pos, String color) {
        Symbol newSymbol = new Symbol(color);
        if (symbols.isEmpty()) {
            symbols.add(newSymbol);
        } else {
            int targetPos = Math.max(1, Math.min(pos, symbols.size() + 1));
            symbols.add(targetPos - 1, newSymbol);
        }
        updatePositions();
    }

    /**
     * Removes the first occurrence of a symbol.
     * @param color Color name to delete.
     * @return True if deleted, false otherwise.
     */
    public boolean delSymbol(String color) {
        int index = indexOfColor(color);
        if (index == -1) return false;
        Symbol removed = symbols.remove(index);
        removed.makeInvisible();
        if (symbols.isEmpty()) currentIndex = 0;
        else if (currentIndex >= symbols.size()) currentIndex = symbols.size() - 1;
        updatePositions();
        return true;
    }

    /**
     * Sets the visible symbol to match the target color.
     * @param color Target symbol color.
     * @return True if symbol exists in this wheel.
     */
    public boolean placeSymbol(String color) {
        int index = indexOfColor(color);
        if (index == -1) return false;
        currentIndex = index;
        updatePositions();
        return true;
    }

    /**
     * Checks if this wheel contains a specific symbol color.
     * @param color Target color.
     * @return True if present.
     */
    public boolean containsSymbol(String color) {
        return indexOfColor(color) != -1;
    }

    /**
     * Returns the color of the current visible symbol.
     * @return Color name or null if empty.
     */
    public String getCurrentSymbol() {
        return symbols.isEmpty() ? null : symbols.get(currentIndex).getColor();
    }

    /**
     * Returns all symbol colors in order.
     * @return Array of color strings.
     */
    public String[] symbols() {
        String[] colors = new String[symbols.size()];
        for (int i = 0; i < symbols.size(); i++) colors[i] = symbols.get(i).getColor();
        return colors;
    }

    /**
     * Returns the total number of symbols in this wheel.
     * @return Symbol count.
     */
    public int countSymbols() {
        return symbols.size();
    }

    /**
     * Advances the wheel by a single step.
     */
    public void spinStep() {
        if (!isLocked && !symbols.isEmpty()) {
            currentIndex = (currentIndex + 1) % symbols.size();
            updatePositions();
        }
    }

    /**
     * Spins the wheel a randomized number of steps with animation.
     */
    public void spin() {
        if (isLocked || symbols.isEmpty()) return;
        spinSteps(2 + random.nextInt(6));
    }

    /**
     * Rotates the wheel by a specified number of steps.
     * @param steps Total steps to advance.
     */
    public void spinSteps(int steps) {
        if (isLocked || symbols.isEmpty()) return;
        for (int s = 0; s < steps; s++) {
            spinStep();
            if (isVisible) Canvas.getCanvas().wait(70);
        }
    }

    /**
     * Repositions the wheel container and active symbol.
     * @param x New horizontal position.
     * @param y New vertical position.
     */
    public void moveTo(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
        updatePositions();
    }

    /**
     * Makes the wheel and its active symbol visible.
     */
    public void makeVisible() {
        isVisible = true;
        updatePositions();
    }

    /**
     * Hides the wheel and all its contained symbols.
     */
    public void makeInvisible() {
        isVisible = false;
        wheelFrame.makeInvisible();
        wheelViewport.makeInvisible();
        for (Symbol sym : symbols) sym.makeInvisible();
    }

    /**
     * Locks this wheel, preventing rotation and updating its border color.
     */
    public void lock() {
        isLocked = true;
        wheelFrame.changeColor("#8B0000");
        if (isVisible) wheelFrame.makeVisible();
    }

    /**
     * Unlocks this wheel, restoring its default metallic border.
     */
    public void unlock() {
        isLocked = false;
        wheelFrame.changeColor("#A8A9AD");
        if (isVisible) wheelFrame.makeVisible();
    }

    /**
     * Returns the locked state of this wheel.
     * @return True if locked.
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Repositions the container frames and centers the active symbol.
     */
    private void updatePositions() {
        wheelFrame.moveTo(xPosition, yPosition);
        wheelViewport.moveTo(xPosition + 8, yPosition + 8);
        if (isVisible) {
            wheelFrame.makeVisible();
            wheelViewport.makeVisible();
        }
        for (int i = 0; i < symbols.size(); i++) {
            Symbol sym = symbols.get(i);
            sym.moveTo(xPosition + 12, yPosition + 13);
            if (i == currentIndex && isVisible) sym.makeVisible();
            else sym.makeInvisible();
        }
    }

    /**
     * Searches for a symbol by its color string.
     * @param color The color to look for.
     * @return The 0-based index if found, or -1 otherwise.
     */
    private int indexOfColor(String color) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).matches(color)) return i;
        }
        return -1;
    }
}