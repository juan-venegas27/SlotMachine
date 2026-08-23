import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JOptionPane;

/**
 * Main class for the Slot Machine Simulator (DOPO - POOB).
 * Manages wheels, symbols, spin operations, jackpot verification,
 * and visual representation on Canvas using shapes.
 *
 * @author Juan Venegas / POOB
 * @version 1.0
 */
public class SlotMachine {
    private ArrayList<Wheel> wheels;
    private ArrayList<String> symbolList;
    private boolean isVisible;
    private boolean isOk;

    // Visual components
    private Rectangle machineFrame;
    private Circle jackpotIndicator;
    private int xPosition;
    private int yPosition;

    private static final int DEFAULT_X = 50;
    private static final int DEFAULT_Y = 50;
    private static final int WHEEL_WIDTH = 60;
    private static final int WHEEL_HEIGHT = 80;
    private static final int WHEEL_SPACING = 20;
    private static final int FRAME_PADDING = 25;

    /**
     * Creates a new Slot Machine simulator in invisible state.
     */
    public SlotMachine() {
        wheels = new ArrayList<Wheel>();
        symbolList = new ArrayList<String>();
        isVisible = false;
        isOk = true;

        xPosition = DEFAULT_X;
        yPosition = DEFAULT_Y;

        // Visual frame initialization
        machineFrame = new Rectangle();
        machineFrame.changeColor("black");
        machineFrame.moveTo(xPosition, yPosition);

        jackpotIndicator = new Circle();
        jackpotIndicator.changeSize(20);
        jackpotIndicator.changeColor("red");
        jackpotIndicator.moveTo(xPosition + FRAME_PADDING, yPosition + 10);

        recalculateFrameSize();
    }

    /**
     * Adds a wheel at the specified position (1-indexed).
     *
     * @param pos position where the wheel will be inserted
     */
    public void addWheel(int pos) {
        if (pos < 1) {
            pos = 1;
        } else if (pos > wheels.size() + 1) {
            pos = wheels.size() + 1;
        }

        int index = pos - 1;
        Wheel newWheel = new Wheel();

        // Synchronize all existing symbols into the new wheel
        for (int i = 0; i < symbolList.size(); i++) {
            newWheel.addSymbol(i + 1, symbolList.get(i));
        }

        wheels.add(index, newWheel);
        repositionVisuals();

        if (isVisible) {
            newWheel.makeVisible();
        }

        isOk = true;
        updateJackpotVisuals();
    }

