package SLOTMACHINEFINAL;

import java.util.ArrayList;
import java.util.Random;

/**
 * Manages the wheels that belong to the slot machine.
 */
public class Reel {
    private ArrayList<Wheel> wheels;
    private boolean isVisible;
    private static final Random random = new Random();

    /**
     * Creates an empty reel without visible wheels.
     */
    public Reel() {
        wheels = new ArrayList<Wheel>();
        isVisible = false;
    }

    /**
     * Adds a normal wheel at the specified position.
     * @param pos Position where the wheel will be inserted.
     */
    public void addWheel(int pos) {
        addWheel(pos, "normal");
    }

    /**
     * Adds a wheel of the requested type at the specified position.
     * @param pos Position where the wheel will be inserted.
     * @param type Wheel type.
     */
    public void addWheel(int pos, String type) {
        Wheel newWheel = WheelCreator.create(type);
        int idx = clamp(pos, wheels.size() + 1) - 1;
        wheels.add(idx, newWheel);
        updateLeftWheels();
    }

    /**
     * Removes the wheel at the specified position.
     * @param pos Number of the wheel to remove.
     * @return The removed wheel, or null if there are no wheels.
     */
    public Wheel delWheel(int pos) {
        if (wheels.isEmpty()) {
            return null;
        }
        int idx = clamp(pos, wheels.size()) - 1;
        if (!wheels.get(idx).canDelete()) {
            return null;
        }
        Wheel removed = wheels.remove(idx);
        updateLeftWheels();
        return removed;
    }

    /**
     * Obtains the wheel located at a given position.
     * @param pos Number of the desired wheel.
     * @return The requested wheel, or null if no wheels exist.
     */
    public Wheel getWheel(int pos) {
        if (wheels.isEmpty()) {
            return null;
        }
        return wheels.get(clamp(pos, wheels.size()) - 1);
    }

    /**
     * Returns the number of wheels currently managed by the reel.
     * @return The current number of wheels.
     */
    public int size() {
        return wheels.size();
    }

    /**
     * Removes a symbol with the specified color from all wheels.
     * @param color Color of the symbol to remove.
     * @return True if the symbol was found in at least one wheel.
     */
    public boolean delSymbol(String color) {
        boolean found = false;
        for (Wheel wheel : wheels) {
            if (wheel.delSymbol(color)) {
                found = true;
            }
        }
        return found;
    }

    /**
     * Returns the first wheel that is not locked.
     * @return The first unlocked wheel, or null if all wheels are locked.
     */
    public Wheel firstUnlockedWheel() {
        for (Wheel wheel : wheels) {
            if (!wheel.isLocked()) {
                return wheel;
            }
        }
        return null;
    }

    /**
     * Checks whether two wheels can be exchanged.
     * @param wheel1 Number of the first wheel.
     * @param wheel2 Number of the second wheel.
     * @return True if both wheels permit exchanging.
     */
    public boolean canSwap(int wheel1, int wheel2) {
        Wheel first = getWheel(wheel1);
        Wheel second = getWheel(wheel2);
        return first != null && second != null
        && first.canSwap() && second.canSwap();
    }

    /**
     * Swaps the positions of two wheels.
     * @param wheel1 Number of the first wheel to swap.
     * @param wheel2 Number of the second wheel to swap.
     * @return True if the swap was performed.
     */
    public boolean swap(int wheel1, int wheel2) {
        if (!canSwap(wheel1, wheel2) || wheels.size() < 2) {
            return false;
        }
        int idx1 = clamp(wheel1, wheels.size()) - 1;
        int idx2 = clamp(wheel2, wheels.size()) - 1;
        Wheel temp = wheels.get(idx1);
        wheels.set(idx1, wheels.get(idx2));
        wheels.set(idx2, temp);
        updateLeftWheels();
        return true;
    }

    /**
     * Returns the colors available in the first wheel.
     * @return Array containing the colors of the symbols.
     */
    public String[] symbols() {
        return wheels.isEmpty()? new String[0] : wheels.get(0).symbols();
    }

    /**
     * Returns the symbols currently visible on all wheels.
     * @return Array with the visible color of each wheel.
     */
    public String[] configuration() {
        String[] res = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            res[i] = wheels.get(i).getCurrentSymbol();
        }
        return res;
    }

    /**
     * Makes all wheels visible.
     */
    public void makeVisible() {
        isVisible = true;
        for (Wheel wheel : wheels) {
            wheel.makeVisible();
        }
    }

    /**
     * Makes all wheels invisible.
     */
    public void makeInvisible() {
        isVisible = false;
        for (Wheel wheel : wheels) {
            wheel.makeInvisible();
        }
    }

    /**
     * Spins all unlocked wheels using an animated sequence.
     */
    public void spin() {
        if (wheels.isEmpty()) {
            return;
        }
        int[] turns = new int[wheels.size()];
        int maxTurns = 0;
        for (int i = 0; i < wheels.size(); i++) {
            if (wheels.get(i).isLocked()) {
                turns[i] = 0;
            } else {
                turns[i] = 2 + random.nextInt(6);
                if (turns[i] > maxTurns) {
                    maxTurns = turns[i];
                }
            }
        }
        for (int step = 0; step < maxTurns; step++) {
            for (int i = 0; i < wheels.size(); i++) {
                if (step < turns[i]) {
                    wheels.get(i).spinAnimationStep();
                }
            }
        }
        for (int i = 0; i < wheels.size(); i++) {
            if (turns[i] > 0) {
                wheels.get(i).finishSpin();
            }
        }
    }
    
    /**
     * Positions all wheels using the original slot machine layout.
     */
    public void positionWheels() {
        int startX = 35;
        int y = 50;
        int spacing = 80;
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).moveTo(startX + (i * spacing), y);
            if (isVisible) {
                wheels.get(i).makeVisible();
            }
        }
    }

    /**
     * Keeps a number within the valid range of wheel positions.
     * @param val Requested position.
     * @param max Maximum allowed position.
     * @return Number adjusted between 1 and the maximum allowed.
     */
    private int clamp(int val, int max) {
        return Math.max(1, Math.min(val, max));
    }

    /**
     * Updates the left-wheel references after a structural change.
     */
    private void updateLeftWheels() {
        for (int i = 0; i < wheels.size(); i++) {
            Wheel left = i == 0 ? null : wheels.get(i - 1);
            wheels.get(i).setLeftWheel(left);
        }
    }
}
