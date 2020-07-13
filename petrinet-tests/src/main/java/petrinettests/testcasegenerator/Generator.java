package petrinettests.testcasegenerator;

import de.monticore.io.paths.ModelPath;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteralBuilder;
import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;
import petrinet._cocos.PetrinetCoCoChecker;
import petrinet._symboltable.PetrinetGlobalScope;
import petrinet._symboltable.PetrinetLanguage;
import petrinet._symboltable.PetrinetSymbolTableCreatorDelegator;
import petrinet.analysis.CoverabilityTree;
import petrinet.cocos.PetrinetCoCos;
import petrinettests._ast.*;
import petrinettests._visitor.PetrinetTestsDelegatorVisitor;
import petrinettests._visitor.PetrinetTestsVisitor;
import petrinettests.simulator.Simulator;
import petrinettests.simulator.TransitionNotEnabledException;
import petrinettests.simulator.TransitionNotFoundException;

import java.util.*;

public class Generator{

  public ASTPetriNetTest getAllTestcase(ASTPetrinet petrinet) throws TransitionNotEnabledException, TransitionNotFoundException {

    ASTPetriNetTest petriNetTest = new ASTPetriNetTest();

    petriNetTest = generateTestcaseTableToPetrinetTest(generateTestcaseTableFromPetriNet(petrinet));

    return petriNetTest;
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

//    List<ASTPlace> startPlacesList = new ArrayList<ASTPlace>(startPlaces);
//    List<List<ASTPlace>> combinePlaces = combineList(startPlacesList);

//    for (List<ASTPlace> astPlaces : combinePlaces){
//      if (astPlaces.size()>0){
//        ASTPetrinet tempPetriNet = initPetrinetInitialMarking(oPetrinet.deepClone());
//        for (ASTPlace op : astPlaces){
//          for (ASTPlace p : tempPetriNet.getPlaceList()){
//            if (p.getName() == op.getName()){
//              p.setInitial(op.getInitial());
//            }
//          }
//        }
//        setupPetriNet(tempPetriNet);
//        Simulator sim = new Simulator(tempPetriNet);
//        for (ASTTransition t : middleTransition){
//          try{
//            sim.simulateTransition(t.getName());
//          }catch (Exception e){
//          }
//        }
//        List<ASTPlace> reachPlaces = new ArrayList<ASTPlace>(endPlaces);
//        for (ASTPlace p : reachPlaces){
//          ASTNatLiteralBuilder astNatLiteralBuilder = new ASTNatLiteralBuilder();
//          int value = Math.abs(sim.getCurrentMarking().get(p.getName()).compareTo(0));
//          astNatLiteralBuilder.setDigits(String.valueOf(value));
//          p.setInitial(astNatLiteralBuilder.build());
//        }
//        petrinetMap.put(astPlaces,reachPlaces);
////        System.out.println(petrinetMap);
//
//      }
//
//    }

  }

