package petrinettests.junitgenerator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import petrinettests._ast.ASTPetriNetTest;
import petrinettests._parser.PetrinetTestsParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class JUnitGenerator {

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Specify a file");
      System.exit(1);
    }
    String file = args[0];

    PetrinetTestsParser parser = new PetrinetTestsParser();
    Optional<ASTPetriNetTest> result = parser.parse(file);

    generateJUnit(result.get());

  }

  public static void generateJUnit(ASTPetriNetTest ast) {
    generateJUnit(ast, "out");
  }

  public static void generateJUnit(ASTPetriNetTest ast, String outputPath) {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setCommentStart("//");
    setup.setCommentEnd("");
    setup.setOutputDirectory(new File(outputPath));

    GeneratorEngine engine = new GeneratorEngine(setup);
    engine.generate("templates/PetrinetTest.ftl", Paths.get(ast.getName() + ".java"), ast);

  }
}