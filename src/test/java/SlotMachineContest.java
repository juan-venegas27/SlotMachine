import java.util.ArrayList;

/**
 * Solves and simulates the Slot Machine contest problem.
 */
public class SlotMachineContest {
    private SlotMachine machine;
    private ArrayList<Action> solution;
    private int[] offsets;
    private int n;
    private boolean solved;

    /**
     * Creates an empty contest solver.
     */
    public SlotMachineContest() {
        solution = new ArrayList<Action>();
        machine = null;
        offsets = null;
        n = 0;
        solved = false;
    }

    /**
     * Solves the slot machine problem for the given number of wheels.
     * @param n number of wheels and symbols
     */
    public void solve(int n) {
        if (n < 3) {
            return;
        }
        initialize(n);
        boolean[] firstScan = scan(1);
        if (solved) {
            return;
        }
        int targetPosition = findPosition(firstScan, false);
        int referencePosition = findPosition(firstScan, true);
        int[] targetPositions =
        findTargetPositions(targetPosition, referencePosition);
        if (solved) {
            return;
        }
        alignWheels(targetPositions);
        solved = true;
    }

    /**
     * Initializes the machine and the data used by the solver.
     * @param n number of wheels and symbols
     */
    private void initialize(int n) {
        this.n = n;
        solution.clear();
        offsets = new int[n];
        solved = false;
        machine = new SlotMachine(n);
        machine.makeInvisible();
    }
    
    /**
     * Finds the first position whose value matches the requested value.
     * @param values boolean array obtained from a scan
     * @param wanted value to search for
     * @return position of the matching value, or -1 if not found
     */
    private int findPosition(boolean[] values, boolean wanted) {
        for (int i = 0; i < n; i++) {
            if (values[i] == wanted) {
                return (i + 1) % n;
            }
        }
        return -1;
    }
    
    /**
     * Finds the target position of the common symbol on every wheel.
     * @param targetPosition position of the target symbol on the first wheel
     * @param referencePosition position of the reference symbol
     * @return target positions for all wheels
     */
    private int[] findTargetPositions(int targetPosition, int referencePosition) {
        int[] positions = new int[n];
        positions[0] = targetPosition;
        rotateTo(1, targetPosition);
        for (int wheel = 2; wheel <= n; wheel++) {
            boolean[] withTarget = scan(wheel);
            if (solved) {
                return positions;
            }
            rotateTo(1, referencePosition);
            if (solved) {
                return positions;
            }
            boolean[] withReference = scan(wheel);
            if (solved) {
                return positions;
            }
            rotateTo(1, targetPosition);
            if (solved) {
                return positions;
            }
            positions[wheel - 1] = findDifference(withTarget, withReference);
        }
        return positions;
    }
    
    /**
     * Aligns every wheel using its previously identified target position.
     * @param positions target position for each wheel
     */
    private void alignWheels(int[] positions) {
        for (int wheel = 2; wheel <= n; wheel++) {
            rotateTo(wheel, positions[wheel - 1]);
            if (solved) {
                return;
            }
        }
    }
    
    /**
     * Simulates the stored solution visibly.
     * If no solution exists yet, solve is executed first.
     * @param n Number of wheels and symbols.
     */
    public void simulate(int n) {
        if (machine == null || this.n != n) {
            solve(n);
        }
        if (machine == null) {
            return;
        }
        restoreInitialState();
        machine.makeVisible();
        for (Action action : solution) {
            machine.spin(action.getWheel(),action.getSteps());
        }
    }

    /**
     * Scans every position of one wheel and records
     * the positions that produce the minimum distinct count.
     * @param wheel Wheel to scan.
     * @return Boolean mask of minimum positions.
     */
    private boolean[] scan(int wheel) {
        int[] counts = new int[n];
        for (int i = 0; i < n; i++) {
            counts[i] = apply(wheel, 1);
            if (solved) {
                return null;
            }
        }
        int minimum = counts[0];
        for (int i = 1; i < n; i++) {
            if (counts[i] < minimum) {
                minimum = counts[i];
            }
        }
        boolean[] result = new boolean[n];
        for (int i = 0; i < n; i++) {
            result[i] = counts[i] == minimum;
        }
        return result;
    }

    /**
     * Finds the position that is present in the first scan
     * and absent in the second scan.
     * @param first First scan mask.
     * @param second Second scan mask.
     * @return Relative position, or -1 if none exists.
     */
    private int findDifference(boolean[] first, boolean[] second) {
        for (int i = 0; i < n; i++) {
            if (first[i] && !second[i]) {
                return (i + 1) % n;
            }
        }
        return -1;
    }

    /**
     * Rotates a wheel to a target offset using the shortest direction.
     * @param wheel Wheel to rotate.
     * @param target Target offset.
     */
    private void rotateTo(int wheel, int target) {
        int current = offsets[wheel - 1];
        int difference = target - current;
        difference = normalize(difference);
        if (difference > n / 2) {
            difference -= n;
        }
        if (difference == 0) {
            return;
        }
        apply(wheel, difference);
    }

    /**
     * Executes and stores one rotation.
     * @param wheel Wheel to rotate.
     * @param steps Number of steps.
     * @return Current number of distinct symbols.
     */
    private int apply(int wheel, int steps) {
        if (steps == 0) {
            return machine.distinctSymbols();
        }
        machine.spin(wheel, steps);
        solution.add(new Action(wheel, steps));
        offsets[wheel - 1] = normalize(offsets[wheel - 1] + steps);
        int distinct = machine.distinctSymbols();
        if (distinct == 1) {
            solved = true;
        }
        return distinct;
    }

    /**
     * Restores the initial state by reversing all stored actions.
     */
    private void restoreInitialState() {
        for (int i = solution.size() - 1; i >= 0; i--) {
            Action action = solution.get(i);
            machine.spin(action.getWheel(),-action.getSteps());
        }
        offsets = new int[n];
        solved = false;
    }

    /**
     * Normalizes a number to the cyclic range [0, n-1].
     * @param value Number to normalize.
     * @return Normalized value.
     */
    private int normalize(int value) {
        int result = value % n;
        if (result < 0) {
            result += n;
        }
        return result;
    }
}