  public ASTPetriNetTest generateTestcaseTableToPetrinetTest(Map<Map<List<ASTPlace>,List<ASTPlace>>,ASTTransition> testcaseTable){

    ASTPetriNetTest finalPetriNetTest = new ASTPetriNetTest();

    testcaseTable.entrySet().forEach(entry->{
      entry.getKey().entrySet().forEach(e->{
        for (ASTPlace p : e.getKey()){
          System.out.println("FromPlace: "+p.getName());
        }
        for (ASTPlace p_value : e.getValue()){
          System.out.println(p_value.getName());
        }
      });
      System.out.println("Transition: "+entry.getValue().getName());
      System.out.println();
      System.out.println();
    });

    return finalPetriNetTest;
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


  public ASTTestcase getTestcase(List<ASTTransition> transitions,List<ASTPlace> places, ASTPetrinet petrinet, ASTPetriNetTest astPetriNetTest) throws TransitionNotEnabledException, TransitionNotFoundException {

    ASTInheritMarkingBuilder astInheritMarkingBuilder = new ASTInheritMarkingBuilder();
    astInheritMarkingBuilder.setRestSpecificationAbsent();

    ASTInitialMarkingBuilder astInitialMarkingBuilder = new ASTInitialMarkingBuilder();
    astInitialMarkingBuilder.setInheritMarking(astInheritMarkingBuilder.build());

    ASTTestcaseBodyBuilder astTestcaseBodyBuilder = new ASTTestcaseBodyBuilder();
    astTestcaseBodyBuilder.setInitialMarking(astInitialMarkingBuilder.build());

    List<ASTPlaceBinding> placeBindings = new ArrayList<ASTPlaceBinding>();

    for(ASTPlace p : places){
      ASTMarkingValueBuilder astMarkingValueBuilder = new ASTMarkingValueBuilder();
      if(!p.isPresentInitial()){
        ASTNatLiteralBuilder astNatLiteralBuilder = new ASTNatLiteralBuilder();
        astNatLiteralBuilder.setDigits("0");
        astMarkingValueBuilder.setNatLiteral(astNatLiteralBuilder.build());
      }else {
        astMarkingValueBuilder.setNatLiteral(p.getInitial());
      }

      ASTPlaceBindingBuilder astPlaceBindingBuilder = new ASTPlaceBindingBuilder();
      astPlaceBindingBuilder.setPlace(p.getName());
      astPlaceBindingBuilder.setValue(astMarkingValueBuilder.build());
      placeBindings.add(astPlaceBindingBuilder.build());
    }


    ASTMarkingConditionBuilder astMarkingConditionBuilder = new ASTMarkingConditionBuilder();
    astMarkingConditionBuilder.setPlaceBindingList(placeBindings);

    ASTConjunctionBuilder astConjunctionBuilder = new ASTConjunctionBuilder();
    astConjunctionBuilder.addCondition(astMarkingConditionBuilder.build());

    ASTExpectationBuilder astExpectationBuilder = new ASTExpectationBuilder();
    astExpectationBuilder.setCondition(astConjunctionBuilder.build());

    astTestcaseBodyBuilder.addExpectation(astExpectationBuilder.build());

    List<String> transitionnames = new ArrayList<String>();
    for(ASTTransition t : transitions){
      transitionnames.add(t.getName());
    }

    ASTSimulationBuilder astSimulationBuilder = new ASTSimulationBuilder();
    astSimulationBuilder.setNameList(transitionnames);

//    --------------------------------------

    Simulator sim = new Simulator(petrinet);
    ASTTransition lastTran = new ASTTransition();
    for(ASTTransition t : transitions){
      lastTran = t;
      sim.simulateTransition(t.getName());
    }

//    -------------------------------------

    List<ASTPlaceBinding> placeBindings1 = new ArrayList<ASTPlaceBinding>();

    for(ASTPlace p : lastTran.getToPlaces()){
      ASTPlaceBindingBuilder astPlaceBindingBuilder = new ASTPlaceBindingBuilder();
      astPlaceBindingBuilder.setPlace(p.getName());
      ASTMarkingValueBuilder astMarkingValueBuilder = new ASTMarkingValueBuilder();
      ASTNatLiteralBuilder astNatLiteralBuilder = new ASTNatLiteralBuilder();
      astNatLiteralBuilder.setDigits(Integer.toString(Math.abs(sim.getCurrentMarking().get(p.getName()).compareTo(0))));
      astMarkingValueBuilder.setNatLiteral(astNatLiteralBuilder.build());
      astPlaceBindingBuilder.setValue(astMarkingValueBuilder.build());
      placeBindings.add(astPlaceBindingBuilder.build());
    }

    ASTMarkingConditionBuilder astMarkingConditionBuilder1 = new ASTMarkingConditionBuilder();
    astMarkingConditionBuilder1.setPlaceBindingList(placeBindings1);

    ASTConjunctionBuilder astConjunctionBuilder1 = new ASTConjunctionBuilder();
    astConjunctionBuilder1.addCondition(astMarkingConditionBuilder1.build());

    ASTExpectationBuilder astExpectationBuilder1 = new ASTExpectationBuilder();
    astExpectationBuilder1.setCondition(astConjunctionBuilder1.build());

    ASTTestStepBuilder astTestStepBuilder = new ASTTestStepBuilder();
    astTestStepBuilder.addSimulation(astSimulationBuilder.build());
    astTestStepBuilder.addExpectation(astExpectationBuilder1.build());

    astTestcaseBodyBuilder.addTestStep(astTestStepBuilder.build());

    ASTTestcaseBuilder astTestcaseBuilder = new ASTTestcaseBuilder();
    astTestcaseBuilder.setName("Test 1");
    astTestcaseBuilder.setTestcaseBody(astTestcaseBodyBuilder.build());

    return astTestcaseBuilder.build();
  }



//  public static List<List<ASTPlace>> combineList(List<ASTPlace> list) {
//    List<List<ASTPlace>> result = new ArrayList<List<ASTPlace>>();
//    long n = (long)Math.pow(2,list.size());
//    List<ASTPlace> combine;
//    for (long l=0L; l<n; l++) {
//      combine = new ArrayList<ASTPlace>();
//      for (int i=0; i<list.size(); i++) {
//        if ((l>>>i&1) == 1){
//          ASTNatLiteralBuilder astNatLiteralBuilder = new ASTNatLiteralBuilder();
//          astNatLiteralBuilder.setDigits("1");
//          ASTPlace place = list.get(i);
//          place.setInitial(astNatLiteralBuilder.build());
//          combine.add(place);
//        }
//      }
//      result.add(combine);
//    }
//    return result;
//  }
}
