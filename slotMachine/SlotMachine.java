import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Represents a slot machine composed of several wheels.
 */
public class SlotMachine {
    private ArrayList<Wheel> wheels;
    private Rectangle machineFrame;
    private Circle jackpotIndicator;
    private boolean isVisible;
    private boolean isOk;

    /** Creates an empty slot machine with its default visual components. */
    public SlotMachine() {
        wheels = new ArrayList<>();
        isVisible = false;
        isOk = true;

        machineFrame = new Rectangle();
        machineFrame.changeSize(270, 280);
        machineFrame.moveTo(10, 10);
        machineFrame.changeColor("black");

        jackpotIndicator = new Circle();
        jackpotIndicator.changeSize(30);
        jackpotIndicator.moveTo(135, 175);
        jackpotIndicator.changeColor("yellow");
    }

    /**
     * Adds a new wheel at the specified position.
     * @param pos the wheel position.
     */
    public void addWheel(int pos) {
        Wheel newWheel = new Wheel();

        if (pos < 1) {
            pos = 1;
        } else if (pos > wheels.size() + 1) {
            pos = wheels.size() + 1;
        }

        wheels.add(pos - 1, newWheel);

        positionWheels();
        isOk = true;

        if (isVisible) {
            newWheel.makeVisible();
        }
    }

