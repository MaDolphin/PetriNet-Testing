package petrinettests.generators.defaultgenerators;

import de.monticore.io.paths.ModelPath;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteralBuilder;
import petrinet._ast.*;
import petrinet._cocos.PetrinetCoCoChecker;
import petrinet._symboltable.PetrinetGlobalScope;
import petrinet._symboltable.PetrinetLanguage;
import petrinet._symboltable.PetrinetSymbolTableCreatorDelegator;
import petrinet.analysis.CoverabilityTree;
import petrinet.cocos.PetrinetCoCos;
import petrinettests.PetrinetTestsMill;
import petrinettests._ast.*;
import petrinettests._visitor.PetrinetTestsDelegatorVisitor;
import petrinettests._visitor.PetrinetTestsVisitor;
import petrinettests.simulator.Simulator;
import petrinettests.simulator.TransitionNotEnabledException;
import petrinettests.simulator.TransitionNotFoundException;
import petrinettests.testgenerator.TestGenerator;

import javax.annotation.Nonnull;
import java.util.*;

public class CauseEffectGenerator implements TestGenerator {

  @Nonnull
  @Override
  public String getName() {
    return "CauseEffect";
  }

  @Nonnull
  @Override
  public ASTPetriNetTest generate(@Nonnull ASTPetrinet petrinet) {
    return generateTestcaseTableToPetrinetTest(generateTestcaseTableFromPetriNet(petrinet),petrinet);
  }

  public Map<Map<List<ASTPlace>,List<ASTPlace>>,ASTTransition> generateTestcaseTableFromPetriNet(ASTPetrinet oPetrinet) {

    ASTPetrinet initPetrinet = initPetrinetInitialMarking(oPetrinet.deepClone());
    setupPetriNet(initPetrinet);
    initPetrinet.deriveGraphInfo();

    List<ASTTransition> transitions = initPetrinet.getTransitionList();
    List<ASTPlace> places = initPetrinet.getPlaceList();

    Set<ASTPlace> startPlaces = new HashSet<ASTPlace>();
    Set<ASTPlace> endPlaces = new HashSet<ASTPlace>();
    Set<ASTTransition> middleTransition = new HashSet<ASTTransition>();

    for (ASTPlace p : places){
      Set<ASTTransition> astTransitionSet = p.getOutTransitions();
      if (astTransitionSet.size() > 1){
        middleTransition.addAll(astTransitionSet);
        for (ASTTransition tran : astTransitionSet){
          startPlaces.addAll(tran.getFromPlaces());
          endPlaces.addAll(tran.getToPlaces());
        }
      }
    }

    Map<Map<List<ASTPlace>,List<ASTPlace>>,ASTTransition> testcaseTable = generateTestcaseTable(middleTransition);
    return testcaseTable;
  }

