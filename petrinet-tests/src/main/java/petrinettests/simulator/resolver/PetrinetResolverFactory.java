package petrinettests.simulator.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.Optional;

public class PetrinetResolverFactory {
  public static final String DYNAMIC_RESOLVER_PACKAGE = "petrinettests.simulator.resolver";
  public static final String DYNAMIC_RESOLVER_CLASSNAME = "DynamicPetrinetResolver";

  private static PetrinetResolver delegate;

  public static void registerDelegate(@Nullable PetrinetResolver resolverDelegate){
    delegate = resolverDelegate;
  }

  @Nonnull
  public static PetrinetResolver getResolver(){
    if(delegate != null){
      return delegate;
    }
    Optional<PetrinetResolver> dynamicLoader = getDynamicLinkedResolver();
    //noinspection OptionalIsPresent
    if(dynamicLoader.isPresent()){
      return dynamicLoader.get();
    }
    // default
    return new LocalPtFileResolver();
  }

  @Nonnull
  private static Optional<PetrinetResolver> getDynamicLinkedResolver(){
    Logger logger = LoggerFactory.getLogger(PetrinetResolverFactory.class.getName());
    try{
      Class<?> genericLoaderClass = ClassLoader.getSystemClassLoader().loadClass(DYNAMIC_RESOLVER_PACKAGE + "." + DYNAMIC_RESOLVER_CLASSNAME);
      if(!PetrinetResolver.class.isAssignableFrom(genericLoaderClass)){
        throw new IllegalStateException("Dynamic loader is not a " + PetrinetResolver.class.getName());  //will be caught and logged
      }
      @SuppressWarnings("unchecked")
      Class<PetrinetResolver> loaderClass = (Class<PetrinetResolver>)genericLoaderClass;
      Constructor<PetrinetResolver> defaultCtor = loaderClass.getConstructor();  //may fails -> throws exception
      PetrinetResolver loader = defaultCtor.newInstance();  //may fails -> throws exception
      logger.info("Dynamic resolver loaded ('" + loaderClass.getSimpleName() + "').");
      return Optional.of(loader);
    }catch (ClassNotFoundException e){
      // Not finding the class is not an error
      logger.info("No dynamic resolver loaded.");
      return Optional.empty();
    } catch (Exception e){
      logger.error("Could not load dynamic resolver.", e);
      return Optional.empty();
    }
  }
}
