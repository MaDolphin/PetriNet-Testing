package petrinettests._ast;

import java.util.Optional;

import petrinet._ast.ASTPetrinet;
import petrinet.analysis.Marking;

public class ASTDisjunction extends ASTDisjunctionTOP {

    /**
     * Verifies if any contained condition holds
     * 
     * @param petrinet The Petri Net considered for verification
     * @param marking The marking considered for verification
     * @return true, if the condition holds. null, if the evaluation caused an error
     */
    @Override
    public Optional<Boolean> verify(ASTPetrinet petrinet, Marking marking) {
        try {
            final boolean result = getConditionList().stream().anyMatch(condition -> {
                return condition.verify(petrinet, marking).orElseThrow();
            });
            return Optional.of(result);
        
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}