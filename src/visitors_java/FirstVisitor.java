package visitors_java;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FirstVisitor extends ASTVisitor {

	CompilationUnit m_Unit = null;
	Map<String, Integer> m_funcMap =null;
	List<IfElseParams> m_lParams = null;
	int m_EFD; 
	int m_loopCounter;
	int m_ifCounter;
	int m_switchCounter;
	double m_switchLength;

	
	String m_elseString = null;
	int m_elseStartPos = 0;
	int m_elseLength = 0;
	double m_huer1 = 0;
	double m_huer2 = 0;
	double m_huer3 = 0;
	
	HashMap<String, Integer> m_invocFlagMap =null;

	public void Init(CompilationUnit compilationUnit, HashMap<String, Integer> map, HashMap<String, Integer>flagMap) {
		m_Unit = compilationUnit;
		m_loopCounter = 1;
		m_ifCounter = 0;
		m_switchCounter = 0;
		m_funcMap = map;
		m_lParams = new ArrayList<IfElseParams>();
		
		m_invocFlagMap =  flagMap;
		
		// take the values for each heuristic from a file
		//if (!readXmlFile(getClass().getClassLoader().getResourceAsStream("heuristics.xml"))) {
			//System.out.print("\n[Warning] - error parsing the xml file, using default values");
			m_huer1 = 0.5;
			m_huer2 = 0.3;
			m_huer3 = 0.2;
		//}
		
	}
	
	private boolean readXmlFile(InputStream f) {
		 boolean res = true;
		 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		 DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			res = false;
			return res;
		}
		 org.w3c.dom.Document doc = null;
		try {
			doc = dBuilder.parse(f);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = false;
			return res;
		}
		 doc.getDocumentElement().normalize();
		 
		 String s1 = null;
		 String s2 = null;
		 String s3 = null;
		 
		 NodeList nList = doc.getElementsByTagName("heuristic");
		 for (int i=0; i<nList.getLength(); i++) {
			 Node nNode = nList.item(i);
			 if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				 Element eElement = (Element) nNode;
				 s1 = eElement.getAttribute("h1");
				 s2 = eElement.getAttribute("h2");
				 s3 = eElement.getAttribute("h3");
			 }
		 }
		 
		 convertStringToDouble(s1, s2, s3);
		 
		 return res;
	 }
	
	private void convertStringToDouble(String s1, String s2, String s3){
		 if(s1 == null || s2 == null || s3 == null){
			 System.out.print("\n Can't convert from string to double, using default values");
			 m_huer1 = 0.5;
			 m_huer2 = 0.3;
			 m_huer3 = 0.2;
			 return;
		 }
		 else {
			 
			 m_huer1 = Double.parseDouble(s1);
			 m_huer2 = Double.parseDouble(s2);
			 m_huer3 = Double.parseDouble(s3);
		 }
	 }
	
