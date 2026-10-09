package SLOTMACHINEFINAL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

/** Pruebas de regresion para las funcionalidades publicas de la maquina. */
public class SlotMachineTest {
    private SlotMachine machine;
    private final List<SlotMachine> toClose =
        new ArrayList<SlotMachine>();

    @Before
    public void setUp() {
        machine = newEmptyMachine();

        for (int i = 1; i <= 3; i++) {
            machine.addWheel(i, "normal");
        }

        for (int i = 1; i <= 3; i++) {
            addBasicSymbols(machine, i);
        }

        machine.spin(new String[] {"red", "green", "blue"});
    }

    @After
    public void tearDown() {
        for (SlotMachine current : toClose) {
            current.makeInvisible();
        }
    }

    private SlotMachine newEmptyMachine() {
        SlotMachine result = new SlotMachine();
        result.makeInvisible();
        toClose.add(result);
        return result;
    }

    private SlotMachine newMachine(String... types) {
        SlotMachine result = newEmptyMachine();

        for (int i = 0; i < types.length; i++) {
            result.addWheel(i + 1, types[i]);
        }

        return result;
    }

    private void addBasicSymbols(SlotMachine target, int wheel) {
        target.addSymbol(wheel, "red");
        target.addSymbol(wheel, "green");
        target.addSymbol(wheel, "blue");
    }

    // CREACION DE LA MAQUINA

    @Test
    public void shouldCreateAnEmptyMachine() {
        SlotMachine empty = newEmptyMachine();

        assertEquals(0, empty.configuration().length);
        assertFalse(empty.isjackpot());
    }

    @Test
    public void shouldCreateMachineWithRequestedNumberOfWheels() {
        SlotMachine generated = new SlotMachine(4);
        generated.makeInvisible();
        toClose.add(generated);

        assertEquals(4, generated.configuration().length);
        assertEquals(4, generated.symbols().length);
    }

    @Test
    public void shouldNotCreateWheelsWhenCountIsZero() {
        SlotMachine empty = new SlotMachine(0);
        empty.makeInvisible();
        toClose.add(empty);

        assertEquals(0, empty.configuration().length);
        assertFalse(empty.isjackpot());
    }

    @Test
    public void shouldAddANormalWheel() {
        SlotMachine empty = newEmptyMachine();

        empty.addWheel(1);

        assertTrue(empty.ok());
        assertEquals(1, empty.configuration().length);
    }

    @Test
    public void shouldAddNormalLeftyAndRebelWheels() {
        SlotMachine varied =
            newMachine("normal", "lefty", "rebel");

        assertTrue(varied.ok());
        assertEquals(3, varied.configuration().length);
    }

    @Test
    public void shouldInsertWheelAtRequestedPosition() {
        SlotMachine inserted = newMachine("normal", "normal");

        inserted.addSymbol(1, "red");
        inserted.addSymbol(2, "blue");

        inserted.addWheel(2, "normal");
        inserted.addSymbol(2, "green");

        assertArrayEquals(
            new String[] {"red", "green", "blue"},
            inserted.configuration()
        );
    }

    @Test
    public void shouldDeleteANormalWheel() {
        SlotMachine pair = newMachine("normal", "normal");

        pair.addSymbol(1, "red");
        pair.addSymbol(2, "blue");

        pair.delWheel(1);

        assertTrue(pair.ok());
        assertEquals(1, pair.configuration().length);
        assertEquals("blue", pair.configuration()[0]);
    }

    @Test
    public void shouldNotDeleteARebelWheel() {
        SlotMachine varied = newMachine("normal", "rebel");

        varied.addSymbol(1, "red");
        varied.addSymbol(2, "blue");

        String[] before = varied.configuration();

        varied.delWheel(2);

        assertFalse(varied.ok());
        assertArrayEquals(before, varied.configuration());
        assertEquals(2, varied.configuration().length);
    }

    @Test
    public void shouldSwapTwoNormalWheels() {
        machine.swap(1, 3);

        assertTrue(machine.ok());

        assertArrayEquals(
            new String[] {"blue", "green", "red"},
            machine.configuration()
        );
    }

