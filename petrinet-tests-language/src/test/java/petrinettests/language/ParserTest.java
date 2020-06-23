package petrinettests.language;

import org.junit.Assert;
import org.junit.Test;
import petrinettests._ast.ASTPetriNetTest;
import petrinettests._parser.PetrinetTestsParser;

import java.io.IOException;
import java.util.Optional;

public class ParserTest {
    @Test
    public void testHelloWorld() {
        System.out.println("Hello, I'm running tests in maven");
        Assert.assertTrue(true);
    }

    @Test
    public void testBasicParse() throws IOException {
        PetrinetTestsParser parser = new PetrinetTestsParser();

        Optional<ASTPetriNetTest> ast = parser.parse("src/test/resources/test01.pnt");
        Assert.assertTrue(ast.isPresent());
    }
}