  public ASTPetriNetTest generateTestcaseTableToPetrinetTest(Map<Map<List<ASTPlace>,List<ASTPlace>>,ASTTransition> testcaseTable,ASTPetrinet petrinet){

    ASTPetriNetTestBuilder testBuilder = PetrinetTestsMill.petriNetTestBuilder()
            .setName(petrinet.getName() + "_" + "AutoTest")
            .setImport(PetrinetTestsMill.importBuilder().setName(petrinet.getName()).build());

    testcaseTable.entrySet().forEach(entry->{
//      System.out.println("Transition: "+entry.getValue().getName());
      ASTTestcaseBuilder testcaseBuilder = PetrinetTestsMill.testcaseBuilder().setName(entry.getValue().getName() + "_TransitionTest");
      ASTTestcaseBodyBuilder testcaseBodyBuilder = PetrinetTestsMill.testcaseBodyBuilder();

      // Simulation
      ASTSimulation simulation = PetrinetTestsMill.simulationBuilder().addName(entry.getValue().getName()).build();

      entry.getKey().entrySet().forEach(e->{

        // Initial Marking
        ASTDefineMarkingBuilder initMarkingBuilder = PetrinetTestsMill.defineMarkingBuilder();
        for (ASTPlace p : e.getKey()){
//          System.out.println("FromPlace: "+p.getName());
          ASTNatLiteralBuilder astNatLiteralBuilder = new ASTNatLiteralBuilder();
          astNatLiteralBuilder.setDigits("1");
          initMarkingBuilder.addPlaceBinding(PetrinetTestsMill.placeBindingBuilder().setPlace(p.getName()).setValue(PetrinetTestsMill.markingValueBuilder().setNatLiteral(astNatLiteralBuilder.build()).build()).build());

        }
        testcaseBodyBuilder.setInitialMarking(PetrinetTestsMill.initialMarkingBuilder().setDefineMarking(initMarkingBuilder.build()).build());

        // Expected Marking
        ASTMarkingConditionBuilder expectMarkingBuilder = PetrinetTestsMill.markingConditionBuilder();
        for (ASTPlace p_value : e.getValue()){
//          System.out.println("ToPlace: "+p_value.getName());
          ASTNatLiteralBuilder astNatLiteralBuilder = new ASTNatLiteralBuilder();
          astNatLiteralBuilder.setDigits("1");
          expectMarkingBuilder.addPlaceBinding(PetrinetTestsMill.placeBindingBuilder().setPlace(p_value.getName()).setValue(PetrinetTestsMill.markingValueBuilder().setNatLiteral(astNatLiteralBuilder.build()).build()).build());
        }

        // combining values
        ASTTestStep testStep = PetrinetTestsMill.testStepBuilder().addSimulation(simulation).addExpectation(PetrinetTestsMill.expectationBuilder().setCondition(expectMarkingBuilder.build()).build()).build();
        testcaseBodyBuilder.addTestStep(testStep);
        testcaseBuilder.setTestcaseBody(testcaseBodyBuilder.build());
        testBuilder.addTestcase(testcaseBuilder.build());

      });

    });

//    testcaseTable.entrySet().forEach(entry->{
//      System.out.println("Transition: "+entry.getValue().getName());
//      entry.getKey().entrySet().forEach(e->{
//        for (ASTPlace p : e.getKey()){
//          System.out.println("FromPlace: "+p.getName());
//        }
//        for (ASTPlace p_value : e.getValue()){
//          System.out.println("ToPlace: "+p_value.getName());
//        }
//      });
//      System.out.println();
//      System.out.println();
//    });

    return testBuilder.build();
  }

  public Map<Map<List<ASTPlace>,List<ASTPlace>>,ASTTransition> generateTestcaseTable(Set<ASTTransition> middleTransition){
    Map<Map<List<ASTPlace>,List<ASTPlace>>,ASTTransition> testcaeTable = new HashMap<>();
    List<ASTPlace> keyList = new ArrayList<>();
    List<ASTPlace> valueList = new ArrayList<>();
    for (ASTTransition t : middleTransition){
      Map<List<ASTPlace>,List<ASTPlace>> petrinetMap = new HashMap<List<ASTPlace>, List<ASTPlace>>();
      keyList = new ArrayList<ASTPlace>(t.getFromPlaces());
      valueList = new ArrayList<ASTPlace>(t.getToPlaces());
      petrinetMap.put(keyList,valueList);
      testcaeTable.put(petrinetMap,t);
    }
    return testcaeTable;
  }

  public ASTPetrinet initPetrinetInitialMarking(ASTPetrinet petrinet){
    List<ASTPlace> astPlaces = petrinet.getPlaceList();
    for (ASTPlace p : astPlaces){
      p.setInitialAbsent();
    }
    return petrinet;
  }

  public void setupPetriNet(ASTPetrinet petrinet){
    PetrinetLanguage language = new PetrinetLanguage();
    PetrinetGlobalScope globalScope = new PetrinetGlobalScope(new ModelPath(), language);
    PetrinetSymbolTableCreatorDelegator creator = language.getSymbolTableCreator(globalScope);
    creator.createFromAST(petrinet);
    PetrinetCoCoChecker checker = PetrinetCoCos.getCheckerForAllCoCos();
    checker.checkAll(petrinet);
  }

  public static List<List<ASTPlace>> combineList(List<ASTPlace> list) {
    List<List<ASTPlace>> result = new ArrayList<List<ASTPlace>>();
    long n = (long)Math.pow(2,list.size());
    List<ASTPlace> combine;
    for (long l=0L; l<n; l++) {
      combine = new ArrayList<ASTPlace>();
      for (int i=0; i<list.size(); i++) {
        if ((l>>>i&1) == 1){
          ASTNatLiteralBuilder astNatLiteralBuilder = new ASTNatLiteralBuilder();
          astNatLiteralBuilder.setDigits("1");
          ASTPlace place = list.get(i);
          place.setInitial(astNatLiteralBuilder.build());
          combine.add(place);
        }
      }
      result.add(combine);
    }
    return result;
  }
}
