package petrinettests.junitgenerator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import petrinettests._ast.ASTPetriNetTest;
import petrinettests._parser.PetrinetTestsParser;

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
        GeneratorSetup setup = new GeneratorSetup();
        setup.setCommentStart("//");
        setup.setCommentEnd("");
    
        GeneratorEngine engine = new GeneratorEngine(setup);
        engine.generate("templates/PetrinetTest.ftl", Paths.get(ast.getName() + ".java"), ast);
    
      }
}