
package visitors_java;

import java.util.HashMap;
import org.eclipse.jdt.core.dom.MethodInvocation;


public class FuncInvocationVisitor extends AbstractVisitor {
	HashMap<String, Integer> mp = null;
	 
	public void Init(HashMap<String, Integer> map) {
		mp = map;
	}

	
@Override
public boolean visit(MethodInvocation node) {
		
		String funcName = node.getName().toString();
		
		if (mp.containsKey(funcName) && mp.get(funcName) != 0) {
			mp.put(funcName, mp.get(funcName) + 1);
		}
		else {
			mp.put(funcName, 0);
		}
		
		return true;
	}
	
	@Override
	public void endVisit(MethodInvocation node) {

		super.endVisit(node);
	}

}
