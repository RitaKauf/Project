
package visitors_cpp;

import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;



public abstract class AbstractVisitorCpp extends ASTVisitor {

	/**
	 * Holds reference to the current working compilation unit. will be used for
	 * getting line numbers
	 */
	protected ITranslationUnit m_translationUnit;

	/**
	 * Reference to a marker creator which will be placing markers at the
	 * required locations
	 */
	//protected DegreeMarker m_degreeMarker;

	/**
	 * Starts the process of comment detecting on the supplied compilation unit.
	 * 
	 * @param unit
	 *            the AST root node. Bindings have to have been resolved.
	 * @param icoUnit
	 *            the ICompilationUnit unit
	 */
	public void process(ITranslationUnit unit) {

		// save the compilation unit for later use
		m_translationUnit = unit;
		// create a newer marker writer
		//m_degreeMarker = new DegreeMarker(icoUnit);
		
		
	}

}
