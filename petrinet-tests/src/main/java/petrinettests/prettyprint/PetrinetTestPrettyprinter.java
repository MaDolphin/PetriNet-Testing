package petrinettests.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import petrinettests._ast.*;
import petrinettests._visitor.PetrinetTestsVisitor;

import javax.annotation.Nonnull;

public class PetrinetTestPrettyprinter extends IndentPrinter implements PetrinetTestsVisitor {
  protected PetrinetTestPrettyprinter(int indentation) {
    setIndentLength(indentation);
  }

  @Nonnull
  public static String print(@Nonnull ASTPetriNetTest test) {
    PetrinetTestPrettyprinter prettyprinter = new PetrinetTestPrettyprinter(4);
    test.accept(prettyprinter);
    return prettyprinter.getContent();
  }

  @Override
  public void visit(ASTPetrinetTestsNode node) {
    CommentPrettyPrinter.printPreComments(node, this);
  }

  @Override
  public void endVisit(ASTPetrinetTestsNode node) {
    CommentPrettyPrinter.printPostComments(node, this);
  }

  @Override
  public void visit(ASTPetriNetTest node) {
    print("pntest ");
    print(node.getName());
    println(" { ");
    indent();
  }

  @Override
  public void endVisit(ASTPetriNetTest node) {
    unindent();
    println("}");
  }

  @Override
  public void visit(ASTImport node) {
    print("use petrinet ");
    println(node.getName());
    println();  // one extra line, because why not
  }

  @Override
  public void visit(ASTTestcase node) {
    print("testcase ");
    print(node.getName());
    println(" {");
    indent();
  }

  @Override
  public void endVisit(ASTTestcase node) {
    unindent();
    println("}");
  }

  @Override
  public void visit(ASTInitialMarking node) {
    print("initial marking ");
  }

  @Override
  public void visit(ASTInheritMarking node) {
    print("inherited ");  // keep the space for eventual rest specification
  }

  @Override
  public void endVisit(ASTInheritMarking node) {
    println();
  }

  @Override
  public void visit(ASTDefineMarking node) {
    println("{");
    indent();
  }

  @Override
  public void handle(ASTDefineMarking node) {
    getRealThis().visit(node);
    boolean first = true;
    for (ASTPlaceBinding binding : node.getPlaceBindingList()) {
      if (first) {
        first = false;
      }
      else {
        println(",");
      }
      binding.accept(getRealThis());
    }
    if (!first) {
      println();
    }
    getRealThis().endVisit(node);
  }

  @Override
  public void endVisit(ASTDefineMarking node) {
    unindent();
    println("}");
  }

  @Override
  public void visit(ASTPlaceBinding node) {
    print(node.getPlace());
    print(" ");
  }

  @Override
  public void visit(ASTMarkingValue node) {
    print(node.getNatLiteral().getValue());
  }

  @Override
  public void visit(ASTSimulation node) {
    println("simulate {");
    indent();
    boolean first = true;
    for (String name : node.getNameList()) {
      if (first) {
        first = false;
      }
      else {
        println(",");
      }
      print(name);
    }
    if (!first) {
      println();
    }
    unindent();
    println("}");
  }

  @Override
  public void visit(ASTNegation node) {
    print("not ");
  }

  @Override
  public void visit(ASTConjunction node) {
    println("all {");
    indent();
  }

  @Override
  public void handle(ASTConjunction node) {
    getRealThis().visit(node);
    boolean first = true;
    for (ASTCondition condition : node.getConditionList()) {
      if (first) {
        first = false;
      }
      else {
        println(",");
      }
      condition.accept(getRealThis());
    }
    if (!first) {
      println();
    }
    getRealThis().endVisit(node);
  }

  @Override
  public void endVisit(ASTConjunction node) {
    unindent();
    print("}");
  }

  @Override
  public void visit(ASTDisjunction node) {
    println("any {");
    indent();
  }

  @Override
  public void handle(ASTDisjunction node) {
    getRealThis().visit(node);
    boolean first = true;
    for (ASTCondition condition : node.getConditionList()) {
      if (first) {
        first = false;
      }
      else {
        println(",");
      }
      condition.accept(getRealThis());
    }
    if (!first) {
      println();
    }
    getRealThis().endVisit(node);
  }

  @Override
  public void endVisit(ASTDisjunction node) {
    unindent();
    print("}");
  }

  @Override
  public void visit(ASTMarkingCondition node) {
    println("marking {");
    indent();
  }

  @Override
  public void handle(ASTMarkingCondition node) {
    getRealThis().visit(node);
    boolean first = true;
    for (ASTPlaceBinding binding : node.getPlaceBindingList()) {
      if (first) {
        first = false;
      }
      else {
        println(",");
      }
      binding.accept(getRealThis());
    }
    if (!first) {
      println();
    }
    getRealThis().endVisit(node);
  }

  @Override
  public void endVisit(ASTMarkingCondition node) {
    unindent();
    print("}");
  }

  @Override
  public void visit(ASTExpectation node) {
    print("expect ");
  }

  @Override
  public void endVisit(ASTExpectation node) {
    println();
  }
}
