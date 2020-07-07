package petrinettests.language;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import petrinettests._parser.PetrinetTestsParser;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseTest {
  protected static final Path modelPath = Paths.get("src/test/resources/");
  protected PetrinetTestsParser parser = new PetrinetTestsParser();

  @BeforeAll
  public static void init() {
    // replacing log by a side effect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
}
