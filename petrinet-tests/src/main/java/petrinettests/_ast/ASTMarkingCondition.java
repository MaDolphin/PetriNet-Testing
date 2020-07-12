package petrinettests._ast;

import java.util.Optional;

import petrinet._ast.ASTPetrinet;
import petrinet.analysis.Marking;

public class ASTMarkingCondition extends ASTMarkingConditionTOP {
  /**
   * Verifies if the condition holds
   * 
   * @param petrinet The Petri Net considered for verification
   * @param marking The marking considered for verification
   * @return true, if the condition holds. null, if the evaluation caused an error
   */  
  @Override
  public Optional<Boolean> verify(ASTPetrinet petrinet, Marking marking) {
    final boolean result = getPlaceBindingList().stream().allMatch(binding -> {
      return marking.get(binding.getPlace()).compareTo(binding.getValue().getNatLiteral().getValue()) == 0;
    });

    return Optional.of(result);
  }
}
