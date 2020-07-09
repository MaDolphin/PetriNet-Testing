package petrinettests;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import de.se_rwth.commons.logging.Log;
import petrinettests._ast.ASTPetriNetTest;
import petrinettests._parser.PetrinetTestsParser;
import petrinettests.junitgenerator.JUnitGenerator;

public class PetrinetTestsTool {
  public static void main(String[] args) {
    // First, determine the log level (debug on or off).
    Level logLevel = Level.DEBUG;
    if (Arrays.asList(args).contains("--no-debug")) {
        if (Arrays.asList(args).contains("--debug")) {
            Log.error("--no-debug and --debug in conflict");
        }
        logLevel = Level.INFO;
    } 
    
    ((Logger) LoggerFactory.getLogger(PetrinetTestsTool.class.getName())).setLevel(logLevel);

    if (args.length < 1) {
        Log.error("Please specify exactly one input model, or --help.");
        return;
    }

    if (args[0].equals("--help")) {
        System.out.println("java -jar petrinets.jar <model> <command>*");
        // System.out.println("    <model>: path to petrinet input file (.pn)");
        // System.out.println("    <command>: argument, or test to run");
        // System.out.println("        arguments:");
        // System.out.println("            --debug (default), --no-debug");
        // System.out.println("        implemented functions (order is important):");
        // System.out.println("            --simplify (should come before others, e.g. --pretty)");
        // System.out.println("            --pretty (implies --no-debug)");
        // System.out.println("            --dot (implies --no-debug)");
        // System.out.println("            --safe, --unsafe");
        // System.out.println("            --bounded, --unbounded");
        // System.out.println("            --l0-live, --l1-live < * | t_1,...,t_n >");
        // System.out.println("            --type");
        System.exit(0);
    }

    // ASTPetrinet ast = parse(args[0]);
    // if (ast == null) {
    //     Log.error("0xP0000 Failed to parse petrinet");
    //     return;
    // }

    // Log.debug(args[0] + " parsed successfully!", PetrinetsTool.class.getName());

    // final PetrinetLanguage lang = new PetrinetLanguage();
    // PetrinetArtifactScope modelTopScope = createSymbolTable(lang, ast);

    // PetrinetCoCoChecker checker = PetrinetCoCos.getCheckerForAllCoCos();
    // checker.checkAll(ast);
    // if (!Log.getFindings().isEmpty()) {
    //     Log.error("0xP0001 CoCo verification failed, aborting");
    // } else {
    //     Log.debug(args[0] + " cocos verified successfully!", PetrinetsTool.class.getName());
    // }

    for (int i = 0; i < args.length; i++) {
        switch (args[i]) {
            case "--debug":
            case "--no-debug":
                // ignore logging commands
                break;
            case "--generate-dir":
                Log.debug(args[i] + ": [Generate Dir] Generating tests for Petrinets in model path", PetrinetTestsTool.class.getName());
                if (i >= args.length - 2) {
                  Log.error("Two arguments required to " + args[i]);
                  break;
              }

                String modelPath = args[i+1];
                String outputPath = args[i+2];


                generateTests(modelPath, outputPath);

                return;
            default:
                Log.error(args[i] + ": function is not implemented.");
                break;
        }
    }
  }


  /**
   * Generates tests for all models in modelPath (recursively) and writes to outputPath
   * @param modelPath
   * @param outputPath
   */
  static private void generateTests(String modelPath, String outputPath) {
    String[] extensions = {"pnt"};
    Collection<File> modelFiles = FileUtils.listFiles(new File(modelPath), extensions, true);

    Log.debug("[Generate Dir] Generating tests for Petrinets in model path " + new File(modelPath).getAbsolutePath(), PetrinetTestsTool.class.getName());

    for (File file : modelFiles) {
      Log.debug("Generating JUnit Tests from " + file.toString() , PetrinetTestsTool.class.getName());
      ASTPetriNetTest ast = parse(file.getAbsolutePath());
      JUnitGenerator.generateJUnit(ast, outputPath);
    }
    
  }


  private static ASTPetriNetTest parse(String model) {
    try {
        PetrinetTestsParser parser = new PetrinetTestsParser();
        Optional<ASTPetriNetTest> optPetrinetTest = parser.parse(model);

        if (!parser.hasErrors() && optPetrinetTest.isPresent()) {
            return optPetrinetTest.get();
        }
        Log.error("Model could not be parsed.");
    } catch (IOException e) {
        Log.error("Failed to parse " + model, e);
    }
    return null;
}
}
