package petrinettests;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import de.se_rwth.commons.logging.Log;
import petrinet._ast.ASTPetrinet;
import petrinet._parser.PetrinetParser;
import petrinettests._ast.ASTPetriNetTest;
import petrinettests._parser.PetrinetTestsParser;
import petrinettests.junitgenerator.JUnitGenerator;
import petrinettests.simulator.TransitionNotEnabledException;
import petrinettests.simulator.TransitionNotFoundException;
import petrinettests.testcasegenerator.TestcaseGenerator;

public class PetrinetTestsTool {
    public static void main(String[] args) throws TransitionNotEnabledException, TransitionNotFoundException {
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
            System.out.println("java -jar petrinet-tests.jar <command>");
            System.out.println("    <command>: argument, or test to run");
            System.out.println("        arguments:");
            System.out.println("            --debug (default), --no-debug");
            System.out.println("        implemented commands (order is important):");
            System.out.println(
                    "            --generate-handwritten <model path> <output path> Generate a Junit Test from a single pnt model");
            System.out.println(
                    "            --generate-handwritten-dir <model path> <output path> Generates tests for all models in modelPath (recursively) and writes to outputPath");
            System.out.println(
                    "            --generate <model path> <output path> Generate a Junit Test from a single Petrinet model");
            System.out.println(
                    "            --generate-dir <model path> <output path> Generates tests for all models in modelPath (recursively) and writes to outputPath");
            System.exit(0);
        }

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--debug":
                case "--no-debug":
                    // ignore logging commands
                    break;
                case "--generate-handwritten":
                    Log.debug(args[i] + ": [Generate Handwritten] Generating tests for hand-written Petrinet test",
                            PetrinetTestsTool.class.getName());
                    if (i >= args.length - 2) {
                        Log.error("Two arguments required to " + args[i]);
                        break;
                    }

                    String handwrittenModelFilePath = args[i + 1];
                    String handwrittenOutputFilePath = args[i + 2];

                    generateJunitTest(handwrittenModelFilePath, handwrittenOutputFilePath);
                    return;

                case "--generate-handwritten-dir":
                    Log.debug(args[i]
                            + ": [Generate Handwritten] Generating tests for hand-written Petrinet tests in model path",
                            PetrinetTestsTool.class.getName());
                    if (i >= args.length - 2) {
                        Log.error("Two arguments required to " + args[i]);
                        break;
                    }

                    String handwrittenDirModelPath = args[i + 1];
                    String handwrittenDirOutputPath = args[i + 2];

                    generateJunitTests(handwrittenDirModelPath, handwrittenDirOutputPath);

                    return;

                case "--generate":
                    Log.debug(args[i] + ": [Generate Tests] Generating tests for Petrinet model",
                            PetrinetTestsTool.class.getName());
                    if (i >= args.length - 2) {
                        Log.error("Two arguments required to " + args[i]);
                        break;
                    }

                    String petrinetFilePath = args[i + 1];
                    String outputFilePath = args[i + 2];

                    generateTest(petrinetFilePath, outputFilePath);
                    return;

                case "--generate-dir":
                    Log.debug(args[i]
                            + ": [Generate Tests] Generating tests for hand-written Petrinet tests in model path",
                            PetrinetTestsTool.class.getName());
                    if (i >= args.length - 2) {
                        Log.error("Two arguments required to " + args[i]);
                        break;
                    }

                    String modelPath = args[i + 1];
                    String outputPath = args[i + 2];

                    generateTests(modelPath, outputPath);

                    return;
                default:
                    Log.error(args[i] + ": function is not implemented.");
                    break;
            }
        }
    }

    /**
     * Generates test for the model in modelPath and writes to outputPath
     * 
     * @param modelPath
     * @param outputPath
     */
    static private void generateJunitTest(String modelPath, String outputPath) {

        Log.debug(
                "[Generate Handwritten] Generating tests for handwritten test " + new File(modelPath).getAbsolutePath(),
                PetrinetTestsTool.class.getName());

        Log.debug("Generating JUnit Tests from " + modelPath, PetrinetTestsTool.class.getName());
        ASTPetriNetTest ast = parsePetrinetTest(modelPath);
        JUnitGenerator.generateJUnit(ast, outputPath);

    }

    /**
     * Generates tests for all models of Petrinet Tests in modelPath (recursively)
     * and writes to outputPath
     * 
     * @param modelPath
     * @param outputPath
     */
    static private void generateJunitTests(String modelPath, String outputPath) {
        String[] extensions = { "pnt" };
        Collection<File> modelFiles = FileUtils.listFiles(new File(modelPath), extensions, true);

        Log.debug("[Generate Handwritten] Generating tests for handwritten tests in model path "
                + new File(modelPath).getAbsolutePath(), PetrinetTestsTool.class.getName());

        for (File file : modelFiles) {
            Log.debug("Generating JUnit Tests from " + file.toString(), PetrinetTestsTool.class.getName());
            ASTPetriNetTest ast = parsePetrinetTest(file.getAbsolutePath());
            JUnitGenerator.generateJUnit(ast, outputPath);
        }

    }

    /**
     * Generates test for the Petrinet model in modelPath and writes to outputPath
     * 
     * @param modelPath
     * @param outputPath
     * @throws TransitionNotFoundException
     * @throws TransitionNotEnabledException
     */
    static private void generateTest(String modelPath, String outputPath)
            throws TransitionNotEnabledException, TransitionNotFoundException {

        Log.debug("[Generate Tests] Generating tests for Petrinet " + new File(modelPath).getAbsolutePath(),
                PetrinetTestsTool.class.getName());

        Log.debug("Generating JUnit Tests from " + modelPath, PetrinetTestsTool.class.getName());
        ASTPetrinet ast = parsePetrinet(modelPath);

        TestcaseGenerator testGenerator = new TestcaseGenerator();
        ASTPetriNetTest tests = testGenerator.getAllTestcases(ast, FilenameUtils.getBaseName(modelPath));
        
        JUnitGenerator.generateJUnit(tests, outputPath);

    
  }

  /**
   * Generates tests for all Petrinet models in modelPath (recursively) and writes
   * to outputPath
   * 
   * @param modelPath
   * @param outputPath
   * @throws TransitionNotFoundException
   * @throws TransitionNotEnabledException
   */
  static private void generateTests(String modelPath, String outputPath)
          throws TransitionNotEnabledException, TransitionNotFoundException {
    String[] extensions = {"pn"};
    Collection<File> modelFiles = FileUtils.listFiles(new File(modelPath), extensions, true);

    Log.debug("[Generate Tests] Generating tests for Petrinets in model path " + new File(modelPath).getAbsolutePath(), PetrinetTestsTool.class.getName());

    for (File file : modelFiles) {
      Log.debug("Generating JUnit Tests from " + file.toString() , PetrinetTestsTool.class.getName());
      generateTest(file.getPath(), outputPath);    
    }
    
  }

  private static ASTPetrinet parsePetrinet(String model) {
    try {
        PetrinetParser parser = new PetrinetParser();
        Optional<ASTPetrinet> optPetrinet = parser.parse(model);

        if (!parser.hasErrors() && optPetrinet.isPresent()) {
            return optPetrinet.get();
        }
        Log.error("Model could not be parsed.");
    } catch (IOException e) {
        Log.error("Failed to parse " + model, e);
    }
    return null;
  }

  private static ASTPetriNetTest parsePetrinetTest(String model) {
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