public boolean visit(MethodDeclaration node) {
		
		m_EFD = (m_funcMap.get(node.getName().toString())); 
		
		
		return true;
	}
	
	@Override
	public void endVisit(MethodDeclaration node) {

		super.endVisit(node);
	}

	public void endVisit(MethodInvocation node) {

		super.endVisit(node);
	}

	
	@Override
	public boolean visit(MethodInvocation node) {
		// if it is a system function, do nothing
		if (m_funcMap.get(node.getName().toString()) != 0) {
			//if it's not the first call to the function, add the last efd to the current efd 
			if(m_invocFlagMap.get(node.getName().toString()) != 0){
				
				
					if(m_ifCounter == 0 && m_switchCounter == 0){
						m_funcMap.put((node.getName().toString()), m_funcMap.get(node.getName().toString())+ m_EFD*m_loopCounter);
					}
					else{
						m_funcMap.put((node.getName().toString()), m_funcMap.get(node.getName().toString())+ getEfdVal(node)*m_loopCounter);
					}
			//replace efd	
			}
			else{
	
				if(m_ifCounter == 0 && m_switchCounter == 0){
					m_funcMap.put((node.getName().toString()), m_EFD*m_loopCounter);
				}
				else{
					m_funcMap.put((node.getName().toString()), m_funcMap.get(node.getName().toString())+ getEfdVal(node)*m_loopCounter);
				}
								
			}
			
			m_invocFlagMap.put(node.getName().toString(),1);
	}

		return true;
	}
	
	@Override
	public void endVisit(SwitchStatement node) {

		m_switchCounter--;
		if(m_switchCounter == 0 && m_ifCounter == 0){

			m_lParams.clear();
		}

		super.endVisit(node);
	}

	
	@Override
	public boolean visit(SwitchStatement node) {
		
		List<Statement> casesList = new ArrayList<Statement>();
		List<Double> tempList = new ArrayList<Double>();
		List<Integer> startPo = new ArrayList<Integer>();
		List<Integer> endPo = new ArrayList<Integer>();
		
		int nodeLength = 0;
		
//		String current = (m_Unit.getJavaElement().getElementName());
//		
//		String activeEditor = (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle());
//			
//		if (activeEditor.equals(current)) {
		
			/********************************/
			
			//  heuristics: considering the length of each case
			
			if (!node.statements().isEmpty()) {
				for ( int i=0;i < node.statements().size(); i++) {

						casesList.add((Statement) (node.statements().get(i)));
				}
				
				int j = -1;
				int temp;
				for (int i=0; i < casesList.size(); i++) {
					if(!(casesList.get(i).toString().contains("case")  || 
							casesList.get(i).toString().contains("default"))){
						if(tempList.get(j) == -1.1){
							tempList.set(j, (double) casesList.get(i).getLength());
							nodeLength += casesList.get(i).getLength();
							temp = casesList.get(i).getStartPosition() - endPo.get(j);
							endPo.set(j, endPo.get(j) + casesList.get(i).getLength() + temp);
						}
						else{
							tempList.set(j, tempList.get(j) + (double) casesList.get(i).getLength());
							nodeLength += casesList.get(i).getLength();
							temp = casesList.get(i).getStartPosition() - endPo.get(j);
							endPo.set(j, endPo.get(j) + casesList.get(i).getLength() + temp);
						}
					}
					else if(casesList.get(i).toString().contains("case")  || 
							casesList.get(i).toString().contains("default")){
						j++;
						tempList.add(j, -1.1);
						startPo.add(j, casesList.get(i).getStartPosition());
						endPo.add(j, casesList.get(i).getStartPosition() + casesList.get(i).getLength());
						//nodeLength -= casesList.get(i).getLength();
						
					}
				}
				
				for (int i=0; i < tempList.size(); i++) {
					tempList.set(i, (tempList.get(i)/nodeLength)); 
				}
			
//				//marker for expression
//				if(m_ifCounter == 0 && m_switchCounter == 0){
//					
//					m_Marker.markDegree(node.getExpression().getStartPosition()-4, node.getExpression().getLength()+7, m_EFD*m_loopCounter);
//					
//				}else{
//						
//					m_Marker.markDegree(node.getExpression().getStartPosition()-4, node.getExpression().getLength()+7, getEfdVal(node));
//
//				}
				
					
			}
			else {
				System.out.print("\n[The Switch has no cases]");
			}
			
				//params for the cases
			for(int i=0; i < tempList.size(); i++){
				
				IfElseParams params = new IfElseParams();
				params.m_startPosition = startPo.get(i);
				params.m_endPosition = endPo.get(i);
				if(m_ifCounter == 0 && m_switchCounter == 0){
					params.m_blockEfd = (int)(tempList.get(i)* m_EFD);
				}else{
					params.m_blockEfd = (int)(tempList.get(i)* getEfdVal(node));
				}
				m_lParams.add(params);
			}
			
//		}

		m_switchCounter++;		
		casesList.clear();
		tempList.clear();
		startPo.clear();
		endPo.clear();
		return true;
	}
	
	
	
	public void endVisit(ForStatement node) {
		// Decrease the loop counter
		m_loopCounter/=10;

		super.endVisit(node);
	}

	
	@Override
	public boolean visit(ForStatement node) {
		
		m_loopCounter*=10;
		
//		String current = (m_Unit.getJavaElement().getElementName());
//			
//		String activeEditor = (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle());
//			
//		if (activeEditor.equals(current)) {
//			
//			if(m_ifCounter == 0 && m_switchCounter == 0){
//				
//				m_Marker.markDegree(node.getStartPosition(), node.getLength(), m_EFD*m_loopCounter);
//			}
//			else{
//				
//				m_Marker.markDegree(node.getStartPosition(), node.getLength(), getEfdVal(node));
//			}
//		}

		return true;
	}
	
	public void endVisit(WhileStatement node) {
		// Decrease the loop counter
		m_loopCounter/=10;

		super.endVisit(node);
	}

	
	@Override
	public boolean visit(WhileStatement node) {
		
		m_loopCounter*=10;
		
//		String current = (m_Unit.getJavaElement().getElementName());
//			
//		String activeEditor = (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle());
//			
//		if (activeEditor.equals(current)) {
//			
//			if(m_ifCounter == 0 && m_switchCounter == 0){
//				
//				m_Marker.markDegree(node.getStartPosition(), node.getLength(), m_EFD*m_loopCounter);
//			}
//			else{
//				
//				m_Marker.markDegree(node.getStartPosition(), node.getLength(), getEfdVal(node));
//			}
//		}

		return true;
	}
	
	public void endVisit(DoStatement node) {
		// Decrease the loop counter
		m_loopCounter/=10;

		super.endVisit(node);
	}

	
	@Override
	public boolean visit(DoStatement node) {
		
		m_loopCounter*=10;
		
//		String current = (m_Unit.getJavaElement().getElementName());
//			
//		String activeEditor = (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle());
//			
//		if (activeEditor.equals(current)) {
//
//			if(m_ifCounter == 0 && m_switchCounter == 0){
//				
//				m_Marker.markDegree(node.getStartPosition(), node.getLength(), m_EFD*m_loopCounter);
//			}
//			else{
//				
//				m_Marker.markDegree(node.getStartPosition(), node.getLength(), getEfdVal(node));
//			}
//		}

		return true;
	}
	
	
	
	
	public void endVisit(IfStatement node) {
		// Decrease the loop counter
		m_ifCounter--;
		if(m_ifCounter == 0 && m_switchCounter == 0){

			m_lParams.clear();
		}

		super.endVisit(node);
	}

	
	@Override
	public boolean visit(IfStatement node) {
		double elseEfd_h1 = 0;
		double thenEfd_h1 = 0;
		double elseEfd_h2 = 0;
		double thenEfd_h2 = 0;
		double elseEfd_h3 = 0;
		double thenEfd_h3 = 0;
		double ifEfd = 0;
		double elseEfd = 0;
		int counter = 0;
		
//		String current = (m_Unit.getJavaElement().getElementName());
//		
//		String activeEditor = (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle());
//			
//		if (activeEditor.equals(current)) {
		
			// the first heuristics: if there is a key word like "assert", "exit".. then the code segment is "colder"
			String condition = node.getExpression().toString();
			String allNode = node.toString();
			String elseNode = null;
			if (node.getElseStatement() != null){
			
				elseNode = node.getElseStatement().toString();
				
			}
			if (allNode.contains("error") || allNode.contains("ERROR") || allNode.contains("exit") || allNode.contains("EXIT") 
					|| allNode.contains("assert") || allNode.contains("ASSERT") || allNode.contains("throw") ) {	
				if (elseNode.contains("error") || elseNode.contains("ERROR") || elseNode.contains("exit") || elseNode.contains("EXIT") 
						|| elseNode.contains("assert") || elseNode.contains("ASSERT") || elseNode.contains("throw") ) {
					 thenEfd_h1++;
				}else{
					elseEfd_h1++;
				}
			}else{
				thenEfd_h1++;
				elseEfd_h1++;
			}
			
			// the second heuristics: if there is a condition like "<,>,!=" or "||" or nested conditions then the code segment is "hotter"
			if (condition.contains("<") || condition.contains(">") || condition.contains("!=") || condition.contains("||")) {
				thenEfd_h2++;
			}
			if (condition.contains("==")){
				elseEfd_h2++;
			}
			if(condition.contains("&&")) {
				counter = countMatches(condition, "&&");
				elseEfd_h2+=counter;
			}
			
			// check if there are nested conditions
			counter = countMatches(allNode, "if") - countMatches(elseNode, "if");
			if(counter > 1){
				elseEfd_h2+= counter-1;
			}
			counter = countMatches(elseNode, "if");
			if(counter > 0){
				thenEfd_h2+=counter;
			}
			
			if(thenEfd_h2 == 0 && elseEfd_h2 == 0){
				thenEfd_h2++;
				elseEfd_h2++;
			}
			
			// the third heuristics: considering the length of the code
			int IfLen = 0;
			int ElseLen = 0;
			if (node.getElseStatement() != null){
				IfLen = node.getLength()- (node.getElseStatement().getLength());
				ElseLen = node.getElseStatement().getLength();
			}else{
				IfLen = node.getLength();
			}
			if(IfLen > ElseLen){
				thenEfd_h3++;
			}else if(IfLen < ElseLen){
				elseEfd_h3++;
			}else{
				thenEfd_h3++;
				elseEfd_h3++;
			}
			
			
			// if the first heuristic exists, calculate the weight 
			if (thenEfd_h1 != 0 || elseEfd_h1 != 0 ){
				ifEfd+= m_huer1*(thenEfd_h1/(thenEfd_h1+elseEfd_h1));
				elseEfd+= m_huer1*(elseEfd_h1/(thenEfd_h1+elseEfd_h1));
			}
			if (thenEfd_h2 != 0 || elseEfd_h2 != 0){
				ifEfd+= m_huer2*(thenEfd_h2/(thenEfd_h2+elseEfd_h2));
				elseEfd+= m_huer2*(elseEfd_h2/(thenEfd_h2+elseEfd_h2));
			}
			if(thenEfd_h3 != 0 || elseEfd_h3 != 0){
				ifEfd+= m_huer3*(thenEfd_h3/(thenEfd_h3+elseEfd_h3));
				elseEfd+= m_huer3*(elseEfd_h3/(thenEfd_h3+elseEfd_h3));
			}
			
			if(m_ifCounter == 0 && m_switchCounter == 0){
				ifEfd*= m_EFD*m_loopCounter;
				elseEfd*= m_EFD*m_loopCounter;
			}else{
				
				if(node.getElseStatement() != null) {
					
					elseEfd*= getEfdVal(node.getElseStatement());
				}
				
				ifEfd*= getEfdVal(node);
			}
			
		
//			//marker for expression
//			if(m_switchCounter == 0 && m_ifCounter == 0){
//				
//				m_Marker.markDegree(node.getExpression().getStartPosition()-4, node.getExpression().getLength()+7, m_EFD*m_loopCounter);
//				
//			}else{
//					
//				m_Marker.markDegree(node.getExpression().getStartPosition()-4, node.getExpression().getLength()+7, getEfdVal(node));
//
//			}
			
				
//		}
		
		if (node.getElseStatement() != null){
			
			//params for the if
			IfElseParams params = new IfElseParams();
			params.m_startPosition = node.getStartPosition();
			params.m_endPosition = node.getStartPosition() + node.getLength() - node.getElseStatement().getLength();
			params.m_blockEfd = (int)ifEfd;
			m_lParams.add(params);
			
			IfElseParams params2 = new IfElseParams();
			// params for the else
			params2.m_startPosition = node.getElseStatement().getStartPosition();
			params2.m_endPosition = node.getElseStatement().getStartPosition() + node.getElseStatement().getLength();
			params2.m_blockEfd = (int)elseEfd;
			m_lParams.add(params2);
			
		}
		else {
			
			//params for the if
			IfElseParams params3 = new IfElseParams();
			params3.m_startPosition = node.getStartPosition();
			params3.m_endPosition = node.getStartPosition() + node.getLength();
			params3.m_blockEfd = (int)ifEfd;
			m_lParams.add(params3);
			
		}
		m_ifCounter++;
		return true;
	}
	
	 public static int countMatches(String str, String sub) {
	      if (str == null || sub == null) {
	          return 0;
	      }
	      int count = 0;
	      int idx = 0;
	      while ((idx = str.indexOf(sub, idx)) != -1) {
	          count++;
	          idx += sub.length();
	      }
	      return count;
	  }
	 
	 private int getEfdVal(ASTNode node){
		 int start=0;
		 int end = 0;
		 for (int i=m_lParams.size()-1; i >= 0 ; i--) {
			  start = m_lParams.get(i).m_startPosition;
			  end = m_lParams.get(i).m_endPosition;
			  if((node.getStartPosition() >= start) &&
					  (node.getStartPosition() + node.getLength()) <= end){
				  
				  return m_lParams.get(i).m_blockEfd;
			  }
				
			}
		 System.out.print("\n[Error] : can't find the node - " + node.toString());
		 return -1;
	 }

}
