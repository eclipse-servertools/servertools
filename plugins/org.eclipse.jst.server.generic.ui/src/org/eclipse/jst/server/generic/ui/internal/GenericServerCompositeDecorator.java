package org.eclipse.jst.server.generic.ui.internal;


/**
 * 
 * @author Gorkem Ercan
 */
public interface GenericServerCompositeDecorator 
{
	/**
	 * 
	 * @param composite
	 */
	public abstract void decorate(GenericServerComposite composite);
    /**
	 * Called if all the fields are valid. This gives subclasses opportunity to
	 * validate and take necessary actions.
	 * 
	 * @return
	 */
	public abstract boolean validate();
}
