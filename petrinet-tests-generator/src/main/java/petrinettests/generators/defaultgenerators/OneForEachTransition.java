package petrinettests.generators.defaultgenerators;

import petrinet._ast.ASTFromEdge;
import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTToEdge;
import petrinet._ast.ASTTransition;
import petrinettests.PetrinetTestsMill;
import petrinettests._ast.*;
import petrinettests.testgenerator.TestGenerator;

import javax.annotation.Nonnull;

public class OneForEachTransition implements TestGenerator {
  @Nonnull
  @Override
  public String getName() {
    return "OneForEachTransition";
  }

  @Nonnull
  @Override
  public ASTPetriNetTest generate(@Nonnull ASTPetrinet petrinet) {
    ASTPetriNetTestBuilder testBuilder = PetrinetTestsMill.petriNetTestBuilder()
        .setName(petrinet.getName() + "_" + getName() + "_test")
        .setImport(PetrinetTestsMill.importBuilder().setName(petrinet.getName()).build());

    for (ASTTransition transition : petrinet.getTransitionList()) {
      ASTTestcaseBuilder testcaseBuilder = PetrinetTestsMill.testcaseBuilder().setName(transition.getName() + "_TransitionTest");
      ASTTestcaseBodyBuilder testcaseBodyBuilder = PetrinetTestsMill.testcaseBodyBuilder();

      // Initial Marking
      ASTDefineMarkingBuilder initMarkingBuilder = PetrinetTestsMill.defineMarkingBuilder();
      for (ASTFromEdge fromEdge : transition.getFromEdgeList()) {
        initMarkingBuilder.addPlaceBinding(PetrinetTestsMill.placeBindingBuilder().setPlace(fromEdge.getPlace()).setValue(PetrinetTestsMill.markingValueBuilder().setNatLiteral(fromEdge.getCount()).build()).build());
      }
      testcaseBodyBuilder.setInitialMarking(PetrinetTestsMill.initialMarkingBuilder().setDefineMarking(initMarkingBuilder.build()).build());

      // Simulation
      ASTSimulation simulation = PetrinetTestsMill.simulationBuilder().addName(transition.getName()).build();

      // Expected Marking
      ASTMarkingConditionBuilder expectMarkingBuilder = PetrinetTestsMill.markingConditionBuilder();
      for (ASTToEdge toEdge : transition.getToEdgeList()) {
        expectMarkingBuilder.addPlaceBinding(PetrinetTestsMill.placeBindingBuilder().setPlace(toEdge.getPlace()).setValue(PetrinetTestsMill.markingValueBuilder().setNatLiteral(toEdge.getCount()).build()).build());
      }

      // combining values
      ASTTestStep testStep = PetrinetTestsMill.testStepBuilder().addSimulation(simulation).addExpectation(PetrinetTestsMill.expectationBuilder().setCondition(expectMarkingBuilder.build()).build()).build();
      testcaseBodyBuilder.addTestStep(testStep);
      testcaseBuilder.setTestcaseBody(testcaseBodyBuilder.build());
      testBuilder.addTestcase(testcaseBuilder.build());
    }

    return testBuilder.build();
  }
}
