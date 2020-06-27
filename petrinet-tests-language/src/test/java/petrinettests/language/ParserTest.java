package petrinettests.language;

import org.junit.jupiter.api.Test;
import petrinettests._ast.ASTPetriNetTest;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest extends BaseTest{
    @Test
    public void testHelloWorld() {
        System.out.println("Hello, I'm running tests in maven");
        assertTrue(true);
    }

    @Test
    public void testBasicParse() throws IOException {
        Optional<ASTPetriNetTest> ast = parser.parse(modelPath.resolve("test01.pnt").toString());
        assertTrue(ast.isPresent());
    }
}