import java.util.ArrayList;
import java.awt.Color;
import javax.swing.JOptionPane;
import java.util.Collections;
import java.util.Random;
/**
 * Slot machine simulator.
 */
public class SlotMachine {
    private Reel reel;
    private Rectangle machineOuterFrame;
    private Rectangle machineInnerFrame;
    private Circle jackpotOuter;
    private Circle jackpotBulb;
    private boolean isVisible;
    private boolean isOk;
    private Rectangle leverBar;
    private Rectangle leverBase;
    private Circle leverKnob;
    private static final Random random = new Random();
    /**
     * Creates a new slot machine without wheels in an invisible state.
     */
    public SlotMachine() {
        reel = new Reel();
        isVisible = true;
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
        leverBase = new Rectangle();
        leverBase.changeSize(30, 20);
        leverBase.changeColor("#444444");
        
        leverBar = new Rectangle();
        leverBar.changeSize(75, 10);
        leverBar.changeColor("#777777");
        
        leverKnob = new Circle();
        leverKnob.changeSize(28);
        leverKnob.changeColor("#C62828");
        positionWheels();
    }

    
    /**
     * Creates a slot machine with n wheels.
     * Each wheel contains the same n distinct colors in the same order.
     * The initial visible symbol is random for every wheel.
     * @param n Number of wheels and symbols.
     */
    public SlotMachine(int n) {
        this();
        if (n < 1) {
            return;
        }
        String[] colors = generateColors(n);
        for (int i = 0; i < n; i++) {
            reel.addWheel(i + 1);
            Wheel wheel = reel.getWheel(i + 1);
            for (int j = 0; j < n; j++) {
                wheel.addSymbol(j + 1, colors[j]);
            }
            String initialColor =
                colors[random.nextInt(colors.length)];
            wheel.placeSymbol(initialColor);
        }
        positionWheels();
        isOk = true;
    }
    
    /**
     * Generates n different random colors in hexadecimal format.
     * @param n Number of colors to generate.
     * @return Array containing n different hexadecimal colors.
     */
    private String[] generateColors(int n) {
        ArrayList<String> colors = new ArrayList<String>();
        while (colors.size() < n) {
            String color = generateRandomColor();
            if (!colors.contains(color)) {
                colors.add(color);
            }
        }
        return colors.toArray(new String[colors.size()]);
    }    
    
    /**
     * Generates a random color in hexadecimal format.
     * @return A hexadecimal color such as "#3FA7D6".
     */
    private String generateRandomColor() {
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return String.format("#%02X%02X%02X",red,green,blue);
    }
    
    
    /**
     * Adds a new wheel at the specified position.
     * @param pos Position where the wheel will be inserted (1 for the first wheel on the left).
     */
    public void addWheel(int pos) {
        reel.addWheel(pos);
        positionWheels();
        isOk = true;
    }

    /**
     * Removes the wheel at the specified position.
     * @param pos Number of the wheel to remove (1 for the first wheel).
     */
    public void delWheel(int pos) {
        if (reel.size() == 0) {
            fail("No wheels available to delete.");
            return;
        }

        Wheel wheel = reel.delWheel(pos);
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
        if (reel.size() == 0) {
            fail("No wheels available in machine.");
            return;
        }

        boolean found = reel.delSymbol(symbol);

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
        if (reel.size() == 0) {
            fail("No wheels available to spin.");
            return;
        }

        executeAnimatedSpin();

        isOk = true;
        updateJackpotIndicator();
    }

    /**
     * Spins a specific wheel by an exact number of steps.
     * Positive values rotate forward and negative values rotate backward.
     * @param wheel Number of the wheel to spin.
     * @param steps Number of positions to rotate.
     */
    public void spin(int wheel, int steps) {
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
        if (setSymbols == null || setSymbols.length != reel.size()) {
            fail("Configuration length must match wheel count.");
            return;
        }

        for (int i = 0; i < reel.size(); i++) {
            if (!reel.getWheel(i + 1).containsSymbol(setSymbols[i])) {
                fail("Symbol " + setSymbols[i] + " not found in wheel " + (i + 1));
                return;
            }
        }

        for (int i = 0; i < reel.size(); i++) {
            reel.getWheel(i + 1).placeSymbol(setSymbols[i]);
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
        if (reel.size() < 2) {
            fail("At least 2 wheels required to swap.");
            return;
        }

        reel.swap(wheel1, wheel2);
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
            updateJackpotIndicator();
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
            updateJackpotIndicator();
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
        return reel.symbols();
    }

    /**
     * Counts how many different symbols are currently visible across the machine.
     * @return Total count of unique visible colors.
     */
    public int distinctSymbols() {
        if (reel.size() == 0) return 0;

        String[] config = configuration();
        ArrayList<String> unique = new ArrayList<>();

        for (String c : config) {
            if (c != null && !unique.contains(c)) {
                unique.add(c);
            }
        }

        return unique.size();
    }

    /**
     * Returns the symbols currently visible on the machine from left to right.
     * @return Array with the visible colors of each wheel.
     */
    public String[] configuration() {
        return reel.configuration();
    }

    /**
     * Checks if all unlocked wheels display the same winning symbol.
     * Locked wheels are ignored.
     * @return True if all unlocked wheels have the same visible color.
     */
    public boolean isjackpot() {
        if (reel.size() == 0) return false;
        Wheel firstWheel = reel.firstUnlockedWheel();
        if (firstWheel == null) return false;
        String first = firstWheel.getCurrentSymbol();
        if (first == null) return false;
        for (int i = 1; i <= reel.size(); i++) {
            Wheel currentWheel = reel.getWheel(i);
            if (currentWheel.isLocked()) {
                continue;
            }
            String curr = currentWheel.getCurrentSymbol();
            if (curr == null || !curr.equalsIgnoreCase(first)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Makes the slot machine visible on screen.
     */
    public void makeVisible() {
        isVisible = true;
    
        reel.makeVisible();
    
        leverBase.makeVisible();
        leverBar.makeVisible();
        leverKnob.makeVisible();
    
        positionWheels();
    }

    /**
     * Hides the slot machine from the screen.
     */
    public void makeInvisible() {
        isVisible = false;
    
        jackpotBulb.makeInvisible();
        jackpotOuter.makeInvisible();
    
        leverKnob.makeInvisible();
        leverBar.makeInvisible();
        leverBase.makeInvisible();
    
        reel.makeInvisible();
    
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
        return reel.getWheel(pos);
    }

    /**
     * Updates the on-screen layout and spacing of all wheels and frames.
     */
    private void positionWheels() {
        int spacing = 80;
        int totalWidth = Math.max(260, (reel.size() * spacing)) + 50;
        int leverX = totalWidth + 18;
        leverBase.moveTo(leverX - 8, 145);
        leverBar.moveTo(leverX+12, 90);
        leverKnob.moveTo(leverX +3, 72);
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
        reel.positionWheels();
        updateJackpotIndicator();
    }

    /**
     * Performs an animated rotation for all wheels on screen.
     */
    private void executeAnimatedSpin() {
        reel.spin();
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
            JOptionPane.showMessageDialog(
                null,
                message,
                "Slot Machine",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}