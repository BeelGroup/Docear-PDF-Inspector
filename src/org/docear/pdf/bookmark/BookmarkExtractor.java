package org.docear.pdf.bookmark;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.docear.pdf.feature.APDMetaObject;
import org.docear.pdf.feature.CachedPDMetaObjectExtractor;
import org.docear.pdf.feature.AObjectType;
import org.docear.pdf.feature.PageDestination;
import org.docear.pdf.feature.UriDestination;

import de.intarsys.pdf.cds.CDSNameTreeEntry;
import de.intarsys.pdf.cds.CDSNameTreeNode;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSCatalog;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNull;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSRuntimeException;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDExplicitDestination;
import de.intarsys.pdf.pd.PDOutline;
import de.intarsys.pdf.pd.PDOutlineItem;
import de.intarsys.pdf.pd.PDOutlineNode;
import de.intarsys.pdf.pd.PDPage;

public class BookmarkExtractor extends CachedPDMetaObjectExtractor {
	

	

	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/

	public BookmarkExtractor(PDDocument document) {
		super(document);
	}

	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/

	public void getMetaObjects(List<APDMetaObject> bookmarkList) throws IOException {
		getBookmarks(bookmarkList);
	}

	private List<APDMetaObject> getBookmarks(List<APDMetaObject> bookmarkList) throws IOException {
		if (bookmarkList == null) {
			bookmarkList = new ArrayList<APDMetaObject>();
		}
		try {
			PDOutlineNode outline;
			try {
				outline = getDocument().getOutline();
			}
			catch (ClassCastException ex) {
				outline = (PDOutlineNode)PDOutline.META.createFromCos(getDocument().getCatalog().cosGetOutline());
			}
			getBookmarks(outline, bookmarkList);
		} catch (Exception e) {
			throw new IOException(e);
		}
		return bookmarkList;
	}

	private void getBookmarks(PDOutlineNode parent, List<APDMetaObject> bookmarks) throws IOException, COSLoadException, COSRuntimeException {
		if (parent == null) {
			return;
		}
		@SuppressWarnings("unchecked")
		List<PDOutlineItem> children = parent.getChildren();
		for (PDOutlineItem child : children) {
			Bookmark bm = new Bookmark(getOrCreateUID(child));
			setBookmarkDestination(bm, child);
			int objectNumber = child.cosGetObject().getIndirectObject().getObjectNumber();
			bm.setObjectNumber(objectNumber);
			bm.setText(child.getTitle());
			
			if (keepObjectReference()) {
				bm.setObjectReference(child);
			}
			if(child.getChildren().size() > 0) {
				getBookmarks(child, bm.getChildren());
			}

			bookmarks.add(bm);
		}
	}

	private void setBookmarkDestination(Bookmark bookmark, PDOutlineItem item) {
		AObjectType type = null;
		if (item.cosGetField(PDOutlineItem.DK_A) instanceof COSNull) {
			Integer page = null;
			try {
				page = getBookmarkDestinationPage(item);
			} catch (Exception e) {
			}
			if (page == null) {
				type = Bookmark.BOOKMARK_WITHOUT_DESTINATION;
			}
			else {
				bookmark.setDestination(new PageDestination(page));
			}
			
		}
		else {
			URI uri = getBookmarkDestinationUri(item);
			if (uri != null) {
				type = Bookmark.BOOKMARK_WITH_URI;
				bookmark.setDestination(new UriDestination(uri));
			}
		}
		if(type == null) {
			type = Bookmark.BOOKMARK;
		}
		bookmark.setType(type);
	}

