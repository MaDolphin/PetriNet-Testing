package petrinettests.simulator;

import java.util.Optional;

import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTTransition;
import petrinet.analysis.Marking;

/**
 *
 */
public class Simulator {
  private ASTPetrinet petrinet;
  private Marking currentMarking;

  /**
   * TODO
   * Overrides initial marking
   *
   * @param petrinet
   */
  public Simulator(ASTPetrinet petrinet, Marking initialMarking) {
    this.petrinet = petrinet;
    currentMarking = initialMarking;
  }

  /**
   * TODO
   * Uses initial marking of given Petrinet
   *
   * @param petrinet
   */
  public Simulator(ASTPetrinet petrinet) {
    this(petrinet, new Marking(petrinet));
  }

  /**
   * TODO
   *
   * @return
   */
  public ASTPetrinet getPetrinet() {
    return petrinet;
  }

  /**
   * TODO
   *
   * @return
   */
  public Marking getCurrentMarking() {
    return currentMarking;
  }

  public void setCurrentMarking(Marking currentMarking) {
    this.currentMarking = currentMarking;
  }

  /**
   * TODO
   *
   * @param transition
   * @throws TransitionNotEnabledException
   */
  public void simulateTransition(ASTTransition transition) throws TransitionNotEnabledException {
    if (!currentMarking.enabled(transition)) {
      throw new TransitionNotEnabledException(transition.getName());
    }

    // Fire the transition on the ptrinet
    currentMarking.fire(transition);
  }

  /**
   * TODO
   *
   * @param transition
   * @throws TransitionNotEnabledException
   * @throws TransitionNotFoundException
   */
  public void simulateTransition(String transition) throws TransitionNotEnabledException, TransitionNotFoundException {
    // Attempt to find transition
    Optional<ASTTransition> optAstTransition = findTransition(transition);
    ASTTransition astTransition = optAstTransition.orElseThrow(TransitionNotFoundException::new);

    simulateTransition(astTransition);
  }

  /**
   * @param transition
   * @return
   */
  public boolean isTransitionEnabled(ASTTransition transition) {
    return currentMarking.enabled(transition);
  }

  /**
   * @param transitionName
   * @return
   * @throws TransitionNotFoundException
   */
  public boolean isTransitionEnabled(String transitionName) throws TransitionNotFoundException {
    // Attempt to find transition
    Optional<ASTTransition> optAstTransition = findTransition(transitionName);
    ASTTransition astTransition = optAstTransition.orElseThrow(TransitionNotFoundException::new);

    return isTransitionEnabled(astTransition);
  }

  /**
   * TODO
   *
   * @param transitionName
   * @return
   */
  private Optional<ASTTransition> findTransition(String transitionName) {
    return getPetrinet().getTransitionList().stream().filter(t -> t.getName().equals(transitionName)).findFirst();
  }

}