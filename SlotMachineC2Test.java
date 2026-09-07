import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for Slot Machine requirements in invisible mode.
 */
public class SlotMachineC2Test {
    private SlotMachine machine;

    /**
     * Sets up a standard 3-wheel slot machine in invisible mode.
     */
    @Before
    public void setUp() {
        machine = new SlotMachine();
        machine.makeInvisible();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);
        machine.addSymbol(1, "red");
        machine.addSymbol(1, "blue");
        machine.addSymbol(2, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "red");
        machine.addSymbol(3, "blue");
    }

    /**
     * Tests swapping positions of two valid wheels.
     */
    @Test
    public void shouldSwapWheelsCorrectly() {
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "blue");
        machine.swap(1, 2);
        assertTrue(machine.ok());
        String[] config = machine.configuration();
        assertEquals("blue", config[0]);
        assertEquals("red", config[1]);
    }

    /**
     * Tests that a locked wheel rejects rotation and maintains its symbol.
     */
    @Test
    public void shouldNotSpinLockedWheel() {
        machine.placeSymbol(1, "red");
        machine.lock(1);
        assertTrue(machine.ok());
        machine.spin(1, 2);
        assertFalse(machine.ok());
        assertEquals("red", machine.configuration()[0]);
        machine.unlock(1);
        assertTrue(machine.ok());
    }

    /**
     * Tests rotating a wheel by a positive number of steps.
     */
    @Test
    public void shouldRotateExactNumberOfSteps() {
        machine.placeSymbol(1, "red");
        machine.spin(1, 1);
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[0]);
    }

    /**
     * Tests that negative step rotation is rejected.
     */
    @Test
    public void shouldFailWhenSpinningNegativeSteps() {
        machine.spin(1, -3);
        assertFalse(machine.ok());
    }

    /**
     * Tests setting a custom valid symbol configuration.
     */
    @Test
    public void shouldSetCustomConfiguration() {
        String[] target = {"blue", "red", "blue"};
        machine.spin(target);
        assertTrue(machine.ok());
        assertArrayEquals(target, machine.configuration());
    }

    /**
     * Tests that configuring an absent symbol causes operation failure.
     */
    @Test
    public void shouldFailConfigurationWithMissingSymbol() {
        String[] invalid = {"gold", "red", "blue"};
        machine.spin(invalid);
        assertFalse(machine.ok());
    }
}