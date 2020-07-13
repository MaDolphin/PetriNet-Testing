package petrinettests._ast;

import java.util.Optional;

import petrinet._ast.ASTPetrinet;
import petrinet.analysis.Marking;

public class ASTEnabledCondition extends ASTEnabledConditionTOP {
    
    /**
     * Verifies if all given transitions are enabled in the Petrinet given the marking
     * 
     * @param petrinet The Petri Net considered for verification
     * @param marking The marking considered for verification
     * @return true, if the condition holds. null, if the evaluation caused an error
     */
    @Override
    public Optional<Boolean> verify(ASTPetrinet petrinet, Marking marking) {
        
        final boolean result = petrinet.getTransitionList().stream().allMatch(transition -> {
            if (getNameList().contains(transition.getName())) {
                return marking.enabled(transition);
            }
            return true;
        });
        return Optional.of(result);
    }
}