package petrinettests.simulator.resolver;

import petrinet._ast.ASTPetrinet;
import petrinet._parser.PetrinetParser;
import petrinet._symboltable.PetrinetLanguage;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class LocalPtFileResolver implements PetrinetResolver {
  @Nonnull private final Path lookupDirectory;
  @Nonnull private final String fileExtension = "." + new PetrinetLanguage().getFileExtension();

  public LocalPtFileResolver() {
    this(Paths.get("."));
  }

  public LocalPtFileResolver(@Nonnull Path lookupDirectory) {
    this.lookupDirectory = lookupDirectory;
  }

  @Nonnull
  @Override
  public Optional<ASTPetrinet> resolve(@Nonnull PetrinetParser parser, @Nonnull String petrinetName) throws IOException {
      return parser.parse(lookupDirectory.resolve(petrinetName + fileExtension).toString());
  }
}