    /**
     * Deletes the wheel at the specified position (1-indexed).
     *
     * @param pos position of the wheel to delete
     */
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            notifyError("Cannot delete wheel: There are no wheels in the slot machine.");
            return;
        }

        if (pos < 1) {
            pos = 1;
        } else if (pos > wheels.size()) {
            pos = wheels.size();
        }

        int index = pos - 1;
        Wheel removed = wheels.remove(index);
        removed.makeInvisible();

        repositionVisuals();
        isOk = true;
        updateJackpotVisuals();
    }

    /**
     * Adds a symbol at the specified position (1-indexed) across all wheels.
     *
     * @param pos position where the symbol will be added
     * @param color CSS color of the symbol
     */
    public void addSymbol(int pos, String color) {
        if (color == null || color.trim().isEmpty()) {
            notifyError("Cannot add symbol: Color is invalid or empty.");
            return;
        }

        if (pos < 1) {
            pos = 1;
        } else if (pos > symbolList.size() + 1) {
            pos = symbolList.size() + 1;
        }

        int index = pos - 1;
        symbolList.add(index, color);

        for (Wheel w : wheels) {
            w.addSymbol(pos, color);
        }

        isOk = true;
        updateJackpotVisuals();
    }

    /**
     * Deletes the first occurrence of a symbol across all wheels.
     *
     * @param symbol CSS color of the symbol to delete
     */
    public void delSymbol(String symbol) {
        if (symbol == null || !symbolList.contains(symbol)) {
            notifyError("Cannot delete symbol: Symbol '" + symbol + "' does not exist.");
            return;
        }

        symbolList.remove(symbol);

        for (Wheel w : wheels) {
            w.delSymbol(symbol);
        }

        isOk = true;
        updateJackpotVisuals();
    }

    /**
     * Places the specified wheel on the first occurrence of a symbol.
     *
     * @param wheel position of the wheel (1-indexed)
     * @param symbol CSS color of the symbol to place
     */
    public void placeSymbol(int wheel, String symbol) {
        if (wheel < 1 || wheel > wheels.size()) {
            notifyError("Cannot place symbol: Wheel position " + wheel + " is out of range.");
            return;
        }

        boolean success = wheels.get(wheel - 1).placeSymbol(symbol);
        if (!success) {
            notifyError("Cannot place symbol: Symbol '" + symbol + "' was not found in wheel " + wheel + ".");
            return;
        }

        isOk = true;
        updateJackpotVisuals();
    }

    /**
     * Spins a specific wheel to its next symbol.
     *
     * @param wheel position of the wheel (1-indexed)
     */
    public void spin(int wheel) {
        if (wheel < 1 || wheel > wheels.size()) {
            notifyError("Cannot spin wheel: Wheel position " + wheel + " is out of range.");
            return;
        }

        wheels.get(wheel - 1).spin();
        isOk = true;
        updateJackpotVisuals();
    }

    /**
     * Spins all wheels of the slot machine.
     */
    public void spin() {
        if (wheels.isEmpty()) {
            notifyError("Cannot spin: There are no wheels in the slot machine.");
            return;
        }

        for (Wheel w : wheels) {
            w.spin();
        }

        isOk = true;
        updateJackpotVisuals();
    }

    /**
     * Returns the ordered sequence of symbols starting from position 1.
     *
     * @return array containing all symbols
     */
    public String[] symbols() {
        return symbolList.toArray(new String[0]);
    }

    /**
     * Returns the total count of distinct symbols in the machine.
     *
     * @return number of distinct symbols
     */
    public int distinctSymbols() {
        HashSet<String> distinct = new HashSet<String>();
        for (String s : symbolList) {
            if (s != null) {
                distinct.add(s.toLowerCase());
            }
        }
        return distinct.size();
    }

    /**
     * Returns the currently visible symbols on all wheels from left to right.
     *
     * @return array containing visible symbols
     */
    public String[] configuration() {
        String[] config = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            config[i] = wheels.get(i).getCurrentSymbol();
        }
        return config;
    }

    /**
     * Checks whether the current configuration is a jackpot winner
     * (all visible symbols on all wheels are identical).
     *
     * @return true if winning, false otherwise
     */
    public boolean isJackpot() {
        if (wheels.isEmpty() || symbolList.isEmpty()) {
            return false;
        }

        String[] config = configuration();
        String first = config[0];
        if (first == null) {
            return false;
        }

        for (String symbol : config) {
            if (symbol == null || !symbol.equalsIgnoreCase(first)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Makes the slot machine and all its components visible on the Canvas.
     */
    public void makeVisible() {
        isVisible = true;
        machineFrame.makeVisible();
        jackpotIndicator.makeVisible();
        for (Wheel w : wheels) {
            w.makeVisible();
        }
        updateJackpotVisuals();
    }

    /**
     * Hides the slot machine and all its components from the Canvas.
     */
    public void makeInvisible() {
        isVisible = false;
        machineFrame.makeInvisible();
        jackpotIndicator.makeInvisible();
        for (Wheel w : wheels) {
            w.makeInvisible();
        }
    }

    /**
     * Exits and closes the simulator.
     */
    public void exit() {
        makeInvisible();
        System.exit(0);
    }

    /**
     * Returns whether the last executed operation succeeded.
     *
     * @return true if the last action succeeded, false otherwise
     */
    public boolean ok() {
        return isOk;
    }

    /**
     * Handles failed operations and presents a JOptionPane error dialog
     * only if the simulator is currently visible.
     *
     * @param message descriptive error message
     */
    private void notifyError(String message) {
        isOk = false;
        if (isVisible) {
            JOptionPane.showMessageDialog(null, message, "Slot Machine - Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates graphical appearance based on the jackpot state.
     */
    private void updateJackpotVisuals() {
        if (isJackpot()) {
            machineFrame.changeColor("yellow");
            jackpotIndicator.changeColor("gold");
        } else {
            machineFrame.changeColor("black");
            jackpotIndicator.changeColor("red");
        }

        if (isVisible) {
            machineFrame.makeVisible();
            jackpotIndicator.makeVisible();
        }
    }

    /**
     * Recalculates the position and frame dimensions according to the wheel count.
     */
    private void repositionVisuals() {
        recalculateFrameSize();

        int wheelY = yPosition + 40;
        for (int i = 0; i < wheels.size(); i++) {
            int wheelX = xPosition + FRAME_PADDING + i * (WHEEL_WIDTH + WHEEL_SPACING);
            wheels.get(i).moveTo(wheelX, wheelY);
            if (isVisible) {
                wheels.get(i).makeVisible();
            }
        }
    }

    /**
     * Dynamically adjusts the outer frame size based on the number of wheels.
     */
    private void recalculateFrameSize() {
        int totalWheels = Math.max(wheels.size(), 1);
        int frameWidth = (FRAME_PADDING * 2) + (totalWheels * WHEEL_WIDTH) + ((totalWheels - 1) * WHEEL_SPACING);
        int frameHeight = WHEEL_HEIGHT + 65;

        machineFrame.changeSize(frameHeight, frameWidth);
        jackpotIndicator.moveTo(xPosition + (frameWidth / 2) - 10, yPosition + 10);
    }
}
