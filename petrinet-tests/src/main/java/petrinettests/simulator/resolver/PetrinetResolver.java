package petrinettests.simulator.resolver;

import petrinet._ast.ASTPetrinet;
import petrinet._parser.PetrinetParser;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Optional;

public interface PetrinetResolver {
  @Nonnull
  Optional<ASTPetrinet> resolve(@Nonnull PetrinetParser parser, @Nonnull String petrinetName) throws IOException;
}
