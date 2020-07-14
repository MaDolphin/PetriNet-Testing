package petrinettests.simulator.resolver;

import petrinet._ast.ASTPetrinet;
import petrinet._parser.PetrinetParser;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DynamicPetrinetResolver extends ThrowsOnResolve{

  private static boolean throwOnResolve = false;

  public static void setThrowOnResolve(boolean throwOnResolve) {
    DynamicPetrinetResolver.throwOnResolve = throwOnResolve;
  }

  @Nonnull
  @Override
  public Optional<ASTPetrinet> resolve(@Nonnull PetrinetParser parser, @Nonnull String petrinetName) throws IOException {
    if(throwOnResolve){
      return super.resolve(parser, petrinetName);
    }
    // else check resource directories
    String expectedFileName = petrinetName + ".pn";
    List<File> files = Files.walk(Paths.get("src", "test", "resources")).map(Path::toFile).filter(file -> file.getName().equals(expectedFileName)).collect(Collectors.toList());
    if(files.size() == 0){
      // try the other module
      files = Files.walk(Paths.get("..", "petrinet-tests-generator", "src", "test", "resources")).map(Path::toFile).filter(file -> file.getName().equals(expectedFileName)).collect(Collectors.toList());
    }
    if(files.size() == 0){
      throw new NoSuchFileException(expectedFileName);
    }else if(files.size() > 1){
      throw new RuntimeException("Multiple files named " + expectedFileName);
    }else {
      return parser.parse(files.get(0).getAbsolutePath());
    }
  }
}
