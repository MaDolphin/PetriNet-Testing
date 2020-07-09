package petrinettests._ast;

import java.util.Collection;
import java.util.List;

public class ASTPetriNetTest extends ASTPetriNetTestTOP {
    
    /**
     * TODO 
     * @return
     */
    public Collection<ASTTestcase> getAllTestcases() {
        if (isPresentTestcaseBody()) {
            // New ASTTestcase to host the single testcase body
            ASTTestcase testcase = new ASTTestcase();
            testcase.setName(this.getName());
            testcase.setTestcaseBody(getTestcaseBody());
            
            return List.of(testcase);

        } else if (!isEmptyTestcases()) {
            return getTestcaseList();
        }

        return List.of();
    }
}