
package visitors_cpp;

import java.util.HashMap;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.ui.PlatformUI;




/**
 * This class is responsible for detecting manual array initialization instead
 * of Array.Fill usage. the detector will detect the array initialization by the
 * following steps: 1) Visit Loops and mark them 2) find the loop iterator from
 * the expression 3) Visit Assignment statement and check if the left hand
 * operand is an array access node. 4) check if array access node index is a
 * variable event for nested array access.
 * 
 */
public class Visitorscpp extends AbstractVisitorCpp {
	 //MethodVisitor m_methodVisitor = null;
	 HashMap<String, Integer> m_CountMap = null;
	 
	public void Init(HashMap<String, Integer> count) {
		
		//m_methodVisitor = new MethodVisitor();
		m_CountMap = count;
		//m_methodVisitor.Init(m_degreeMarker, m_translationUnit ,m_CountMap);
	}
	
	
	public int visit(IASTFunctionDeclarator node) {
		//m_methodVisitor.m_EFD = (m_CountMap.get(node.getName().toString())) * 10; 
		//node.getBody().accept(m_methodVisitor);
	
		
		//mark the function declaration
		String current = (m_translationUnit.getElementName());
		
		String activeEditor = (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle());
			
		if (activeEditor.equals(current)) {
			System.out.print("\n" + node.getRawSignature());
			//m_degreeMarker.markDegree(node.getStartPosition(), node.getName().getLength() + 20, m_methodVisitor.m_EFD);
		}
		
		return PROCESS_CONTINUE;
	}
	
	public int leave(IASTFunctionDeclarator node) {

		return super.leave(node);
	}


	public void toVisitNodes() {
		// TODO Auto-generated method stub
		this.shouldVisitNames = true;
		this.shouldVisitDeclarations = true;
		this.shouldVisitStatements = true;
		this.shouldVisitDeclarators = true;
		this.shouldVisitDeclSpecifiers = true;
		this.shouldVisitEnumerators = true;
		this.shouldVisitExpressions = true;
		this.shouldVisitInitializers = true;
		this.shouldVisitParameterDeclarations = true;
		this.shouldVisitProblems = true;
		this.shouldVisitTranslationUnit = true;
		this.shouldVisitTypeIds = true;
	}

}
