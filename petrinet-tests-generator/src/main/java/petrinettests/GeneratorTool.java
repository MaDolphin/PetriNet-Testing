package petrinettests;

import jline.internal.Log;
import petrinet._ast.ASTPetrinet;
import petrinet._parser.PetrinetParser;
import petrinettests._ast.ASTPetriNetTest;
import petrinettests.prettyprint.PetrinetTestPrettyprinter;
import petrinettests.testgenerator.DefaultGeneratorFactory;
import petrinettests.testgenerator.GeneratorFactory;
import petrinettests.testgenerator.TestGenerator;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.OptionalLong;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GeneratorTool {
  @Nonnull
  private GeneratorFactory factory;
  @Nonnull
  private final ExecutorService executor = Executors.newWorkStealingPool();
  @Nonnull
  private OptionalLong timeout = OptionalLong.empty();
  @Nonnull
  private TimeUnit timeoutUnit = TimeUnit.SECONDS;


  public GeneratorTool(@Nonnull GeneratorFactory factory) {
    this.factory = factory;
  }

  public static void main(String... args) throws IOException {
    new GeneratorTool(new DefaultGeneratorFactory()).generateAll(new PetrinetParser().parse(args[0]).orElseThrow(), args[1]);
  }

  public void setTimeout(@Nonnull OptionalLong timeout, @Nonnull TimeUnit timeoutUnit) {
    this.timeout = timeout;
    this.timeoutUnit = timeoutUnit;
  }

  protected Writer requireOutput(@Nonnull String outputDir, @Nonnull ASTPetrinet petrinet, @Nonnull ASTPetriNetTest pnt, @Nonnull TestGenerator generator) throws IOException {
    return new BufferedWriter(new FileWriter(new File(outputDir, petrinet.getName() + "@" + generator.getName() + ".pnt")));
  }

  public void generateOnce(@Nonnull TestGenerator generator, @Nonnull ASTPetrinet petrinet, @Nonnull String outputDir){
    try{
      ASTPetriNetTest pnt = generator.generate(petrinet);
      try(Writer writer = requireOutput(outputDir, petrinet, pnt, generator)){
        writer.write(PetrinetTestPrettyprinter.print(pnt));
      }
    }catch (Exception e){
      //TODO LOG E
      Log.error(e);
    }
  }

  public void generateAll(@Nonnull ASTPetrinet petrinet, @Nonnull String outputDir){
    factory.getAllGenerators().values().forEach(generator -> executor.execute(()->generateOnce(generator, petrinet, outputDir)));
    executor.shutdown();

    try {
      boolean finished = false;
      while (!finished) {
        boolean finishedExecution = executor.awaitTermination(timeout.orElse(10), timeoutUnit);
        finished = finishedExecution || timeout.isPresent();
      }
    }catch (InterruptedException e){
      // log ?
    }
  }
}
