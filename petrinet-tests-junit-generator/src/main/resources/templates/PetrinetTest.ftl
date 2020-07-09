<#import "macros.ftl" as macros/>
package petrinettests.simulator; //TODO: package?

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import petrinet._ast.ASTPetrinet;
import petrinet._parser.PetrinetParser;
import petrinet.analysis.Marking;
import petrinet.analysis.TokenCount;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ${ast.name} {
<#assign import>${ast.import.name}</#assign>
    ASTPetrinet petrinet;
    Simulator sim;

    @BeforeEach
    public void prepareSimulator() throws IOException {
        PetrinetParser parser = new PetrinetParser();
        <#-- TODO Path / Find recursively in folder -->
        petrinet = parser.parse("src/test/resources/${import}.pn").orElseGet(() -> {
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

    private void assertMarking(String placeName, int tokenCount) {
        assertEquals(0, sim.getCurrentMarking().get(placeName).compareTo(tokenCount));
    }

    private void setTokens(String placeName, int tokenCount) {
        Marking initialMarking = sim.getCurrentMarking();
        TokenCount count = new TokenCount(tokenCount);
        initialMarking.set(placeName, count);
        sim.setCurrentMarking(initialMarking);
    }

    private void clearAllTokens() {
        applyRest(0);
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
