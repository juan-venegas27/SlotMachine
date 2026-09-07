import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;
import javax.swing.JOptionPane;

/**
 * Slot Machine simulator supporting multi-wheel gaming, lock, swap, and jackpot.
 */
public class SlotMachine {
    private ArrayList<Wheel> wheels;
    private Rectangle machineOuterFrame;
    private Rectangle machineInnerFrame;
    private Circle jackpotOuter;
    private Circle jackpotBulb;
    private boolean isVisible;
    private boolean isOk;
    private static final Random random = new Random();

    /**
     * Constructs a slot machine with visual casino components.
     */
    public SlotMachine() {
        wheels = new ArrayList<>();
        isVisible = false;
        isOk = true;
        machineOuterFrame = new Rectangle();
        machineOuterFrame.changeColor("#1A1A1D");
        machineInnerFrame = new Rectangle();
        machineInnerFrame.changeColor("#D4AF37");
        jackpotOuter = new Circle();
        jackpotOuter.changeSize(42);
        jackpotOuter.changeColor("#FFD700");
        jackpotBulb = new Circle();
        jackpotBulb.changeSize(30);
        jackpotBulb.changeColor("#3B2200");
        positionWheels();
    }

    /**
     * Adds a new wheel at the specified 1-based index position.
     * @param pos 1-based target insertion index.
     */
    public void addWheel(int pos) {
        Wheel newWheel = new Wheel();
        int idx = clamp(pos, wheels.size() + 1) - 1;
        wheels.add(idx, newWheel);
        positionWheels();
        isOk = true;
    }

