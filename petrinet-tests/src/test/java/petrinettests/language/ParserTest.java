package petrinettests.language;

import org.junit.jupiter.api.Test;
import petrinettests._ast.ASTPetriNetTest;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest extends BaseTest {
  @Test
  public void testBasicParse() throws IOException {
    Optional<ASTPetriNetTest> ast = parser.parse(modelPath.resolve("parser/test01.pnt").toString());
    assertTrue(ast.isPresent());
  }
}