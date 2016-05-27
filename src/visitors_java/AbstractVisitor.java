
package visitors_java;

import markers_java.DegreeMarker;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;


public abstract class AbstractVisitor extends ASTVisitor {

	/**
	 * Holds reference to the current working compilation unit. will be used for
	 * getting line numbers
	 */
	protected CompilationUnit m_compilationUnit;

	/**
	 * Reference to a marker creator which will be placing markers at the
	 * required locations
	 */
	protected DegreeMarker m_degreeMarker;

	/**
	 * Starts the process of comment detecting on the supplied compilation unit.
	 * 
	 * @param unit
	 *            the AST root node. Bindings have to have been resolved.
	 * @param icoUnit
	 *            the ICompilationUnit unit
	 */
	public void process(CompilationUnit unit, ICompilationUnit icoUnit) {

		// save the compilation unit for later use
		m_compilationUnit = unit;
		// create a newer marker writer
		m_degreeMarker = new DegreeMarker(icoUnit);
		//m_degreeMarker.getId();
		// start the AST Visitor
		//unit.accept(this);
		
	}

}
