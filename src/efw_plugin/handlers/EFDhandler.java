package efw_plugin.handlers;


import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import visitors_java.FirstVisitor;
import visitors_java.FuncDeclerationVisitor;
import visitors_java.FuncInvocationVisitor;
import visitors_java.Visitors;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class EFDhandler extends AbstractHandler {
	
	Visitors m_visitors = null;
	FuncDeclerationVisitor m_declVisitor = null;
	FuncInvocationVisitor m_invcVisitor = null;
	HashMap<String, Integer> m_countMethodMap = null; 
	FirstVisitor m_firstVisitor = null;
	
	HashMap<String, Integer> m_FlagMap =null;
	/**
	 * The constructor.
	 */
	public EFDhandler() {
	
	}
	
	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";

	
	@SuppressWarnings("deprecation")
	private void PreProcess1(FuncDeclerationVisitor visitor, IProject pro) {
		
		IPackageFragment[] packages = null;
		try {
			packages = JavaCore.create(pro).getPackageFragments();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    // parse(JavaCore.create(project));
	    for (IPackageFragment mypackage : packages) {
	      try {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				  for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
					  ASTParser parser = ASTParser.newParser(AST.JLS3);
					  parser.setKind(ASTParser.K_COMPILATION_UNIT);
					  parser.setSource(unit); // set source
					  parser.setResolveBindings(true); // we need bindings later on
						// create a new compilation unit
					  CompilationUnit comUnit = (CompilationUnit) parser.createAST(null /* IProgressMonitor */); 
					  
					  comUnit.accept(visitor);
						
				  }
			  }
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    }
	}
	
	@SuppressWarnings("deprecation")
	private void PreProcess2(FuncInvocationVisitor visitor, IProject pro) {
		
		IPackageFragment[] packages = null;
		try {
			packages = JavaCore.create(pro).getPackageFragments();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    // parse(JavaCore.create(project));
	    for (IPackageFragment mypackage : packages) {
	      try {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				  for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
					  ASTParser parser = ASTParser.newParser(AST.JLS3);
					  parser.setKind(ASTParser.K_COMPILATION_UNIT);
					  parser.setSource(unit); // set source
					  parser.setResolveBindings(true); // we need bindings later on
						// create a new compilation unit
					  CompilationUnit comUnit = (CompilationUnit) parser.createAST(null /* IProgressMonitor */); 
					  comUnit.accept(visitor);
						
				  }
			  }
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    }
	}
	
@SuppressWarnings("deprecation")
private void PreProcess3(FirstVisitor visitor, IProject pro) {
		
		IPackageFragment[] packages = null;
		try {
			packages = JavaCore.create(pro).getPackageFragments();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    // parse(JavaCore.create(project));
	    for (IPackageFragment mypackage : packages) {
	      try {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				  for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
					  ASTParser parser = ASTParser.newParser(AST.JLS3);
					  parser.setKind(ASTParser.K_COMPILATION_UNIT);
					  parser.setSource(unit); // set source
					  parser.setResolveBindings(true); // we need bindings later on
						// create a new compilation unit
					  CompilationUnit comUnit = (CompilationUnit) parser.createAST(null /* IProgressMonitor */);
					 
					  m_firstVisitor.Init(comUnit, m_countMethodMap, m_FlagMap);
					  comUnit.accept(visitor);
						
				  }
			  }
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    }
	}
	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    IWorkspaceRoot root = workspace.getRoot();
	    // Get all projects in the workspace
	    IProject[] projects = root.getProjects();
	    // Loop over all projects
	    for (IProject project : projects) {
	      try {
	    	 
	        if (project.isNatureEnabled(JDT_NATURE)) {
	        	if (isFileInProject(project)) {
	        	//project.((PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getClass().get);
	        		analyseMethods(project);
	        	}
	          
	        }
	      } catch (CoreException e) {
	        e.printStackTrace();
	      }
	    }
	   
	    m_countMethodMap.clear();
	    
		return null;
	}
	
	  private void analyseMethods(IProject project) throws JavaModelException {
		  	
		    m_countMethodMap = new HashMap<String, Integer>();
		    m_FlagMap = new HashMap<String, Integer>();
		  	m_declVisitor = new FuncDeclerationVisitor();
		  	m_invcVisitor = new FuncInvocationVisitor();
		  	m_declVisitor.Init(m_countMethodMap);
		  	
		  	PreProcess1(m_declVisitor, project);
		  	
		  	m_invcVisitor.Init(m_countMethodMap);
		  	
		  	PreProcess2(m_invcVisitor, project);
		  	
		  	
		  	//update the map to be number of calls * 10
		    for (Entry<String, Integer> entry1 : m_countMethodMap.entrySet()) {
		    	m_countMethodMap.put(entry1.getKey(), entry1.getValue()*10);
		    }
		    
		    m_firstVisitor = new FirstVisitor();
		    
		    
		    m_FlagMap.putAll(m_countMethodMap);
		    
		  //update the map: for each func , flag is 0
		    for (Entry<String, Integer> entry2 : m_FlagMap.entrySet()) {
		    	m_FlagMap.put(entry2.getKey(), 0);
		    }
		    
		  	PreProcess3(m_firstVisitor, project);
//		  	for (Entry<String, Integer> entry : m_countVisitor.mp.entrySet()) {
//		  	    System.out.println("\n"+entry.getKey() + ", " + entry.getValue());
//		  	}
		  	
		    IPackageFragment[] packages = JavaCore.create(project).getPackageFragments();
		    // parse(JavaCore.create(project));
		    for (IPackageFragment mypackage : packages) {
		      if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
		        createAST(mypackage);
		      }

		    }
		  }

		  private void createAST(IPackageFragment mypackage)
		      throws JavaModelException {
			 
			String current = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
		    for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
		    	
		    	if(current.equals(unit.getElementName())){
		    		// now create the AST for the ICompilationUnits
		    		efdAnalyze(unit);
		    	}
		    }
		  }
		  

	/**
	 * Analyzes of the supplied compilation unit
	 * 
	 * @param compilationUnit
	 */
	protected void efdAnalyze(ICompilationUnit compilationUnit){
		try {
			@SuppressWarnings("static-access")
			int depth = compilationUnit.getUnderlyingResource().DEPTH_INFINITE;
			compilationUnit.getUnderlyingResource().deleteMarkers(null, false, depth);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		@SuppressWarnings("deprecation")
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(compilationUnit); // set source
		parser.setResolveBindings(true); // we need bindings later on
		// create a new compilation unit
		CompilationUnit comUnit = (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
		
		// create a new InitArrayDetector and process the compilation unit
		m_visitors = new Visitors();
		m_visitors.process(comUnit, compilationUnit);
		m_visitors.Init(m_countMethodMap);
		comUnit.accept(m_visitors);
	}
	
	private boolean isFileInProject(IProject pro){
		
		
		String current = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
		
		 IPackageFragment[] packages = null;
		try {
			packages = JavaCore.create(pro).getPackageFragments();
		} catch (JavaModelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		    // parse(JavaCore.create(project));
		    for (IPackageFragment mypackage : packages) {
		      try {
				if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
					  for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
						  if(current.equals(unit.getElementName())){
							  return true;
						  }
					  }
				  }
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    }
		
		return false;
	}

}
