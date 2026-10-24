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
    private Wheel leftWheel;
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
     * @param posx Initial horizontal position.
     * @param posy Initial vertical position.
     */
    public Wheel(int posx, int posy) {
        symbols = new ArrayList<Symbol>();
        currentIndex = 0;
        xPosition = posx;
        yPosition = posy;
        isVisible = false;
        isLocked = false;
        leftWheel = null;
        wheelFrame = new Rectangle();
        wheelFrame.changeSize(100, 70);
        wheelFrame.changeColor("#A8A9AD");
        wheelViewport = new Rectangle();
        wheelViewport.changeSize(84, 54);
        wheelViewport.changeColor("#FDFDFD");
        updatePositions();
    }

    /**
     * Adds a normal symbol at the specified position.
     * @param pos Insertion position.
     * @param color Symbol color.
     */
    public void addSymbol(int pos, String color) {
        addSymbol(pos, color, "normal");
    }

    /**
     * Adds a symbol of the requested type.
     * @param pos Insertion position.
     * @param color Symbol color.
     * @param type Symbol type.
     */
    public void addSymbol(int pos,String color,String type) {
        Symbol newSymbol = SymbolCreator.create(type, color);
        if (symbols.isEmpty()) {
            symbols.add(newSymbol);
        } else {
            int targetPos = Math.max(1,Math.min(pos, symbols.size() + 1));
            symbols.add(targetPos - 1, newSymbol);
        }
        updatePositions();
    }

    /**
     * Removes the first symbol matching the color.
     * @param color Symbol color.
     * @return True when a symbol was removed.
     */
    public boolean delSymbol(String color) {
        int index = indexOfColor(color);
        if (index == -1) {
            return false;
        }
        Symbol removed = symbols.remove(index);
        removed.makeInvisible();
        if (symbols.isEmpty()) {
            currentIndex = 0;
        } else if (currentIndex >= symbols.size()) {
            currentIndex = symbols.size() - 1;
        }
        updatePositions();
        return true;
    }

    /**
     * Sets the wheel to display a specific symbol.
     * @param color Symbol color.
     * @return True when the symbol exists.
     */
    public boolean placeSymbol(String color) {
        int index = indexOfColor(color);
        if (index == -1) {
            return false;
        }
        currentIndex = index;
        symbols.get(currentIndex).onSelected();
        updatePositions();
        return true;
    }

    /**
     * Checks if the wheel contains the given symbol.
     * @param color Symbol color.
     * @return True when the symbol exists.
     */
    public boolean containsSymbol(String color) {
        return indexOfColor(color) != -1;
    }
    
    /**
     * Returns the current symbol color.
     * @return Current color or null if the wheel has no symbols.
     */
    public String getCurrentSymbol() {
        if (symbols.isEmpty()) {
            return null;
        }
        return symbols.get(currentIndex).getColor();
    }
    
