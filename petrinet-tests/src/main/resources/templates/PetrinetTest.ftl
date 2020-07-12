<#import "macros.ftl" as macros/>
package petrinettests.generated;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTTransition;
import petrinet._parser.PetrinetParser;
import petrinet.analysis.Marking;
import petrinet.analysis.TokenCount;
import petrinettests.simulator.Simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ${ast.name} {
<#assign import>${ast.import.name}</#assign>
    ASTPetrinet petrinet;
    Simulator sim;

    @BeforeEach
    public void setUp() throws IOException {
        PetrinetParser parser = new PetrinetParser();
        <#-- TODO Path / Find recursively in folder -->
        petrinet = parser.parse("/Users/stocc/git/sle/project/petrinet-testing/petrinet-tests-generator/src/main/resources/${import}.pn").orElseGet(() -> {
            fail("Model path not found");
            return null;
        });

        sim = new Simulator(petrinet);
    }

<#list ast.getAllTestcases() as testcase>
<#assign body=testcase.testcaseBody/>
    @Test
    public void test${testcase.name}() throws IOException {
<@macros.initial initialMarking=body.getInitialMarking()/>

    <#list body.expectationList as e>
<@macros.expectation expectation=e/>
    </#list>

    <#list body.testStepList as step>
        <#list step.simulationList as simStep>
<@macros.simulation simulation=simStep/>            
        </#list>
        <#list step.expectationList as expStep>
<@macros.expectation expectation=expStep/>            
        </#list>
    </#list>
    }
</#list>

    // HELPERS

    private void performTransition(String transitionName) {
        assertDoesNotThrow(() -> sim.simulateTransition(transitionName));
    }

    private boolean any(boolean... conditions) {
        List<Boolean> conditionsList = new ArrayList<>();
        for (boolean c: conditions) {
            conditionsList.add(c);
        }

        return conditionsList.stream().anyMatch(b -> b);
        
    }

    private boolean all(boolean... conditions) {
        List<Boolean> conditionsList = new ArrayList<>();
        for (boolean c: conditions) {
            conditionsList.add(c);
        }

        return conditionsList.stream().allMatch(b -> b);
    }

    private boolean checkMarking(String placeName, int tokenCount) {
        return 0 == sim.getCurrentMarking().get(placeName).compareTo(tokenCount);
    }

    private boolean checkEnabled(String transitionName) {
        return sim.getCurrentMarking().enabled(findTransitionByName(transitionName));   
    }

    private ASTTransition findTransitionByName(String name) {
        return petrinet.getTransitionList().stream().filter(t -> t.getName().equals(name)).findAny().orElseThrow();
    }

    private void setTokens(String placeName, int tokenCount) {
        Marking initialMarking = sim.getCurrentMarking();
        TokenCount count = new TokenCount(tokenCount);
        initialMarking.set(placeName, count);
        sim.setCurrentMarking(initialMarking);
    }

    private void clearAllTokens() {
        Marking initialMarking = sim.getCurrentMarking();
        for (String key : initialMarking.keys()) {
            TokenCount count = new TokenCount(0);
            initialMarking.set(key, count);
        }
        sim.setCurrentMarking(initialMarking);
    }

    private void applyRest(int tokenCount) {
        Marking initialMarking = sim.getCurrentMarking();
        for (String key : initialMarking.keys()) {
            TokenCount count = initialMarking.get(key);
            if (count.compareTo(0) > 0) {
                continue;
            }

            count.add(tokenCount);
            initialMarking.set(key, count);
        }
        sim.setCurrentMarking(initialMarking);
    }
}
