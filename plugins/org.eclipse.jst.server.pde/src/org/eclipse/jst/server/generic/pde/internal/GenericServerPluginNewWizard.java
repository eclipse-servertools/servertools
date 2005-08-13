package org.eclipse.jst.server.generic.pde.internal;


import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;
/**
 * New Generic server plug-in wizard. 
 * @author Gorkem Ercan
 *
 */
public class GenericServerPluginNewWizard extends NewPluginTemplateWizard {

	public ITemplateSection[] createTemplateSections(){
		return new ITemplateSection[] {new GenericServerTemplate()};
	}
	
	public void init(IFieldData data) {
		super.init(data);
		setWindowTitle(Messages.windowTitleWizard);
	}
}
