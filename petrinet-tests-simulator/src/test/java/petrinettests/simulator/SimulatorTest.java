package petrinettests.simulator;

import org.junit.jupiter.api.Test;

import petrinet._ast.ASTPetrinet;
import petrinet._parser.PetrinetParser;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimulatorTest {
    @Test
    public void testBasicSim() throws IOException {
        PetrinetParser parser = new PetrinetParser();
        Optional<ASTPetrinet> ast = parser.parse("src/test/resources/CookieMachine_modified.pn");
        assertTrue(ast.isPresent());
        ASTPetrinet petrinet = ast.get();

        Simulator sim = new Simulator(petrinet);


        assertEquals(0, sim.getCurrentMarking().get("Keksspeicher").compareTo(4));
        assertEquals(0, sim.getCurrentMarking().get("Einwurfschlitz").compareTo(0));

        assertDoesNotThrow(() -> sim.simulateTransition("MuenzeEinwerfen"));
        
        assertEquals(0, sim.getCurrentMarking().get("Einwurfschlitz").compareTo(1));

        assertDoesNotThrow(() -> sim.simulateTransition("MuenzeAkzeptieren"));

        assertEquals(0, sim.getCurrentMarking().get("Signal").compareTo(1));

        assertDoesNotThrow(() -> sim.simulateTransition("KeksDruecken"));
        assertEquals(0, sim.getCurrentMarking().get("KeksZaehler").compareTo(1));

        assertDoesNotThrow(() -> sim.simulateTransition("KekseAusgeben"));
        assertDoesNotThrow(() -> sim.simulateTransition("KeksGeben"));
        assertDoesNotThrow(() -> sim.simulateTransition("SchachtelEntnehmen"));

        assertEquals(0, sim.getCurrentMarking().get("Keksspeicher").compareTo(3));
        assertEquals(0, sim.getCurrentMarking().get("Einwurfschlitz").compareTo(0));
    }

    @Test
    public void testTransitionNotEnabled() throws IOException {
        PetrinetParser parser = new PetrinetParser();
        Optional<ASTPetrinet> ast = parser.parse("src/test/resources/CookieMachine_modified.pn");
        assertTrue(ast.isPresent());
        ASTPetrinet petrinet = ast.get();

        Simulator sim = new Simulator(petrinet);

        // Try to steal a cookie
        assertThrows(TransitionNotEnabledException.class, () -> sim.simulateTransition("KeksGeben"));
    }
}