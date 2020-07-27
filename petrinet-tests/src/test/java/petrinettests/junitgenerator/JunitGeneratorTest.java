package petrinettests.junitgenerator;

import org.junit.jupiter.api.Test;
import petrinettests._ast.ASTPetriNetTest;
import petrinettests._parser.PetrinetTestsParser;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JunitGeneratorTest {
  @Test
  public void testBasicSim() throws IOException {
    PetrinetTestsParser parser = new PetrinetTestsParser();
    Optional<ASTPetriNetTest> ast = parser.parse("src/test/resources/junitgenerator/test01.pnt");
    assertTrue(ast.isPresent());
    ASTPetriNetTest test = ast.get();

    JUnitGenerator.generateJUnit(test);
  }

  @Test
  public void testDefinedMarkingWithRest() throws IOException {
    PetrinetTestsParser parser = new PetrinetTestsParser();
    Optional<ASTPetriNetTest> ast = parser.parse("src/test/resources/junitgenerator/test02.pnt");
    assertTrue(ast.isPresent());
    ASTPetriNetTest test = ast.get();

    JUnitGenerator.generateJUnit(test);
  }

  @Test
  public void testExpectations() throws IOException {
    PetrinetTestsParser parser = new PetrinetTestsParser();
    Optional<ASTPetriNetTest> ast = parser.parse("src/test/resources/junitgenerator/test03.pnt");
    assertTrue(ast.isPresent());
    ASTPetriNetTest test = ast.get();

    JUnitGenerator.generateJUnit(test);
  }

  @Test
  public void testAutoGenerate() throws IOException {
    PetrinetTestsParser parser = new PetrinetTestsParser();
    Optional<ASTPetriNetTest> ast = parser.parse("src/test/resources/testcasegenerator/CookieMachine_AutoTest.pnt");
    assertTrue(ast.isPresent());
    ASTPetriNetTest test = ast.get();

    JUnitGenerator.generateJUnit(test);
  }

}