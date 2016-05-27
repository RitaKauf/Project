
package visitors_java;

import java.util.HashMap;
import org.eclipse.jdt.core.dom.MethodDeclaration;


/**
 * This class is responsible for detecting manual array initialization instead
 * of Array.Fill usage. the detector will detect the array initialization by the
 * following steps: 1) Visit Loops and mark them 2) find the loop iterator from
 * the expression 3) Visit Assignment statement and check if the left hand
 * operand is an array access node. 4) check if array access node index is a
 * variable event for nested array access.
 * 
 */
public class FuncDeclerationVisitor extends AbstractVisitor {
	HashMap<String, Integer> mp = null;
	 
	public void Init(HashMap<String, Integer> map) {
		mp = map;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		
		String funcName = node.getName().toString();

		if (mp.containsKey(funcName)) {
			mp.put(funcName, mp.get(funcName) + 1);
		}
		else {
			mp.put(funcName, 1);
		}
		
		return true;
	}
	
	@Override
	public void endVisit(MethodDeclaration node) {

		super.endVisit(node);
	}

}
