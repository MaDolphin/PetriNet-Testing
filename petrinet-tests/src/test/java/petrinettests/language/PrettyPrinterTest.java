package petrinettests.language;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import petrinettests._ast.ASTPetriNetTest;
import petrinettests.prettyprint.PetrinetTestPrettyprinter;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrettyPrinterTest extends BaseTest {

  public static Stream<Path> allModels() throws Exception {
    return Files.walk(modelPath).filter(path -> path.toFile().getName().endsWith(".pnt"));
  }

  @ParameterizedTest
  @MethodSource("allModels")
  public void prettyPrintEqualsOriginalModel(Path model) throws Exception {
    Optional<ASTPetriNetTest> pnt = parser.parse(model.toString());
    // maybe there is a test for unparsable-models, ignore them
    if (pnt.isEmpty()) {
      return;
    }
    String printResult = PetrinetTestPrettyprinter.print(pnt.get());
    Optional<ASTPetriNetTest> reparsed = parser.parse(new StringReader(printResult));
    try {
      assertTrue(reparsed.isPresent());
      assertTrue(reparsed.get().deepEquals(pnt.get()));
    }
    catch (AssertionError e) {
      Log.getFindings().forEach(System.err::println);
      printWithLineNumbersToErr(printResult);
      throw e;
    }
  }

  private void printWithLineNumbersToErr(String s) {
    String[] lines = s.split("\\R");
    int lineNumberFormatWidth = (int) Math.ceil(Math.log10(lines.length));
    String formatString = String.format("%%%dd: %%s\n", lineNumberFormatWidth); // -> result something like this "%3d: %s<newline>"
    for (int lineIdx = 0; lineIdx < lines.length; ++lineIdx) {
      System.err.printf(formatString, lineIdx + 1, lines[lineIdx]);
    }
  }
}
