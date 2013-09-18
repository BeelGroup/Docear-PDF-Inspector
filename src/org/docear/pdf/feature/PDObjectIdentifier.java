package org.docear.pdf.feature;

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.pd.PDAnnotation;
import de.intarsys.pdf.pd.PDObject;

public class PDObjectIdentifier extends PDObject {

	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDAnnotation.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDObjectIdentifier(object);
		}
	}

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());
	

	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/
	
	protected PDObjectIdentifier(COSObject object) {
		super(object);
	}
	
	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/
	
	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
}
