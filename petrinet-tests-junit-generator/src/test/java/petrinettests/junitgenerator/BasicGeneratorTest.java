package petrinettests.junitgenerator;


import org.junit.jupiter.api.Test;

import petrinettests._ast.ASTPetriNetTest;
import petrinettests._parser.PetrinetTestsParser;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BasicGeneratorTest {
    @Test
    public void testBasicSim() throws IOException {
        PetrinetTestsParser parser = new PetrinetTestsParser();
        Optional<ASTPetriNetTest> ast = parser.parse("src/test/resources/test01.pnt");
        assertTrue(ast.isPresent());
        ASTPetriNetTest test = ast.get();

        JUnitGenerator.generateJUnit(test);
    }


    @Test
    public void testDefinedMarkingWithRest() throws IOException {
        PetrinetTestsParser parser = new PetrinetTestsParser();
        Optional<ASTPetriNetTest> ast = parser.parse("src/test/resources/test02.pnt");
        assertTrue(ast.isPresent());
        ASTPetriNetTest test = ast.get();

        JUnitGenerator.generateJUnit(test);
    }



}