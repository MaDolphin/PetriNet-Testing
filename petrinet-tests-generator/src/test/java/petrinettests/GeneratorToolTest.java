package petrinettests;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import petrinet._ast.ASTPetrinet;
import petrinet._parser.PetrinetParser;
import petrinettests._ast.ASTPetriNetTest;
import petrinettests._parser.PetrinetTestsParser;
import petrinettests.generators.defaultgenerators.OneForEachTransition;
import petrinettests.testgenerator.GeneratorFactory;
import petrinettests.testgenerator.TestGenerator;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeneratorToolTest {
  @BeforeAll
  public static void initLogging() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void TestOneForAllTransition() throws IOException {
    Writer writer = new StringWriter();

    String testOutputDir = "generated";

    GeneratorTool tool = new GeneratorTool(new GeneratorFactory() {
      @Nonnull
      @Override
      public Map<String, TestGenerator> getAllGenerators() {
        HashMap<String, TestGenerator> rtn = new HashMap<>();
        TestGenerator oneForAll = new OneForEachTransition();
        rtn.put(oneForAll.getName(), oneForAll);
        return rtn;
      }
    }){
      // Override the output of the generator tool to circumvent file-generation
      @Override
      protected Writer requireOutput(@Nonnull String outputDir, @Nonnull ASTPetrinet petrinet, @Nonnull ASTPetriNetTest pnt, @Nonnull TestGenerator generator)
          throws IOException {
        assertEquals(testOutputDir, outputDir);
        return writer;
      }
    };

    Path resources = Paths.get("src", "test", "resources");
    ASTPetrinet petrinet = new PetrinetParser().parse(resources.resolve("OneForEachTransitionTest.pn").toFile().getAbsolutePath()).get();
    ASTPetriNetTest expectedOutput = new PetrinetTestsParser().parse(resources.resolve("OneForEachTransitionTest.pnt").toFile().getAbsolutePath()).get();

    tool.generateAll(petrinet, testOutputDir);

    ASTPetriNetTest generatedOutput = new PetrinetTestsParser().parse_String(writer.toString()).get();

    assertTrue(expectedOutput.deepEquals(generatedOutput));
  }
}
