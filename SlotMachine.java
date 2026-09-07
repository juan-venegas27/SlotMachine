import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;
import javax.swing.JOptionPane;

/**
 * Slot machine simulator.
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
     * Creates a new slot machine without wheels in an invisible state.
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
     * Adds a new wheel at the specified position.
     * @param pos Position where the wheel will be inserted (1 for the first wheel on the left).
     */
    public void addWheel(int pos) {
        Wheel newWheel = new Wheel();
        int idx = clamp(pos, wheels.size() + 1) - 1;
        wheels.add(idx, newWheel);
        positionWheels();
        isOk = true;
    }

    /**
     * Removes the wheel at the specified position.
     * @param pos Number of the wheel to remove (1 for the first wheel).
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
     * Adds a colored symbol to the specified wheel.
     * @param pos Target wheel number (1 for the first wheel).
     * @param color Color name in English (e.g., "red", "yellow") or hexadecimal code (e.g., "#FF0000").
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
     * Removes a symbol from all wheels in the machine.
     * @param symbol Color of the symbol to remove (e.g., "red", "#00FF00").
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
     * Sets a specific symbol to be shown in the view of a wheel.
     * @param wheel Target wheel number (1 for the first wheel).
     * @param symbol Color of the symbol to display on screen.
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
     * Spins a single wheel of the machine.
     * @param wheel Number of the wheel to spin (1 for the first wheel).
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
     * Spins all wheels of the machine at the same time.
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
     * Spins a specific wheel by an exact number of steps.
     * @param wheel Number of the wheel to spin (1 for the first wheel).
     * @param steps Number of positions the wheel will advance (integer greater than 0).
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
     * Sets all wheels to display a given combination of symbols.
     * @param setSymbols Array with colors to display on each wheel from left to right.
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
     * Swaps the positions of two wheels in the machine.
     * @param wheel1 Number of the first wheel to swap (1 for the first wheel).
     * @param wheel2 Number of the second wheel to swap (1 for the first wheel).
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
     * Locks a wheel to prevent it from spinning.
     * @param wheel Number of the wheel to lock (1 for the first wheel).
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
     * Unlocks a wheel to allow it to spin again.
     * @param wheel Number of the wheel to unlock (1 for the first wheel).
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
     * Returns the available symbols in the first wheel.
     * @return Array containing the colors of the symbols.
     */
    public String[] symbols() {
        return wheels.isEmpty() ? new String[0] : wheels.get(0).symbols();
    }

    /**
     * Counts how many different symbols are currently visible across the machine.
     * @return Total count of unique visible colors.
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
     * Returns the symbols currently visible on the machine from left to right.
     * @return Array with the visible colors of each wheel.
     */
    public String[] configuration() {
        String[] res = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) res[i] = wheels.get(i).getCurrentSymbol();
        return res;
    }

    /**
     * Checks if all wheels display the exact same winning symbol.
     * @return True if every wheel has the same visible color, false otherwise.
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
     * Makes the slot machine visible on screen.
     */
    public void makeVisible() {
        isVisible = true;
        positionWheels();
    }

    /**
     * Hides the slot machine from the screen.
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
     * Closes the simulator and exits the application.
     */
    public void exit() {
        makeInvisible();
        System.exit(0);
    }

    /**
     * Indicates whether the last operation was successful.
     * @return True if the operation was valid, false if it failed.
     */
    public boolean ok() {
        return isOk;
    }

    /**
     * Obtains the wheel located at a given position.
     * @param pos Number of the desired wheel (1 for the first wheel).
     * @return The requested wheel, or null if no wheels exist.
     */
    private Wheel getWheel(int pos) {
        if (wheels.isEmpty()) return null;
        return wheels.get(clamp(pos, wheels.size()) - 1);
    }

    /**
     * Keeps a number within the valid range of positions.
     * @param val Requested number.
     * @param max Maximum allowed value.
     * @return Number adjusted between 1 and the maximum allowed.
     */
    private int clamp(int val, int max) {
        return Math.max(1, Math.min(val, max));
    }

    /**
     * Updates the on-screen layout and spacing of all wheels and frames.
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
     * Performs an animated rotation for all wheels on screen.
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
     * Updates the jackpot indicator light based on the winning state.
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
     * Checks if a color name or hex code is valid.
     * @param colorString Text of the color to verify (e.g., "red", "#FF0000").
     * @return True if the color is valid, false otherwise.
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
     * Checks if a color name belongs to the list of allowed color names.
     * @param c Name of the color in lowercase (e.g., "blue").
     * @return True if the color name is supported, false otherwise.
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
     * Registers an operation error and notifies the user if visible.
     * @param message Description of the error.
     */
    private void fail(String message) {
        isOk = false;
        if (isVisible) {
            JOptionPane.showMessageDialog(null, message, "Slot Machine", JOptionPane.ERROR_MESSAGE);
        }
    }
}