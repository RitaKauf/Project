package efw_plugin.handlers;


import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElementVisitor;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;

import visitors_cpp.Visitorscpp;





/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class EFD_handler_cpp extends AbstractHandler {
	
	Visitorscpp m_visitors = null;
	//FuncDeclerationVisitor m_declVisitor = null;
	//FuncInvocationVisitor m_invcVisitor = null;
	HashMap<String, Integer> m_countMethodMap = null; 
	/**
	 * The constructor.
	 */
	public EFD_handler_cpp() {
	
	}
	
	//private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";

	
//	@SuppressWarnings("deprecation")
//	private void PreProcess1(FuncDeclerationVisitor visitor, IProject pro) {
//		
//		IPackageFragment[] packages = null;
//		try {
//			packages = JavaCore.create(pro).getPackageFragments();
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	    // parse(JavaCore.create(project));
//	    for (IPackageFragment mypackage : packages) {
//	      try {
//			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
//				  for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
//					  ASTParser parser = ASTParser.newParser(AST.JLS3);
//					  parser.setKind(ASTParser.K_COMPILATION_UNIT);
//					  parser.setSource(unit); // set source
//					  parser.setResolveBindings(true); // we need bindings later on
//						// create a new compilation unit
//					  CompilationUnit comUnit = (CompilationUnit) parser.createAST(null /* IProgressMonitor */); 
//					  comUnit.accept(visitor);
//						
//				  }
//			  }
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	    }
//	}
//	
//	@SuppressWarnings("deprecation")
//	private void PreProcess2(FuncInvocationVisitor visitor, IProject pro) {
//		
//		IPackageFragment[] packages = null;
//		try {
//			packages = JavaCore.create(pro).getPackageFragments();
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	    // parse(JavaCore.create(project));
//	    for (IPackageFragment mypackage : packages) {
//	      try {
//			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
//				  for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
//					  ASTParser parser = ASTParser.newParser(AST.JLS3);
//					  parser.setKind(ASTParser.K_COMPILATION_UNIT);
//					  parser.setSource(unit); // set source
//					  parser.setResolveBindings(true); // we need bindings later on
//						// create a new compilation unit
//					  CompilationUnit comUnit = (CompilationUnit) parser.createAST(null /* IProgressMonitor */); 
//					  comUnit.accept(visitor);
//						
//				  }
//			  }
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	    }
//	}
	
	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ICProject tmpProjects[] = null;
		
			try {
				tmpProjects = CCorePlugin.getDefault().getCoreModel().getCModel().getCProjects();
			} catch (CModelException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//			ICommandService service = (ICommandService) window.getService(ICommandService.class);
//		IWorkspace workspace = ResourcesPlugin.getWorkspace();
//	    IWorkspaceRoot root = workspace.getRoot();
//	    // Get all projects in the workspace
//	    ICProject[] projects = root.getgetProjects();
	    // Loop over all projects
			for (ICProject project : tmpProjects) {
		     
		        //**********CHANGE*****************	
		    	  try {
					if (isFileInProject(project)) {
						//project.((PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getClass().get);
							analyseMethods(project);
						}
				} catch (CModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		          
		        
			}
	    
	   
	    m_countMethodMap.clear();
	    
		return null;
	}
	
	  private void analyseMethods(ICProject project) throws CoreException {
		  	
		    m_countMethodMap = new HashMap<String, Integer>();
//		  	m_declVisitor = new FuncDeclerationVisitor();
//		  	m_invcVisitor = new FuncInvocationVisitor();
//		  	m_declVisitor.Init(m_countMethodMap);
//		  	
//		  	//PreProcess1(m_declVisitor, project);
//		  	
//		  	m_invcVisitor.Init(m_countMethodMap);
		  	
		  	//PreProcess2(m_invcVisitor, project);
		  	
		  	ISourceRoot[] tmpSRoots = project.getAllSourceRoots();
			for (ISourceRoot sr : tmpSRoots)
			{
				ITranslationUnit[] tmpTUs = sr.getTranslationUnits();
				for (ITranslationUnit tu : tmpTUs){
					createAST(tu);
				}
			}

		    
		  }

		  private void createAST(ITranslationUnit myfile) throws CoreException{
			 
			String current = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
		    	
		    	if(current.equals(myfile.getElementName())){
		    		// now create the AST for the ICompilationUnits
		    		efdAnalyze(myfile);
		    	}
		    
		  }

	protected void efdAnalyze(ITranslationUnit translationUnit) throws CoreException{
		
			int depth = translationUnit.getUnderlyingResource().DEPTH_INFINITE;
			translationUnit.getUnderlyingResource().deleteMarkers(null, false, depth);
		
//		@SuppressWarnings("deprecation")
//		ASTParser parser = ASTParser.newParser(AST.JLS3);
//		parser.setKind(ASTParser.K_COMPILATION_UNIT);
//		parser.setSource(compilationUnit); // set source
//		parser.setResolveBindings(true); // we need bindings later on
//		// create a new compilation unit
//		CompilationUnit comUnit = (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
		
		// create a new InitArrayDetector and process the compilation unit
		m_visitors = new Visitorscpp();
		m_visitors.process(translationUnit);
		m_visitors.Init(m_countMethodMap);
		IASTTranslationUnit unit = translationUnit.getAST();
		
		 m_visitors.toVisitNodes();
		
		unit.accept(m_visitors);
	}
	
	private boolean isFileInProject(ICProject pro) throws CModelException{
		
		
		String current = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
		
		ISourceRoot[] tmpSRoots = pro.getAllSourceRoots();
		for (ISourceRoot sr : tmpSRoots)
		{
			ITranslationUnit[] tmpTUs = sr.getTranslationUnits();
			for (ITranslationUnit tu : tmpTUs){
				if(current.equals(tu.getElementName())){
					return true;
				}
			}
		}
		
		
//		 IPackageFragment[] packages = null;
//		try {
//			packages = JavaCore.create(pro).getPackageFragments();
//		} catch (JavaModelException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		    // parse(JavaCore.create(project));
//		    for (IPackageFragment mypackage : packages) {
//		      try {
//				if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
//					  for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
//						  if(current.equals(unit.getElementName())){
//							  return true;
//						  }
//					  }
//				  }
//			} catch (JavaModelException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		    }
		
		return false;
	}

}
