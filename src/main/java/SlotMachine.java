import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;
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
     * Creates a new slot machine without wheels.
     */
    public SlotMachine() {
        reel = new Reel();
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
     * Creates a slot machine with n normal wheels.
     * @param n Number of wheels and symbols.
     */
    public SlotMachine(int n) {
        this();
        if (n < 1) {
            return;
        }
        String[] colors = generateColors(n);
        for (int i = 0; i < n; i++) {
            reel.addWheel(i + 1, "normal");
            Wheel wheel = reel.getWheel(i + 1);
            for (int j = 0; j < n; j++) {
                wheel.addSymbol(j + 1, colors[j],"normal" );
            }
            wheel.placeSymbol( colors[random.nextInt(colors.length)]);
        }
        positionWheels();
        isOk = true;
    }

    /**
     * Generates n different random colors.
     * @param n Number of colors.
     * @return Generated colors.
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
     * Generates a random hexadecimal color.
     * @return Generated color.
     */
    private String generateRandomColor() {
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return String.format("#%02X%02X%02X",red,green,blue);
    }

    /**
     * Adds a normal wheel.
     * @param pos Position of the wheel.
     */
    public void addWheel(int pos) {
        addWheel(pos, "normal");
    }

    /**
     * Adds a wheel of the requested type.
     * @param pos Wheel position.
     * @param type Wheel type.
     */
    public void addWheel(int pos, String type) {
        try {
            reel.addWheel(pos, type);
            positionWheels();
            isOk = true;
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Removes a wheel.
     * @param pos Wheel position.
     */
    public void delWheel(int pos) {
        if (reel.size() == 0) {
            fail("No wheels available to delete.");
            return;
        }
        Wheel target = getWheel(pos);
        if (target == null) {
            fail("Invalid wheel index.");
            return;
        }
        if (!target.canDelete()) {
            fail("Wheel cannot be deleted.");
            return;
        }
        Wheel wheel = reel.delWheel(pos);
        if (wheel == null) {
            fail("Wheel could not be deleted.");
            return;
        }
        wheel.makeInvisible();
        positionWheels();
        isOk = true;
        updateJackpotIndicator();
    }

    /**
     * Adds a normal symbol.
     * @param pos Wheel position.
     * @param color Symbol color.
     */
    public void addSymbol(int pos, String color) {
        addSymbol(pos, color, "normal");
    }

    /**
     * Adds a symbol of the requested type.
     * @param pos Wheel position.
     * @param color Symbol color.
     * @param type Symbol type.
     */
    public void addSymbol(int pos,String color,String type) {
        if (!isValidColor(color)) {
            fail("Invalid CSS or hex color: " + color);
            return;
        }
        Wheel wheel = getWheel(pos);
        if (wheel == null) {
            fail("No wheels available to add symbol.");
            return;
        }
        try {
            wheel.addSymbol(wheel.countSymbols() + 1,color,type   );
            isOk = true;
            updateJackpotIndicator();
        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Removes a symbol from every wheel.
     * @param symbol Symbol color.
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
     * Places a symbol on a wheel.
     * @param wheel Wheel number.
     * @param symbol Symbol color.
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
     * Spins one wheel randomly.
     * @param wheel Wheel number.
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
     * Spins all wheels.
     */
    public void spin() {
        if (reel.size() == 0) {
            fail("No wheels available to spin.");
            return;
        }
        reel.spin();
        isOk = true;
        updateJackpotIndicator();
    }

    /**
     * Spins one wheel by an exact number of steps.
     * @param wheel Wheel number.
     * @param steps Number of positions.
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
     * Sets the configuration of all wheels.
     * @param setSymbols Symbols to display from left to right.
     */
    public void spin(String[] setSymbols) {
        if (setSymbols == null|| setSymbols.length != reel.size()) {
            fail("Configuration length must match wheel count.");
            return;
        }
        for (int i = 0; i < reel.size(); i++) {
            if (!reel.getWheel(i + 1).containsSymbol(setSymbols[i])) {
                fail("Symbol " + setSymbols[i]+ " not found in wheel " + (i + 1));
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
     * Swaps two wheels.
     * @param wheel1 First wheel.
     * @param wheel2 Second wheel.
     */
    public void swap(int wheel1, int wheel2) {
        if (reel.size() < 2) {
            fail("At least 2 wheels required to swap.");
            return;
        }
        if (!reel.canSwap(wheel1, wheel2)) {
            fail("One of the selected wheels cannot be swapped.");
            return;
        }
        if (!reel.swap(wheel1, wheel2)) {
            fail("Wheel swap failed.");
            return;
        }
        positionWheels();
        isOk = true;
        updateJackpotIndicator();
    }

    /**
     * Locks a wheel.
     * @param wheel Wheel number.
     */
    public void lock(int wheel) {
        Wheel target = getWheel(wheel);
        if (target == null) {
            fail("Invalid wheel index.");
            return;
        }
        if (!target.canLock()) {
            fail("Wheel cannot be locked.");
            return;
        }
        target.lock();
        updateJackpotIndicator();
        isOk = true;
    }

    /**
     * Unlocks a wheel.
     * @param wheel Wheel number.
     */
    public void unlock(int wheel) {
        Wheel target = getWheel(wheel);
        if (target == null) {
            fail("Invalid wheel index.");
            return;
        }
        target.unlock();
        updateJackpotIndicator();
        isOk = true;
    }

    /**
     * Returns symbols available in the first wheel.
     * @return Symbol colors.
     */
    public String[] symbols() {
        return reel.symbols();
    }

    /**
     * Counts different visible symbols.
     * @return Number of different visible colors.
     */
    public int distinctSymbols() {
        if (reel.size() == 0) {
            return 0;
        }
        String[] configuration = configuration();
        ArrayList<String> unique = new ArrayList<String>();
        for (String color : configuration) {
            if (color != null&& !unique.contains(color)) {
                unique.add(color);
            }
        }
        return unique.size();
    }

    /**
     * Returns the visible configuration.
     * @return Visible symbol colors.
     */
    public String[] configuration() {
        return reel.configuration();
    }

/**
 * Checks if all unlocked wheels can represent the same winning symbol.
 * Wildcard symbols can match any color.
 * Locked wheels are ignored.
 * Hidden symbols are not counted as jackpots.
 * @return True when the configuration is a jackpot.
 */
public boolean isjackpot() {
    if (reel.size() == 0) {
        return false;
    }

    String targetColor = null;
    boolean foundWildcard = false;
    boolean foundUnlockedWheel = false;

    for (int i = 1; i <= reel.size(); i++) {
        Wheel wheel = reel.getWheel(i);

        if (wheel.isLocked()) {
            continue;
        }

        foundUnlockedWheel = true;

        String color = wheel.getCurrentSymbol();

        if (color == null) {
            return false;
        }

        if (!wheel.currentSymbolIsDisplayEnabled()) {
            return false;
        }

        if (wheel.currentSymbolIsWildcard()) {
            foundWildcard = true;
            continue;
        }

        if (targetColor == null) {
            targetColor = color;
        } else if (!targetColor.equalsIgnoreCase(color)) {
            return false;
        }
    }

    return foundUnlockedWheel && (targetColor != null || foundWildcard);
}


    /**
     * Makes the machine visible.
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
     * Hides the machine.
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
     * Closes the simulator.
     */
    public void exit() {
        makeInvisible();
        System.exit(0);
    }

    /**
     * Indicates whether the last operation was successful.
     * @return True when successful.
     */
    public boolean ok() {
        return isOk;
    }

    /**
     * Gets a wheel by position.
     * @param pos Wheel position.
     * @return Requested wheel or null.
     */
    private Wheel getWheel(int pos) {
        return reel.getWheel(pos);
    }

    /**
     * Updates the machine visual layout.
     */
    private void positionWheels() {
        int spacing = 80;
        int totalWidth = Math.max(260,reel.size() * spacing) + 50;
        int leverX = totalWidth + 18;
        leverBase.moveTo(leverX - 8, 145);
        leverBar.moveTo(leverX + 12, 90);
        leverKnob.moveTo(leverX + 3, 72);
        machineOuterFrame.moveTo(10, 10);
        machineOuterFrame.changeSize(270, totalWidth);
        machineInnerFrame.moveTo(20, 20);
        machineInnerFrame.changeSize(250,totalWidth - 20);
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
     * Updates the jackpot indicator.
     */
    private void updateJackpotIndicator() {
        if (!isVisible) {
            return;
        }
        if (isjackpot()) {
            jackpotOuter.changeColor("#FFD700");
            jackpotBulb.changeColor("#FF073A");
        } else {
            jackpotOuter.changeColor("#8A7322");
            jackpotBulb.changeColor("#3B2200");
        }
    }

    /**
     * Checks whether a color is valid.
     * @param colorString Color text.
     * @return True when the color is supported.
     */
    private boolean isValidColor(String colorString) {
        if (colorString == null || colorString.trim().isEmpty()) {
            return false;
        }
        String color = colorString.trim();
        if (color.startsWith("#") && (color.length() == 7 || color.length() == 4)) {
            try {
                Color.decode(color);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return isCssName(color.toLowerCase());
    }

    /**
     * Checks whether a CSS color name is supported.
     * @param color Color name in lowercase.
     * @return True when supported.
     */
    private boolean isCssName(String color) {
        String[] validColors = {
            "red", "black", "blue", "yellow", "green",
            "magenta", "white", "cyan", "gray", "orange",
            "pink", "gold", "purple", "silver", "brown",
            "coral", "crimson", "navy", "lime", "teal"
        };
        for (String validColor : validColors) {
            if (validColor.equals(color)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Registers an operation failure.
     * @param message Error description.
     */
    private void fail(String message) {
        isOk = false;
        if (isVisible) {
            JOptionPane.showMessageDialog(null,message,"Slot Machine",
            JOptionPane.ERROR_MESSAGE
            );
        }
    }
}