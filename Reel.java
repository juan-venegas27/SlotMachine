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
        wheels = new ArrayList<>();
        isVisible = true;
    }

    /**
     * Adds a new wheel at the specified position.
     * @param pos Position where the wheel will be inserted (1 for the first wheel on the left).
     */
    public void addWheel(int pos) {
        Wheel newWheel = new Wheel();
        int idx = clamp(pos, wheels.size() + 1) - 1;
        wheels.add(idx, newWheel);
    }

    /**
     * Removes the wheel at the specified position.
     * @param pos Number of the wheel to remove (1 for the first wheel).
     * @return The removed wheel, or null if there are no wheels.
     */
    public Wheel delWheel(int pos) {
        if (wheels.isEmpty()) return null;

        int idx = clamp(pos, wheels.size()) - 1;
        return wheels.remove(idx);
    }

    /**
     * Obtains the wheel located at a given position.
     * @param pos Number of the desired wheel (1 for the first wheel).
     * @return The requested wheel, or null if no wheels exist.
     */
    public Wheel getWheel(int pos) {
        if (wheels.isEmpty()) return null;
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
     * @return The first unlocked wheel, or null if all wheels are locked or none exist.
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
     * Swaps the positions of two wheels.
     * @param wheel1 Number of the first wheel to swap.
     * @param wheel2 Number of the second wheel to swap.
     */
    public void swap(int wheel1, int wheel2) {
        if (wheels.size() < 2) return;

        int idx1 = clamp(wheel1, wheels.size()) - 1;
        int idx2 = clamp(wheel2, wheels.size()) - 1;

        Wheel temp = wheels.get(idx1);
        wheels.set(idx1, wheels.get(idx2));
        wheels.set(idx2, temp);
    }

    /**
     * Returns the colors available in the first wheel.
     * @return Array containing the colors of the symbols.
     */
    public String[] symbols() {
        return wheels.isEmpty() ? new String[0] : wheels.get(0).symbols();
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
            turns[i] = 2 + random.nextInt(6);
            if (turns[i] > maxTurns) {
                maxTurns = turns[i];
            }
        }
        for (int step = 0; step < maxTurns; step++) {
            for (int i = 0; i < wheels.size(); i++) {
                if (step < turns[i]) {
                    wheels.get(i).spinSteps(1);
                }
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
     * Calculates the animation delay for the current spin step.
     * The delay increases near the end to create a slowing effect.
     * @param step Current animation step.
     * @param totalSteps Total number of animation steps.
     * @return Delay in milliseconds.
     */
    private int animationDelay(int step, int totalSteps) {
        double progress = (double) step / totalSteps;
    
        if (progress < 0.50) {
            return 25;
        }
    
        if (progress < 0.75) {
            return 35;
        }
    
        if (progress < 0.90) {
            return 50;
        }
    
        return 75;
    }
}