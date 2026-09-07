import java.util.ArrayList;
import java.util.Random;

/**
 * Individual wheel for the slot machine.
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
     * Creates a wheel at its default visual position.
     */
    public Wheel() {
        this(40, 55);
    }

    /**
     * Creates a wheel at specific coordinates.
     * @param posx Initial horizontal pixel position.
     * @param posy Initial vertical pixel position.
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
     * Adds a new colored symbol to the wheel at the specified position.
     * @param pos Insertion position for the symbol (1 for the first position).
     * @param color Color name in English (e.g., "red") or hex code (e.g., "#FF0000").
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
     * Removes the first symbol matching the specified color.
     * @param color Color of the symbol to remove (e.g., "blue" or "#0000FF").
     * @return True if the symbol was found and removed, false otherwise.
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
     * Sets the wheel to display a specific symbol.
     * @param color Color of the symbol to display.
     * @return True if the symbol exists in the wheel, false otherwise.
     */
    public boolean placeSymbol(String color) {
        int index = indexOfColor(color);
        if (index == -1) return false;
        currentIndex = index;
        updatePositions();
        return true;
    }

    /**
     * Checks if the wheel contains a symbol with the specified color.
     * @param color Color to look for in the wheel.
     * @return True if the color exists in the wheel, false otherwise.
     */
    public boolean containsSymbol(String color) {
        return indexOfColor(color) != -1;
    }

    /**
     * Returns the color of the symbol currently visible on the wheel.
     * @return Currently visible color, or null if the wheel has no symbols.
     */
    public String getCurrentSymbol() {
        return symbols.isEmpty() ? null : symbols.get(currentIndex).getColor();
    }

    /**
     * Returns the list of all symbol colors contained in the wheel.
     * @return Array with the symbol colors in their current order.
     */
    public String[] symbols() {
        String[] colors = new String[symbols.size()];
        for (int i = 0; i < symbols.size(); i++) colors[i] = symbols.get(i).getColor();
        return colors;
    }

    /**
     * Returns the total number of symbols in the wheel.
     * @return Total count of symbols.
     */
    public int countSymbols() {
        return symbols.size();
    }

    /**
     * Advances the wheel forward by one position.
     */
    public void spinStep() {
        if (!isLocked && !symbols.isEmpty()) {
            currentIndex = (currentIndex + 1) % symbols.size();
            updatePositions();
        }
    }

    /**
     * Spins the wheel randomly.
     */
    public void spin() {
        if (isLocked || symbols.isEmpty()) return;
        spinSteps(2 + random.nextInt(6));
    }

    /**
     * Advances the wheel forward by a specific number of steps.
     * @param steps Number of positions to advance (integer greater than 0).
     */
    public void spinSteps(int steps) {
        if (isLocked || symbols.isEmpty()) return;
        for (int s = 0; s < steps; s++) {
            spinStep();
            if (isVisible) Canvas.getCanvas().wait(70);
        }
    }

    /**
     * Moves the wheel to a new position on the screen.
     * @param x Horizontal pixel position.
     * @param y Vertical pixel position.
     */
    public void moveTo(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
        updatePositions();
    }

    /**
     * Shows the wheel and its active symbol on the screen.
     */
    public void makeVisible() {
        isVisible = true;
        updatePositions();
    }

    /**
     * Hides the wheel and its symbols from the screen.
     */
    public void makeInvisible() {
        isVisible = false;
        wheelFrame.makeInvisible();
        wheelViewport.makeInvisible();
        for (Symbol sym : symbols) sym.makeInvisible();
    }

    /**
     * Locks the wheel to prevent it from spinning.
     */
    public void lock() {
        isLocked = true;
        wheelFrame.changeColor("#8B0000");
        if (isVisible) wheelFrame.makeVisible();
    }

    /**
     * Unlocks the wheel to allow it to spin again.
     */
    public void unlock() {
        isLocked = false;
        wheelFrame.changeColor("#A8A9AD");
        if (isVisible) wheelFrame.makeVisible();
    }

    /**
     * Indicates whether the wheel is locked.
     * @return True if the wheel is locked, false if it can spin.
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Updates the positions and visibility of the wheel borders and its current symbol.
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
     * Finds the index of a symbol by its color.
     * @param color Color to search for in the wheel.
     * @return The index of the symbol, or -1 if it is not found.
     */
    private int indexOfColor(String color) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).matches(color)) return i;
        }
        return -1;
    }
}