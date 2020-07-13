package petrinettests.testgenerator;

import petrinettests.generators.defaultgenerators.CauseEffectGenerator;
import petrinettests.generators.defaultgenerators.OneForEachTransition;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultGeneratorFactory implements GeneratorFactory {
  @Nonnull
  @Override
  public Map<String, TestGenerator> getAllGenerators() {
    return Stream.of(
        new OneForEachTransition(),
        new CauseEffectGenerator())
        .collect(Collectors.toMap(TestGenerator::getName, Function.identity()));
  }
}
