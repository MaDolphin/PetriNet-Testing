package petrinettests.simulator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import petrinet._ast.ASTPetrinet;
import petrinet._parser.PetrinetParser;
import petrinettests.simulator.resolver.DynamicPetrinetResolver;
import petrinettests.simulator.resolver.LocalPtFileResolver;
import petrinettests.simulator.resolver.PetrinetResolverFactory;
import petrinettests.simulator.resolver.ThrowsOnResolve;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ResolverTest {
  @BeforeEach
  public void resetDelegate(){
    PetrinetResolverFactory.registerDelegate(null);
  }

  @Test
  public void loadsDynamicResolver(){
    assertEquals(PetrinetResolverFactory.DYNAMIC_RESOLVER_PACKAGE, DynamicPetrinetResolver.class.getPackageName());
    assertEquals(PetrinetResolverFactory.DYNAMIC_RESOLVER_CLASSNAME, DynamicPetrinetResolver.class.getSimpleName());
    DynamicPetrinetResolver.setThrowOnResolve(true);
    assertThrows(DynamicPetrinetResolver.MarkCallException.class, () -> PetrinetResolverFactory.getResolver().resolve(new PetrinetParser(), ""));
    DynamicPetrinetResolver.setThrowOnResolve(false);
  }

  @Test
  public void resolvesWithDelegate(){
    PetrinetResolverFactory.registerDelegate(new ThrowsOnResolve());
    assertThrows(DynamicPetrinetResolver.MarkCallException.class, () -> PetrinetResolverFactory.getResolver().resolve(new PetrinetParser(), ""));
  }

  @Test
  public void localResolverWorks() throws IOException {
    PetrinetResolverFactory.registerDelegate(new LocalPtFileResolver(Paths.get("src", "test", "resources", "simulator").toAbsolutePath()));
    Optional<ASTPetrinet> pn = PetrinetResolverFactory.getResolver().resolve(new PetrinetParser(), "CookieMachine_modified");
    assertTrue(pn.isPresent());
    assertEquals(pn.get().getName(), "CookieMachine");
  }
}
