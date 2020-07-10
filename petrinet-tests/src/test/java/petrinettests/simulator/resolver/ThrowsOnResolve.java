package petrinettests.simulator.resolver;

import petrinet._ast.ASTPetrinet;
import petrinet._parser.PetrinetParser;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Optional;

public class ThrowsOnResolve implements PetrinetResolver{
  @Nonnull
  @Override
  public Optional<ASTPetrinet> resolve(@Nonnull PetrinetParser parser, @Nonnull String petrinetName) throws IOException {
    throw new MarkCallException();
  }

  public static class MarkCallException extends RuntimeException {}
}
