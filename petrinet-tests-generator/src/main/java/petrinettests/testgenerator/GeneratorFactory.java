package petrinettests.testgenerator;

import javax.annotation.Nonnull;
import java.util.Map;

public interface GeneratorFactory {
  @Nonnull
  Map<String, TestGenerator> getAllGenerators();
}