    /**
     * Removes the wheel at the specified 1-based position.
     * @param pos 1-based target index.
     */
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            fail("No wheels available to delete.");
            return;
        }
        int idx = clamp(pos, wheels.size()) - 1;
        Wheel wheel = wheels.remove(idx);
        wheel.makeInvisible();
        positionWheels();
        isOk = true;
        updateJackpotIndicator();
    }

    /**
     * Adds a symbol with CSS color or hex code to the specified wheel.
     * @param pos 1-based wheel position.
     * @param color CSS name or hex color string.
     */
    public void addSymbol(int pos, String color) {
        if (!isValidColor(color)) {
            fail("Invalid CSS or hex color: " + color);
            return;
        }
        Wheel wheel = getWheel(pos);
        if (wheel == null) {
            fail("No wheels available to add symbol.");
            return;
        }
        wheel.addSymbol(wheel.countSymbols() + 1, color);
        isOk = true;
        updateJackpotIndicator();
    }

    /**
     * Deletes the given symbol across all wheels.
     * @param symbol Color of the symbol to delete.
     */
    public void delSymbol(String symbol) {
        if (wheels.isEmpty()) {
            fail("No wheels available in machine.");
            return;
        }
        boolean found = false;
        for (Wheel wheel : wheels) {
            if (wheel.delSymbol(symbol)) found = true;
        }
        if (found) {
            isOk = true;
            updateJackpotIndicator();
        } else {
            fail("Symbol does not exist in any wheel.");
        }
    }

    /**
     * Places the specified wheel on the designated symbol.
     * @param wheel 1-based wheel index.
     * @param symbol Target symbol color.
     */
    public void placeSymbol(int wheel, String symbol) {
        Wheel target = getWheel(wheel);
        if (target == null) {
            fail("Invalid wheel index.");
            return;
        }
        if (target.placeSymbol(symbol)) {
            isOk = true;
            updateJackpotIndicator();
        } else {
            fail("Symbol does not exist in the selected wheel.");
        }
    }

    /**
     * Spins a single wheel once with animated motion.
     * @param wheel 1-based wheel index.
     */
    public void spin(int wheel) {
        Wheel target = getWheel(wheel);
        if (target == null) {
            fail("Invalid wheel index.");
            return;
        }
        if (target.isLocked()) {
            fail("Wheel is currently locked.");
            return;
        }
        target.spin();
        isOk = true;
        updateJackpotIndicator();
    }

    /**
     * Spins all wheels simultaneously with randomized steps.
     */
    public void spin() {
        if (wheels.isEmpty()) {
            fail("No wheels available to spin.");
            return;
        }
        executeAnimatedSpin();
        isOk = true;
        updateJackpotIndicator();
    }

    /**
     * Spins a specific wheel by a given step count.
     * @param wheel 1-based wheel index.
     * @param steps Number of rotation steps.
     */
    public void spin(int wheel, int steps) {
        if (steps < 1) {
            fail("Step count must be greater than 0.");
            return;
        }
        Wheel target = getWheel(wheel);
        if (target == null) {
            fail("Invalid wheel index.");
            return;
        }
        if (target.isLocked()) {
            fail("Wheel is currently locked.");
            return;
        }
        target.spinSteps(steps);
        isOk = true;
        updateJackpotIndicator();
    }

    /**
     * Sets all wheels directly to the given color sequence.
     * @param setSymbols Array of target symbol colors.
     */
    public void spin(String[] setSymbols) {
        if (setSymbols == null || setSymbols.length != wheels.size()) {
            fail("Configuration length must match wheel count.");
            return;
        }
        for (int i = 0; i < wheels.size(); i++) {
            if (!wheels.get(i).containsSymbol(setSymbols[i])) {
                fail("Symbol " + setSymbols[i] + " not found in wheel " + (i + 1));
                return;
            }
        }
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).placeSymbol(setSymbols[i]);
        }
        isOk = true;
        updateJackpotIndicator();
    }

    /**
     * Swaps the positions of two wheels.
     * @param wheel1 1-based index of the first wheel.
     * @param wheel2 1-based index of the second wheel.
     */
    public void swap(int wheel1, int wheel2) {
        if (wheels.size() < 2) {
            fail("At least 2 wheels required to swap.");
            return;
        }
        int idx1 = clamp(wheel1, wheels.size()) - 1;
        int idx2 = clamp(wheel2, wheels.size()) - 1;
        Wheel temp = wheels.get(idx1);
        wheels.set(idx1, wheels.get(idx2));
        wheels.set(idx2, temp);
        positionWheels();
        isOk = true;
        updateJackpotIndicator();
    }

    /**
     * Locks the specified wheel to prevent it from spinning.
     * @param wheel 1-based wheel index.
     */
    public void lock(int wheel) {
        Wheel target = getWheel(wheel);
        if (target != null) {
            target.lock();
            isOk = true;
        } else {
            fail("Invalid wheel index.");
        }
    }

    /**
     * Unlocks the specified wheel to permit rotation.
     * @param wheel 1-based wheel index.
     */
    public void unlock(int wheel) {
        Wheel target = getWheel(wheel);
        if (target != null) {
            target.unlock();
            isOk = true;
        } else {
            fail("Invalid wheel index.");
        }
    }

    /**
     * Returns the symbol colors of the first wheel.
     * @return Array of symbol colors.
     */
    public String[] symbols() {
        return wheels.isEmpty() ? new String[0] : wheels.get(0).symbols();
    }

    /**
     * Counts the distinct visible symbols currently on the machine.
     * @return Number of unique visible colors.
     */
    public int distinctSymbols() {
        if (wheels.isEmpty()) return 0;
        String[] config = configuration();
        ArrayList<String> unique = new ArrayList<>();
        for (String c : config) {
            if (c != null && !unique.contains(c)) unique.add(c);
        }
        return unique.size();
    }

    /**
     * Returns the visible symbols from left to right.
     * @return Current machine configuration array.
     */
    public String[] configuration() {
        String[] res = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) res[i] = wheels.get(i).getCurrentSymbol();
        return res;
    }

    /**
     * Checks if all wheels show the exact same symbol.
     * @return True if in jackpot state.
     */
    public boolean isjackpot() {
        if (wheels.isEmpty()) return false;
        String first = wheels.get(0).getCurrentSymbol();
        if (first == null) return false;
        for (int i = 1; i < wheels.size(); i++) {
            String curr = wheels.get(i).getCurrentSymbol();
            if (curr == null || !curr.equalsIgnoreCase(first)) return false;
        }
        return true;
    }

    /**
     * Makes the slot machine and its components visible.
     */
    public void makeVisible() {
        isVisible = true;
        positionWheels();
    }

    /**
     * Makes the slot machine completely invisible.
     */
    public void makeInvisible() {
        isVisible = false;
        jackpotBulb.makeInvisible();
        jackpotOuter.makeInvisible();
        for (Wheel w : wheels) w.makeInvisible();
        machineInnerFrame.makeInvisible();
        machineOuterFrame.makeInvisible();
    }

    /**
     * Closes the application safely.
     */
    public void exit() {
        makeInvisible();
        System.exit(0);
    }

    /**
     * Indicates whether the last attempted operation succeeded.
     * @return True if successful.
     */
    public boolean ok() {
        return isOk;
    }

    /**
     * Retrieves the wheel at the specified 1-based index applying limits.
     * @param pos 1-based wheel position.
     * @return The target wheel or null if no wheels exist.
     */
    private Wheel getWheel(int pos) {
        if (wheels.isEmpty()) return null;
        return wheels.get(clamp(pos, wheels.size()) - 1);
    }

    /**
     * Clamps an integer value between 1 and a given upper limit.
     * @param val The value to constrain.
     * @param max The maximum permissible value.
     * @return The clamped integer value.
     */
    private int clamp(int val, int max) {
        return Math.max(1, Math.min(val, max));
    }

    /**
     * Arranges the background chassis and positions all wheels inside it.
     */
    private void positionWheels() {
        int startX = 35;
        int y = 50;
        int spacing = 80;
        int totalWidth = Math.max(260, (wheels.size() * spacing)) + 50;
        machineOuterFrame.moveTo(10, 10);
        machineOuterFrame.changeSize(270, totalWidth);
        machineInnerFrame.moveTo(20, 20);
        machineInnerFrame.changeSize(250, totalWidth - 20);
        int centerX = totalWidth / 2;
        jackpotOuter.moveTo(centerX - 11, 185);
        jackpotBulb.moveTo(centerX - 5, 191);
        if (isVisible) {
            machineOuterFrame.makeVisible();
            machineInnerFrame.makeVisible();
            jackpotOuter.makeVisible();
            jackpotBulb.makeVisible();
        }
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).moveTo(startX + (i * spacing), y);
            if (isVisible) wheels.get(i).makeVisible();
        }
        updateJackpotIndicator();
    }

    /**
     * Coordinates the step-by-step animated spin across all active wheels.
     */
    private void executeAnimatedSpin() {
        int[] turns = new int[wheels.size()];
        int maxTurns = 0;
        for (int i = 0; i < wheels.size(); i++) {
            turns[i] = 2 + random.nextInt(6);
            if (turns[i] > maxTurns) maxTurns = turns[i];
        }
        for (int step = 0; step < maxTurns; step++) {
            for (int w = 0; w < wheels.size(); w++) {
                if (step < turns[w]) wheels.get(w).spinStep();
            }
            if (isVisible) Canvas.getCanvas().wait(70);
        }
    }

    /**
     * Updates the jackpot indicator lights according to the winning state.
     */
    private void updateJackpotIndicator() {
        if (!isVisible) return;
        if (isjackpot()) {
            jackpotOuter.changeColor("#FFD700");
            jackpotBulb.changeColor("#FF073A");
        } else {
            jackpotOuter.changeColor("#8A7322");
            jackpotBulb.changeColor("#3B2200");
        }
    }

    /**
     * Validates if a given string represents a valid CSS name or hex color code.
     * @param colorString The color string to validate.
     * @return True if valid, false otherwise.
     */
    private boolean isValidColor(String colorString) {
        if (colorString == null || colorString.trim().isEmpty()) return false;
        String c = colorString.trim();
        if (c.startsWith("#") && (c.length() == 7 || c.length() == 4)) {
            try {
                Color.decode(c);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return isCssName(c.toLowerCase());
    }


    /**
     * Checks if the color string matches standard CSS named colors.
     * @param c Lowercase color name.
     * @return True if supported in the CSS palette.
     */
    private boolean isCssName(String c) {
        String[] validColors = {
            "red", "black", "blue", "yellow", "green", "magenta", "white",
            "cyan", "gray", "orange", "pink", "gold", "purple", "silver",
            "brown", "coral", "crimson", "navy", "lime", "teal"
        };
        
        for (String validColor : validColors) {
            if (validColor.equals(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles operational failures, updates status, and notifies if visible.
     * @param message The descriptive error message to display.
     */
    private void fail(String message) {
        isOk = false;
        if (isVisible) {
            JOptionPane.showMessageDialog(null, message, "Slot Machine", JOptionPane.ERROR_MESSAGE);
        }
    }
}