/**
     * Returns the current symbol color when the symbol is enabled for display.
     * @return Current symbol color or an empty string when there is no symbol
     *         or when the current symbol is hidden.
     */
    public String getCurrentVisibleSymbol() {
        if (symbols.isEmpty()) {
            return "";
        }

        if (!symbols.get(currentIndex).isDisplayEnabled()) {
            return "";
        }

        return symbols.get(currentIndex).getColor();
    }
    
    /**
     * Returns all symbol colors.
     * @return Array with symbol colors.
     */
    public String[] symbols() {
        String[] colors = new String[symbols.size()];
        for (int i = 0; i < symbols.size(); i++) {
            colors[i] = symbols.get(i).getColor();
        }
        return colors;
    }

    /**
     * Returns the number of symbols.
     * @return Symbol count.
     */
    public int countSymbols() {
        return symbols.size();
    }

    /**
     * Spins the wheel randomly.
     */
    public void spin() {
        if (isLocked || symbols.isEmpty()) {
            return;
        }
        spinSteps(2 + random.nextInt(6));
    }

    /**
     * Spins the wheel by the requested number of steps.
     * @param steps Number of positions.
     */
    public void spinSteps(int steps) {
        if (!canSpin(steps)) {
            return;
        }
        applySpinEffect();
        rotateSteps(steps);
        afterSpinStep();
    }
    
    /**
     * Performs one animation step without applying final spin effects.
     */
    void spinAnimationStep() {
        if (isLocked || symbols.isEmpty()) {
            return;
        }
        currentIndex++;
        normalizeCurrentIndex();
        symbols.get(currentIndex).onSelected();
        updatePositions();
        if (isVisible) {
            Canvas.getCanvas().wait(500);
        }
    }
    
    /**
     * Applies the effects that happen once when a complete spin finishes.
     */
    void finishSpin() {
        if (isLocked || symbols.isEmpty()) {
            return;
        }
        applySpinEffect();
        afterSpinStep();
    }
    
    /**
     * Applies one complete spin effect to every symbol.
     */
    protected void applySpinEffect() {
        for (Symbol symbol : symbols) {
            symbol.onSpin();
        }
    }

    /**
     * Checks whether the wheel can spin.
     * @param steps Number of requested steps.
     * @return True when the spin is valid.
     */
    private boolean canSpin(int steps) {
        return !isLocked && !symbols.isEmpty() && steps != 0;
    }

    /**
     * Rotates the wheel by the requested amount.
     * @param steps Number of positions.
     */
    private void rotateSteps(int steps) {
        int direction = steps > 0 ? 1 : -1;
        int amount = Math.abs(steps % symbols.size());
        if (symbols.size() == 1) {
            rotateOneStep(direction);
            return;
        }
        for (int i = 0; i < amount; i++) {
            rotateOneStep(direction);
        }
    }

    /**
     * Performs one wheel rotation.
     * @param direction Rotation direction.
     */
    private void rotateOneStep(int direction) {
        currentIndex += direction;
        normalizeCurrentIndex();
        symbols.get(currentIndex).onSelected();
        updatePositions();
        afterSpinStep();
        if (isVisible) {
            Canvas.getCanvas().wait(1000);
        }
    }

    /**
     * Keeps the current symbol index valid.
     */
    private void normalizeCurrentIndex() {
        if (currentIndex >= symbols.size()) {
            currentIndex = 0;
        }
        if (currentIndex < 0) {
            currentIndex = symbols.size() - 1;
        }
    }

    /**
     * Performs special behavior after one rotation step.
     * Subclasses may override this method.
     */
    protected void afterSpinStep() {
    }

    /**
     * Copies the current symbol from another wheel.
     * @param source Source wheel.
     */
    protected void copyCurrentSymbol(Wheel source) {
        if (source == null) {
            return;
        }
        String color = source.getCurrentSymbol();
        int index = indexOfColor(color);
        if (index != -1) {
            currentIndex = index;
            updatePositions();
        }
    }


    /**
     * Indicates whether the current symbol can be displayed.
     * @return True when the current symbol display is enabled.
     */
    public boolean currentSymbolIsDisplayEnabled() {
        return symbols.get(currentIndex).isDisplayEnabled();
    }

    /**
     * Moves the wheel to a screen position.
     * @param x Horizontal position.
     * @param y Vertical position.
     */
    public void moveTo(int x, int y) {
        xPosition = x;
        yPosition = y;
        updatePositions();
    }


    /**

    

    /**
     * Makes the wheel visible.
     */
    public void makeVisible() {
        isVisible = true;
        updatePositions();
    }

    /**
     * Makes the wheel invisible.
     */
    public void makeInvisible() {
        isVisible = false;
        wheelFrame.makeInvisible();
        wheelViewport.makeInvisible();
        for (Symbol symbol : symbols) {
            symbol.makeInvisible();
        }
    }

    /**
     * Locks the wheel.
     */
    public void lock() {
        if (!canLock()) {
            return;
        }
        isLocked = true;
        wheelFrame.changeColor("black");
        updatePositions();
    }

    /**
     * Unlocks the wheel.
     */
    public void unlock() {
        isLocked = false;
        wheelFrame.changeColor(unlockedFrameColor());
        updatePositions();
    }

    /**
     * Indicates whether the wheel is locked.
     * @return True when locked.
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Indicates whether the wheel can be locked.
     * @return True for a normal wheel.
     */
    public boolean canLock() {
        return true;
    }

    /**
     * Indicates whether the wheel can be exchanged.
     * @return True for a normal wheel.
     */
    public boolean canSwap() {
        return true;
    }

    /**
     * Indicates whether the wheel can be deleted.
     * @return True for a normal wheel.
     */
    public boolean canDelete() {
        return true;
    }

    /**
     * Returns the frame color for an unlocked wheel.
     * @return Unlocked frame color.
     */
    protected String unlockedFrameColor() {
        return "#A8A9AD";
    }

    /**
     * Changes the wheel frame color.
     * @param color New frame color.
     */
    protected void setFrameColor(String color) {
        wheelFrame.changeColor(color);
    }

    /**
     * Assigns the wheel immediately to the left.
     * @param wheel Left wheel.
     */
    void setLeftWheel(Wheel wheel) {
        leftWheel = wheel;
    }

    /**
     * Returns the wheel immediately to the left.
     * @return Left wheel or null.
     */
    protected Wheel getLeftWheel() {
        return leftWheel;
    }

    /**
     * Updates the wheel and symbol positions.
     */
    private void updatePositions() {
        wheelFrame.moveTo(xPosition, yPosition);
        wheelViewport.moveTo(xPosition + 8,yPosition + 8);
        if (isVisible) {
            wheelFrame.makeVisible();
            wheelViewport.makeVisible();
        }
        for (int i = 0; i < symbols.size(); i++) {
            Symbol symbol = symbols.get(i);
            symbol.moveTo(xPosition + 35,yPosition + 50);
            symbol.updateVisibility(i == currentIndex,isVisible);
        }
    }

    /**
     * Finds the index of a symbol by its color.
     * @param color Color to search.
     * @return Symbol index or -1.
     */
    private int indexOfColor(String color) {
        if (color == null) {
            return -1;
        }
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).matches(color)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Indicates whether the current symbol is a wildcard.
     * @return True when the selected symbol is a wildcard.
     */
    public boolean currentSymbolIsWildcard() {
        return !symbols.isEmpty() && symbols.get(currentIndex).isWildcard();
    }
}