
package markers_java;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;


public class DegreeMarker {
	//====================================== Final Public fields
	/**
	 * The name of the JDT Comments Marker
	 */
	public static String EFDMarker = null;
//	public final static String JDTCommentsMarker = "JDTCommentsV2.JDTCommentsMarkerV2";

//	public final static String JDTCommentsBookMark = "JDTCommentsV2.JDTCommentsMarkerV3";
	// ====================================== Private Data Members
	/*
	 * Hold reference to the marker
	 */
	private IResource m_resource;
	/**
	 * Holds id of created marker
	 */
	private long m_id;
	
	private static Position m_position;
	
	private ITextEditor m_editor;

	// ====================================== Constructor
	/**
	 * Default Constructor
	 * 
	 * @param compilationUnit
	 *            Reference to the wanted compilation unit
	 */
	public DegreeMarker(ICompilationUnit compilationUnit) {

		try {
			// Initialize the Creator with the required recourses from the
			// compilation unit
			m_resource = compilationUnit.getUnderlyingResource();

			m_editor = (ITextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		} catch (JavaModelException e) {
			// Error getting Resource from compilation unit
			e.printStackTrace();
		}
	}

	// ====================================== Public Methods
	/**
	 * Adds a marker in the file at the given position from and highlights the text from start to end
	 * @param start of text highlight
	 * @param lenght of text highlight
	 * @param commentType Type of message
	 */
	
	public void markDegree(int start, int length, int level)
	{
		if(level < 20){
			EFDMarker = "EFDMarker_1.Marker";
		}else if(level>=20 && level<=100){
			EFDMarker = "EFDMarker_2.Marker";
		}else if(level>100 && level<=500){
			EFDMarker = "EFDMarker_3.Marker";
		}else if(level>500 && level<=1500){
			EFDMarker = "EFDMarker_4.Marker";
		}else if(level>1500){
			EFDMarker = "EFDMarker_5.Marker";
		}
		// create a marker inside the compilation unit
		try {
			
			String comment = "EFD = " + level;
			IMarker marker = getMarker();
			m_position = new Position(start,length);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			marker.setAttribute(IMarker.CHAR_START, start);
			marker.setAttribute(IMarker.CHAR_END, start + length);
			marker.setAttribute(IMarker.LOCATION, m_resource.getName());
			marker.setAttribute(IMarker.MESSAGE, comment);
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			addAnnotation(marker, m_editor);
							      

		} catch (CoreException e) {
			// Error creating marker
			e.printStackTrace();
		}
	}

	// ====================================== Private Method

	/**
	 * The method looks if the marker has been already created under this
	 * resource if so then it uses it otherwise it creates a new one
	 */
	public IMarker getMarker() throws CoreException {
		// search for the marker if it was already created
		IMarker marker = m_resource.findMarker(m_id);

		if (marker != null) {
			return marker;
		}
		
		marker = m_resource.createMarker(EFDMarker);
		//m_id = marker.getId();
		return marker;
		
	}
	
	public static void addAnnotation(IMarker marker, ITextEditor editor) {
		//The DocumentProvider enables to get the document currently loaded in the editor
		IDocumentProvider idp = editor.getDocumentProvider();
		
		//This is the document we want to connect to. This is taken from 
		//the current editor input.
		IDocument document = idp.getDocument(editor.getEditorInput());
		
		//The IannotationModel enables to add/remove/change annotation to a Document 
		//loaded in an Editor
		IAnnotationModel iamf = idp.getAnnotationModel(editor.getEditorInput());
		
		//Note: The annotation type id specify that you want to create one of your 
		//annotations
		SimpleMarkerAnnotation ma = new SimpleMarkerAnnotation(EFDMarker + ".MyAnnotation" ,marker);
		
		
		
		
		//Finally add the new annotation to the model
		iamf.connect(document);
		iamf.addAnnotation(ma,m_position);
		
		/*CompositeRuler ruler= new CompositeRuler();
        ruler.setModel(iamf);

	    ruler.addDecorator(500, new AnnotationRulerColumn(100));*/
		
		///////////////////
		/*IVerticalRuler ruler = new VerticalRuler(6, null);
		
		AnnotationRulerColumn column1 = new AnnotationRulerColumn(100, new DefaultMarkerAnnotationAccess());
		ruler.setModel(iamf);
		
		CompositeRuler ruler2 = (CompositeRuler) ruler;
		column1.addAnnotationType(ma); 
	    ruler2.addDecorator(2, column1);*/
		///////////////
		iamf.disconnect(document);
	}
	

}