    /**
     * Removes the wheel at the specified position.
     * @param pos the wheel position.
     */
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            fail("There are no wheels to delete.");
            return;
        }

        if (pos < 1) {
            pos = 1;
        } else if (pos > wheels.size()) {
            pos = wheels.size();
        }

        Wheel wheel = wheels.remove(pos - 1);
        wheel.makeInvisible();

        positionWheels();
        isOk = true;
        updateJackpotIndicator();
    }

    /**
     * Adds a colored symbol to the specified wheel.
     * @param pos the wheel position.
     * @param color the symbol color.
     */
    public void addSymbol(int pos, String color) {
        if (!validColor(color)) {
            fail("Invalid color.");
            return;
        }

        Wheel selectedWheel = getWheel(pos);

        if (selectedWheel == null) {
            fail("There are no wheels.");
            return;
        }

        if (containsColor(selectedWheel.symbols(), color)) {
            fail("The color already exists in that wheel.");
            return;
        }

        selectedWheel.addSymbol(
            selectedWheel.countSymbols() + 1,
            color
        );

        isOk = true;
        updateJackpotIndicator();
    }

    /**
     * Deletes a symbol using its color.
     * @param symbol the symbol color.
     */
    public void delSymbol(String symbol) {
        if (wheels.isEmpty()) {
            fail("There are no wheels.");
            return;
        }

        boolean found = false;

        for (Wheel wheel : wheels) {
            if (wheel.delSymbol(symbol)) {
                found = true;
            }
        }

        if (found) {
            isOk = true;
            updateJackpotIndicator();
        } else {
            fail("The symbol does not exist.");
        }
    }

    /**
     * Places a symbol in the specified wheel.
     * @param wheel the wheel position.
     * @param symbol the symbol color.
     */
    public void placeSymbol(int wheel, String symbol) {
        Wheel selectedWheel = getWheel(wheel);

        if (selectedWheel == null) {
            fail("There are no wheels.");
            return;
        }

        if (selectedWheel.placeSymbol(symbol)) {
            isOk = true;
            updateJackpotIndicator();
        } else {
            fail("The symbol does not exist in that wheel.");
        }
    }

    /**
     * Spins the specified wheel once.
     * @param wheel the wheel position.
     */
    public void spinWheel(int wheel) {
        Wheel selectedWheel = getWheel(wheel);

        if (selectedWheel == null) {
            fail("There are no wheels.");
            return;
        }

        selectedWheel.spin();
        isOk = true;
        updateJackpotIndicator();
    }

    /** Spins all wheels once. */
    public void spin() {
        if (wheels.isEmpty()) {
            fail("There are no wheels.");
            return;
        }

        for (Wheel wheel : wheels) {
            wheel.spin();
        }

        isOk = true;
        updateJackpotIndicator();
    }

    /**
     * Returns the symbols in the first wheel.
     * @return the wheel symbols in order.
     */
    public String[] symbols() {
        if (wheels.isEmpty()) {
            return new String[0];
        }

        return wheels.get(0).symbols();
    }

    /**
     * Counts the distinct symbols in the first wheel.
     * @return the number of distinct symbols.
     */
    public int distinctSymbols() {
        if (wheels.isEmpty()) {
            return 0;
        }

        String[] symbols = wheels.get(0).symbols();
        int count = 0;

        for (int i = 0; i < symbols.length; i++) {
            boolean repeated = false;

            for (int j = 0; j < i; j++) {
                if (symbols[i].equals(symbols[j])) {
                    repeated = true;
                    break;
                }
            }

            if (!repeated) {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns the visible symbols from left to right.
     * @return the current configuration of the machine.
     */
    public String[] configuration() {
        String[] result = new String[wheels.size()];

        for (int i = 0; i < wheels.size(); i++) {
            result[i] = wheels.get(i).getCurrentSymbol();
        }

        return result;
    }

    /**
     * Determines whether all wheels display the same symbol.
     * @return true if the machine is in jackpot state.
     */
    public boolean isJackpot() {
        if (wheels.isEmpty()) {
            return false;
        }

        String first = wheels.get(0).getCurrentSymbol();

        if (first == null) {
            return false;
        }

        for (int i = 1; i < wheels.size(); i++) {
            String current = wheels.get(i).getCurrentSymbol();

            if (current == null || !current.equals(first)) {
                return false;
            }
        }

        return true;
    }

    /** Makes the machine and its components visible. */
    public void makeVisible() {
        isVisible = true;

        machineFrame.makeVisible();

        for (Wheel wheel : wheels) {
            wheel.makeVisible();
        }

        jackpotIndicator.makeVisible();
        updateJackpotIndicator();
    }

    /** Makes the machine and its components invisible. */
    public void makeInvisible() {
        isVisible = false;

        jackpotIndicator.makeInvisible();

        for (Wheel wheel : wheels) {
            wheel.makeInvisible();
        }

        machineFrame.makeInvisible();
    }

    /** Exits the slot machine application. */
    public void exit() {
        makeInvisible();
        System.exit(0);
    }

    /**
     * Checks whether the last operation was successful.
     * @return true if the last operation was successful.
     */
    public boolean ok() {
        return isOk;
    }

    /**
     * Returns the wheel at the specified position.
     * @param pos the wheel position.
     * @return the selected wheel or null if there are no wheels.
     */
    private Wheel getWheel(int pos) {
        if (wheels.isEmpty()) {
            return null;
        }

        if (pos < 1) {
            pos = 1;
        } else if (pos > wheels.size()) {
            pos = wheels.size();
        }

        return wheels.get(pos - 1);
    }

    /** Positions all wheels inside the machine frame. */
    private void positionWheels() {
        int startX = 30;
        int y = 65;
        int spacing = 90;

        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).moveTo(startX + i * spacing, y);
        }
    }

    /** Updates the jackpot indicator according to the machine state. */
    private void updateJackpotIndicator() {
        if (isJackpot()) {
            jackpotIndicator.changeColor("red");
        } else {
            jackpotIndicator.changeColor("yellow");
        }
    }

    /**
     * Checks whether a color is already present.
     * @param colors the colors in the wheel.
     * @param color the color to search for.
     * @return true if the color exists.
     */
    private boolean containsColor(String[] colors, String color) {
        for (String current : colors) {
            if (current.equals(color)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether a color is supported by the canvas.
     * @param color the color to validate.
     * @return true if the color is supported.
     */
    private boolean validColor(String color) {
        if (color == null) {
            return false;
        }

        String c = color.trim().toLowerCase();

        return c.equals("red") ||
               c.equals("black") ||
               c.equals("blue") ||
               c.equals("yellow") ||
               c.equals("green") ||
               c.equals("magenta") ||
               c.equals("white") ||
               c.equals("cyan") ||
               c.equals("gray") ||
               c.equals("grey") ||
               c.equals("darkgray") ||
               c.equals("darkgrey") ||
               c.equals("lightgray") ||
               c.equals("lightgrey") ||
               c.equals("orange") ||
               c.equals("pink") ||
               c.equals("gold") ||
               c.equals("purple") ||
               c.equals("lime") ||
               c.equals("navy") ||
               c.equals("teal") ||
               c.equals("maroon") ||
               c.equals("olive") ||
               c.equals("silver") ||
               c.equals("brown") ||
               c.equals("violet") ||
               c.equals("indigo") ||
               c.equals("coral") ||
               c.equals("crimson");
    }

    /**
     * Marks the operation as unsuccessful and shows a message if visible.
     * @param message the error message.
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