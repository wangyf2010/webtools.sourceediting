/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.parser.regions;

import org.eclipse.wst.sse.core.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.util.Debug;
import org.eclipse.wst.sse.core.util.Utilities;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;



public class XMLContentRegion implements ITextRegion {
	static private final String fType = XMLRegionContext.XML_CONTENT;
	// length and textLength are always the same for content region
	//private int fTextLength;
	private int fLength;
	private int fStart;


	public XMLContentRegion() {
		super();
	}

	public XMLContentRegion(int start, int length) {
		this();
		fStart = start;
		fLength = length;
	}


	public void adjustLength(int i) {
		fLength += i;

	}

	public void adjustStart(int i) {
		fStart += i;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#adjustTextLength(int)
	 */
	public void adjustTextLength(int i) {
		// not supported

	}

	public void equatePositions(ITextRegion region) {
		fStart = region.getStart();
		fLength = region.getLength();
	}

	public int getEnd() {
		return fStart + fLength;
	}

	public int getLength() {
		return fLength;
	}

	public int getStart() {
		return fStart;
	}

	public int getTextEnd() {
		return fStart + fLength;
	}

	public int getTextLength() {
		return fLength;
	}

	public String getType() {
		return fType;
	}

	public String toString() {
		return RegionToStringUtil.toString(this);
	}


	public StructuredDocumentEvent updateRegion(Object requester, IStructuredDocumentRegion parent, String changes, int requestStart, int lengthToReplace) {
		// TODO: this is a pretty lame method, since XML Content can have a
		// much
		// better rule for region update, but this is what previous
		// (unfactored)
		// version had, so I'll carry it over, of now.
		RegionChangedEvent result = null;
		// if the region is an easy type (e.g. attribute value),
		// and the requested changes are all
		// alphanumeric, then make the change here locally.
		// (This can obviously be made more sophisticated as the need arises,
		// but should
		// always follow this pattern.)
		if (Debug.debugStructuredDocument) {
			System.out.println("\t\tContextRegion::updateModel"); //$NON-NLS-1$
			System.out.println("\t\t\tregion type is " + fType); //$NON-NLS-1$
		}
		boolean canHandle = false;
		// note: we'll always handle deletes from these
		// regions ... if its already that region,
		// deleting something from it won't change its
		// type. (remember, the calling program needs
		// to insure we are not called, if not all contained
		// on one region.
		if ((changes == null) || (changes.length() == 0)) {
			// delete case
			// We can not do the quick delete, if
			// if all the text in a region is to be deleted.
			// Or, if the delete starts in the white space region.
			// In these cases, a reparse is needed.
			// Minor note, we use textEnd-start since it always
			// less than or equal to end-start. This might
			// cause us to miss a few cases we could have handled,
			// but will prevent us from trying to handle funning cases
			// involving whitespace.
			if ((fStart >= getTextEnd()) || (Math.abs(lengthToReplace) >= getTextEnd() - getStart())) {
				canHandle = false;
			} else {
				canHandle = true;
			}
		} else {
			if ((RegionUpdateRule.canHandleAsWhiteSpace(this, parent, changes, requestStart, lengthToReplace)) || RegionUpdateRule.canHandleAsLetterOrDigit(this, parent, changes, requestStart, lengthToReplace)) {
				canHandle = true;
			} else {
				canHandle = false;
			}

		}
		if (canHandle) {
			// at this point, we still have the old region. We won't create a
			// new instance, we'll just update the one we have, by changing
			// its end postion,
			// The parent flatnode, upon return, has responsibility
			// for updating sibling regions.
			// and in turn, the structuredDocument itself has responsibility
			// for
			// updating the text store and down stream flatnodes.
			if (Debug.debugStructuredDocument) {
				System.out.println("change handled by region"); //$NON-NLS-1$
			}
			int lengthDifference = Utilities.calculateLengthDifference(changes, lengthToReplace);
			// Note: we adjust both end and text end, because for any change
			// that is in only the trailing whitespace region, we should not
			// do a quick change,
			// so 'canHandle' should have been false for those case.
			fLength += lengthDifference;
			// TO_DO_FUTURE: cache value of canHandleAsWhiteSpace from above
			// If we are handling as whitespace, there is no need to increase
			// the text length, only
			// the total length is changing.
			// don't need for content region
			//			if (!RegionUpdateRule.canHandleAsWhiteSpace(this, changes,
			// fStart, lengthToReplace)) {
			//				fTextLength += lengthDifference;
			//			}
			result = new RegionChangedEvent(parent.getParentDocument(), requester, parent, this, changes, requestStart, lengthToReplace);
		}

		return result;
	}

}
