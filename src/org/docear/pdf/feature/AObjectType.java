package org.docear.pdf.feature;

public abstract class AObjectType {
	private static int ordinals = 0;
	private final int id;
	public AObjectType() {
		id = ordinals++;
	}
	
	public int getOrdinal() {
		return id;
	}
	
	public boolean equals(Object o) {
		if(o instanceof AObjectType) {
			return (getOrdinal() == ((AObjectType) o).getOrdinal());
		}
		return super.equals(o);
	}
}
