package petrinettests.testgenerator;

import petrinet._ast.ASTPetrinet;
import petrinettests._ast.ASTPetriNetTest;

import javax.annotation.Nonnull;

public interface TestGenerator {
  @Nonnull String getName();
  @Nonnull ASTPetriNetTest generate(@Nonnull ASTPetrinet petrinet);
}
