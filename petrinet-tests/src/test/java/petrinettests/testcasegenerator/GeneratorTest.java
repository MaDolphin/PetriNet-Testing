package petrinettests.testcasegenerator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import de.monticore.io.paths.ModelPath;
import org.junit.jupiter.api.Test;
import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;
import petrinet._cocos.PetrinetCoCoChecker;
import petrinet._parser.PetrinetParser;
import petrinet._symboltable.PetrinetGlobalScope;
import petrinet._symboltable.PetrinetLanguage;
import petrinet._symboltable.PetrinetSymbolTableCreatorDelegator;
import petrinet.cocos.PetrinetCoCos;
import petrinettests._ast.ASTPetriNetTest;
import petrinettests.prettyprint.PetrinetTestPrettyprinter;
import petrinettests.simulator.TransitionNotEnabledException;
import petrinettests.simulator.TransitionNotFoundException;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeneratorTest {

    @Test
    public void testLoadPetrinet() throws IOException, TransitionNotEnabledException, TransitionNotFoundException {

        Generator generator = new Generator();
        PetrinetParser parser = new PetrinetParser();
        String oFileName = "CookieMachine_modified";
        Optional<ASTPetrinet> oPetrinet = parser.parse("src/test/resources/testcasegenerator/"+oFileName+".pn");
        assertTrue(oPetrinet.isPresent());
        ASTPetriNetTest petriNetTest = generator.getAllTestcase(oPetrinet.get(),oFileName);
        String str = PetrinetTestPrettyprinter.print(petriNetTest);
        System.out.println(str);

        String fileName = petriNetTest.getName() + ".pnt";
        String dirName = "src/test/resources/testcasegenerator/" + fileName;

//        File dir = new File (dirName);
//        File actualFile = new File (dir, fileName);

        byte data[] = str.getBytes();
        Path p = Paths.get(dirName);
        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(p, CREATE, APPEND))) {
            out.write(data, 0, data.length);
        } catch (IOException x) {
            System.err.println(x);
        }

    }
}