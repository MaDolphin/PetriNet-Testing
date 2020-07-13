package petrinettests.testcasegenerator;

import java.io.IOException;
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
import petrinettests.simulator.TransitionNotEnabledException;
import petrinettests.simulator.TransitionNotFoundException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeneratorTest {

    @Test
    public void testLoadPetrinet() throws IOException, TransitionNotEnabledException, TransitionNotFoundException {

        Generator generator = new Generator();
        PetrinetParser parser = new PetrinetParser();

        Optional<ASTPetrinet> oPetrinet = parser.parse("src/test/resources/testcasegenerator/generator/CookieMachine_modified.pn");

        assertTrue(oPetrinet.isPresent());

        generator.getAllTestcase(oPetrinet.get());

    }
}