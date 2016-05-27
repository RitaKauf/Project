
package visitors_java;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.MethodDeclaration;
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
public class Visitors extends AbstractVisitor {
	 MethodVisitor m_methodVisitor = null;
	 //FuncDeclerationVisitor m_tempVisitor = null;
	 HashMap<String, Integer> m_CountMap = null;
	 
//	 FirstVisitor m_firstVisitor = null;
	 
	public void Init(HashMap<String, Integer> count) {
		
		m_methodVisitor = new MethodVisitor();
		
		m_CountMap = count;
		
		m_methodVisitor.Init(m_degreeMarker, m_compilationUnit,m_CountMap);
		
//		
//		m_firstVisitor = new FirstVisitor();
//		m_firstVisitor.Init(m_compilationUnit,m_CountMap);
	}
	
	
	@Override
	public boolean visit(MethodDeclaration node) {
		
//		m_firstVisitor.m_EFD = (m_CountMap.get(node.getName().toString())); 
//		node.getBody().accept(m_firstVisitor);
		
		
		m_methodVisitor.m_EFD = (m_CountMap.get(node.getName().toString())); 
		node.getBody().accept(m_methodVisitor);
		
		
		//mark the function declaration
		String current = (m_compilationUnit.getJavaElement().getElementName());
		
		String activeEditor = (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle());
			
		if (activeEditor.equals(current)) {
		//placeProblemMarker(node.getStartPosition(),node.getLength());
			m_degreeMarker.markDegree(node.getStartPosition(), node.getName().getLength() + 20, m_methodVisitor.m_EFD);
		}
		
		return true;
	}
	
	@Override
	public void endVisit(MethodDeclaration node) {

		super.endVisit(node);
	}

}