	private Integer getBookmarkDestinationPage(PDOutlineItem item) throws IOException, COSLoadException {
		if (item == null) {
			return null;
		}

		if (item.getDestination() != null) {
			PDExplicitDestination destination = item.getDestination().getResolvedDestination(getDocument());
			if (destination != null) {
				PDPage page = destination.getPage(getDocument());
				return page.getNodeIndex() + 1;
			}
		}
		if (!(item.cosGetField(PDOutlineItem.DK_A) instanceof COSNull)) {

			COSDictionary cosDictionary = (COSDictionary) item.cosGetField(PDOutlineItem.DK_A);
			COSArray destination = getCOSArrayFromDestination(cosDictionary);

			return getPageFromCOSArray((COSArray) destination);
		}

		return null;
	}

	private COSArray getCOSArrayFromDestination(COSDictionary cosDictionary) {
		COSObject cosObject = cosDictionary.get(COSName.create("D")); //$NON-NLS-1$
		if (cosObject instanceof COSArray) {
			return (COSArray) cosObject;
		}
		if (cosObject instanceof COSString) {
			String destinationName = cosObject.getValueString(null);
			if (destinationName == null || destinationName.length() <= 0) {
				return null;
			}

			COSDictionary dests = cosDictionary.getDoc().getCatalog().cosGetDests();
			if (dests != null) {
				for (Iterator<?> i = dests.keySet().iterator(); i.hasNext();) {
					COSName key = (COSName) i.next();
					if (key.stringValue().equals(destinationName)) {
						cosDictionary = (COSDictionary) dests.get(key);
						cosObject = cosDictionary.get(COSName.create("D")); //$NON-NLS-1$
						if (cosObject instanceof COSArray) {
							return (COSArray) cosObject;
						}
					}
				}
			}

			COSDictionary names = cosDictionary.getDoc().getCatalog().cosGetNames();
			if (names != null) {
				COSDictionary destsDict = names.get(COSCatalog.DK_Dests).asDictionary();
				if (destsDict != null) {
					CDSNameTreeNode destsTree = CDSNameTreeNode.createFromCos(destsDict);
					for (Iterator<?> i = destsTree.iterator(); i.hasNext();) {
						CDSNameTreeEntry entry = (CDSNameTreeEntry) i.next();
						if (entry.getName().stringValue().equals(destinationName)) {
							if (entry.getValue() instanceof COSDictionary) {
								cosDictionary = (COSDictionary) entry.getValue();
								cosObject = cosDictionary.get(COSName.create("D")); //$NON-NLS-1$
								if (cosObject instanceof COSArray) {
									return (COSArray) cosObject;
								}
							} else if (entry.getValue() instanceof COSArray) {
								return (COSArray) entry.getValue();
							}
						}
					}
				}
			}

		}
		return null;
	}

	private Integer getPageFromCOSArray(COSArray destination) {
		// DOCEAR: fallback if no entry was found
		if (destination == null) {
			return 1;
		}
		Iterator<?> it = destination.iterator();
		while (it.hasNext()) {
			COSObject o = (COSObject) it.next();
			if (o.isIndirect()) { // the page is indirect referenced
				o.dereference();
			}
			PDPage page = getDocument().getPageTree().getFirstPage();
			while (page != null) {
				if (page.cosGetObject().equals(o)) {
					return page.getNodeIndex() + 1;
				}
				page = page.getNextPage();
			}
		}
		return null;
	}
	
	private URI getBookmarkDestinationUri(PDOutlineItem item) {
		if (!(item.cosGetField(PDOutlineItem.DK_A) instanceof COSNull)) {
			COSDictionary cosDictionary = (COSDictionary) item.cosGetField(PDOutlineItem.DK_A);
			COSObject destination = cosDictionary.get(COSName.create("URI"));
			if (!(destination instanceof COSNull)) {
				if (destination instanceof COSString && destination.getValueString(null) != null && destination.getValueString(null).length() > 0) {
					try {
						return new URI(destination.getValueString(null));
					} catch (URISyntaxException e) {
						System.out.println("Bookmark Destination Uri Syntax incorrect." + e.getMessage());
					}
				}
			}
		}
		return null;
	}
	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
}
