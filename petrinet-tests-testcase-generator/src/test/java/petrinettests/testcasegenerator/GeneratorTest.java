package petrinettests.testcasegenerator;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import petrinet._ast.ASTPetrinet;
import petrinet._parser.PetrinetParser;

public class GeneratorTest {

    @Test
    public void testLoadPetrinet() throws IOException {
        PetrinetParser parser = new PetrinetParser();

        Optional<ASTPetrinet> oPetrinet = parser.parse("src/test/resources/ExampleNet.pn");

        assertTrue(oPetrinet.isPresent());
        System.out.print(oPetrinet.get().getName());
    }
}