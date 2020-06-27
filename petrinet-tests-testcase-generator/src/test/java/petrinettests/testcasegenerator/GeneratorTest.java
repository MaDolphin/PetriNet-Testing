package petrinettests.testcasegenerator;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import petrinet._ast.ASTPetrinet;
import petrinet._parser.PetrinetParser;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeneratorTest {

    @Test
    public void testLoadPetrinet() throws IOException {
        PetrinetParser parser = new PetrinetParser();

        Optional<ASTPetrinet> oPetrinet = parser.parse("src/test/resources/ExampleNet.pn");

        assertTrue(oPetrinet.isPresent());
        System.out.print(oPetrinet.get().getName());
    }
}