    @Test
    public void shouldNotSwapWheelsWhenRebelIsInvolved() {
        SlotMachine varied = newMachine("normal", "rebel");

        varied.addSymbol(1, "red");
        varied.addSymbol(2, "blue");

        String[] before = varied.configuration();

        varied.swap(1, 2);

        assertFalse(varied.ok());
        assertArrayEquals(before, varied.configuration());
    }

    @Test
    public void shouldNotSwapWhenOnlyOneWheelExists() {
        SlotMachine single = newMachine("normal");
        single.addSymbol(1, "red");

        String[] before = single.configuration();

        single.swap(1, 1);

        assertFalse(single.ok());
        assertArrayEquals(before, single.configuration());
    }

    @Test
    public void shouldNotLockARebelWheel() {
        SlotMachine rebel = newMachine("rebel");
        rebel.addSymbol(1, "red");

        rebel.lock(1);

        assertFalse(rebel.ok());

        rebel.spin(1);

        assertTrue(rebel.ok());
    }

    // AGREGAR, SELECCIONAR Y ELIMINAR SIMBOLOS

    @Test
    public void shouldAddNormalEphemeralAndShySymbols() {
        SlotMachine varied =
            newMachine("normal", "normal", "normal");

        varied.addSymbol(1, "red", "normal");
        varied.addSymbol(2, "green", "ephemeral");
        varied.addSymbol(3, "blue", "shy");

        assertTrue(varied.ok());

        assertArrayEquals(
            new String[] {"red", "green", "blue"},
            varied.configuration()
        );
    }

    @Test
    public void shouldNotAddSymbolWhenWheelDoesNotExist() {
        SlotMachine empty = newEmptyMachine();

        empty.addSymbol(1, "red", "normal");

        assertFalse(empty.ok());
        assertEquals(0, empty.configuration().length);
    }

    @Test
    public void shouldNotAddAnInvalidColor() {
        String[] before = machine.configuration();
        int symbolsBefore = machine.symbols().length;

        machine.addSymbol(1, "not-a-valid-color");

        assertFalse(machine.ok());
        assertArrayEquals(before, machine.configuration());
        assertEquals(symbolsBefore, machine.symbols().length);
    }

    @Test
    public void shouldDeleteSymbolColorFromAllWheels() {
        machine.delSymbol("green");

        assertTrue(machine.ok());

        assertArrayEquals(
            new String[] {"red", "blue"},
            machine.symbols()
        );
    }

    @Test
    public void shouldNotDeleteASymbolThatDoesNotExist() {
        String[] before = machine.configuration();

        machine.delSymbol("purple");

        assertFalse(machine.ok());
        assertArrayEquals(before, machine.configuration());
    }

    @Test
    public void shouldPlaceAnExistingSymbolOnASelectedWheel() {
        machine.placeSymbol(2, "blue");

        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[1]);
    }

    @Test
    public void shouldNotPlaceASymbolThatDoesNotExistOnTheWheel() {
        String[] before = machine.configuration();

        machine.placeSymbol(1, "purple");

        assertFalse(machine.ok());
        assertArrayEquals(before, machine.configuration());
    }

    @Test
    public void shouldAllowCaseInsensitiveSymbolSelection() {
        machine.placeSymbol(1, "BLUE");

        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[0]);
    }

    @Test
    public void shouldReturnSymbolsFromTheFirstWheel() {
        assertArrayEquals(
            new String[] {"red", "green", "blue"},
            machine.symbols()
        );
    }

    @Test
    public void shouldCountDifferentCurrentSymbols() {
        machine.spin(new String[] {"red", "green", "blue"});

        assertEquals(3, machine.distinctSymbols());
    }

    @Test
    public void shouldNotCountRepeatedCurrentSymbolsMoreThanOnce() {
        machine.spin(new String[] {"red", "red", "green"});

        assertEquals(2, machine.distinctSymbols());
    }

    // GIROS ALEATORIOS, EXACTOS Y CONFIGURACIONES PREDEFINIDAS

    @Test
    public void shouldSpinOneWheel() {
        machine.spin(1);

        assertTrue(machine.ok());
        assertEquals(3, machine.configuration().length);
    }

    @Test
    public void shouldSpinAllWheels() {
        machine.spin();

        assertTrue(machine.ok());
        assertEquals(3, machine.configuration().length);

        for (String color : machine.configuration()) {
            assertNotNull(color);
        }
    }

    @Test
    public void shouldMoveForwardTheRequestedNumberOfSteps() {
        machine.spin(1, 1);

        assertTrue(machine.ok());
        assertEquals("green", machine.configuration()[0]);
    }

    @Test
    public void shouldMoveBackwardTheRequestedNumberOfSteps() {
        machine.spin(1, -1);

        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[0]);
    }

    @Test
    public void shouldNotChangeConfigurationWhenStepCountIsZero() {
        String[] before = machine.configuration();

        machine.spin(1, 0);

        assertArrayEquals(before, machine.configuration());
    }

    @Test
    public void shouldNotSpinALockedWheel() {
        String[] before = machine.configuration();

        machine.lock(1);
        assertTrue(machine.ok());

        machine.spin(1, 1);

        assertFalse(machine.ok());
        assertArrayEquals(before, machine.configuration());
    }

    @Test
    public void shouldAllowSpinningAfterUnlockingAWheel() {
        machine.lock(1);
        machine.unlock(1);
        machine.spin(1, 1);

        assertTrue(machine.ok());
        assertEquals("green", machine.configuration()[0]);
    }

    @Test
    public void shouldSetAValidFixedConfiguration() {
        String[] expected = {"blue", "red", "green"};

        machine.spin(expected);

        assertTrue(machine.ok());
        assertArrayEquals(expected, machine.configuration());
    }

    @Test
    public void shouldNotAcceptAConfigurationWithWrongLength() {
        String[] before = machine.configuration();

        machine.spin(new String[] {"red"});

        assertFalse(machine.ok());
        assertArrayEquals(before, machine.configuration());
    }

    @Test
    public void shouldNotAcceptAConfigurationWithUnknownSymbol() {
        String[] before = machine.configuration();

        machine.spin(new String[] {"red", "purple", "blue"});

        assertFalse(machine.ok());
        assertArrayEquals(before, machine.configuration());
    }

    // JACKPOT Y RUEDAS BLOQUEADAS

    @Test
    public void shouldRecognizeAJackpotWhenUnlockedWheelsMatch() {
        machine.spin(new String[] {"red", "red", "red"});

        assertTrue(machine.isjackpot());
    }

    @Test
    public void shouldNotRecognizeAJackpotWhenUnlockedWheelsDiffer() {
        machine.spin(new String[] {"red", "green", "blue"});

        assertFalse(machine.isjackpot());
    }

    @Test
    public void shouldIgnoreLockedWheelsWhenCheckingJackpot() {
        machine.spin(new String[] {"red", "green", "blue"});

        machine.lock(2);
        machine.lock(3);

        assertTrue(machine.isjackpot());
    }

    @Test
    public void shouldNotRecognizeAJackpotWhenEveryWheelIsLocked() {
        machine.spin(new String[] {"red", "red", "red"});

        machine.lock(1);
        machine.lock(2);
        machine.lock(3);

        assertFalse(machine.isjackpot());
    }

    // COMPORTAMIENTO DE LEFTYWHEEL

    @Test
    public void shouldLeftyCopyItsImmediateLeftNeighbourAfterSpinning() {
        SlotMachine lefty = newMachine("normal", "lefty");

        addBasicSymbols(lefty, 1);
        addBasicSymbols(lefty, 2);

        lefty.placeSymbol(1, "blue");
        lefty.spin(2, 1);

        assertTrue(lefty.ok());
        assertEquals("blue", lefty.configuration()[0]);
        assertEquals("blue", lefty.configuration()[1]);
    }

    @Test
    public void shouldNotMakeALeftyWheelCopyTheWheelToItsRight() {
        SlotMachine lefty = newMachine("lefty", "normal");

        addBasicSymbols(lefty, 1);
        addBasicSymbols(lefty, 2);

        lefty.placeSymbol(1, "red");
        lefty.placeSymbol(2, "green");

        lefty.spin(2, 1);

        assertEquals("red", lefty.configuration()[0]);
        assertEquals("blue", lefty.configuration()[1]);
    }

    // COMPORTAMIENTO DE EPHEMERALSYMBOL

    @Test
    public void shouldDecreaseEphemeralSymbolSizeAfterASpin() {
        EphemeralSymbol symbol = new EphemeralSymbol("red");
        int initialWidth = symbol.getShapeWidth();

        symbol.onSpin();

        assertTrue(symbol.getShapeWidth() < initialWidth);
    }

    @Test
    public void shouldNotChangeNormalSymbolSizeWhenSpun() {
        Symbol symbol = new Symbol("red");

        int initialWidth = symbol.getShapeWidth();
        int initialHeight = symbol.getShapeHeight();

        symbol.onSpin();

        assertEquals(initialWidth, symbol.getShapeWidth());
        assertEquals(initialHeight, symbol.getShapeHeight());
    }

    @Test
    public void shouldNotDecreaseEphemeralSymbolBelowItsMinimumSize() {
        EphemeralSymbol symbol = new EphemeralSymbol("red");

        for (int i = 0; i < 30; i++) {
            symbol.onSpin();
        }

        int minimumWidth = symbol.getShapeWidth();

        symbol.onSpin();

        assertEquals(1, minimumWidth);
        assertEquals(minimumWidth, symbol.getShapeWidth());
    }

    // COMPORTAMIENTO DE SHYSYMBOL

    @Test
    public void shouldHideAShySymbolWhenItIsSelected() {
        ShySymbol symbol = new ShySymbol("red");

        assertTrue(symbol.isDisplayEnabled());

        symbol.onSelected();

        assertFalse(symbol.isDisplayEnabled());
    }

    @Test
    public void shouldRestoreAShySymbolAfterItsSecondSelection() {
        ShySymbol symbol = new ShySymbol("red");

        symbol.onSelected();
        symbol.onSelected();

        assertTrue(symbol.isDisplayEnabled());
    }

    @Test
    public void shouldNotShareVisibilityStateBetweenDifferentShySymbols() {
        ShySymbol first = new ShySymbol("red");
        ShySymbol second = new ShySymbol("blue");

        first.onSelected();

        assertFalse(first.isDisplayEnabled());
        assertTrue(second.isDisplayEnabled());
    }

    @Test
    public void shouldNotCountAHiddenShySymbolInTheJackpot() {
        SlotMachine shy = newMachine("normal", "normal");

        shy.addSymbol(1, "red", "shy");
        shy.addSymbol(2, "red", "shy");

        assertTrue(shy.isjackpot());

        shy.placeSymbol(2, "red");

        assertFalse(shy.isjackpot());
        assertEquals("", shy.configuration()[1]);
    }

    @Test
    public void shouldNotConfuseCanvasVisibilityWithShyDisplayState() {
        ShySymbol symbol = new ShySymbol("red");

        symbol.makeInvisible();

        assertFalse(symbol.isVisible());
        assertTrue(symbol.isDisplayEnabled());

        symbol.onSelected();

        assertFalse(symbol.isDisplayEnabled());
        assertFalse(symbol.isVisible());
    }

    // VISIBILIDAD BASICA DE SYMBOL

    @Test
    public void shouldMakeASymbolVisible() {
        Symbol symbol = new Symbol("red");

        symbol.makeVisible();

        assertTrue(symbol.isVisible());
    }

    @Test
    public void shouldMakeASymbolInvisible() {
        Symbol symbol = new Symbol("red");

        symbol.makeVisible();
        symbol.makeInvisible();

        assertFalse(symbol.isVisible());
    }
}
