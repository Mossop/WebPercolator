package com.blueprintit.webpercolator;

/**
 * This final class implements a type-safe enumeration
 * over the valid instances of a LinkType.
 * Instances of this class are immutable.
 * 
 * @author Dave
 * @version 1.0
 */
public final class LinkType {

	/** 
	 * LinkType with Value = 0
	 */
	public static final LinkType UNSPECIFIED = new LinkType(0);

	/** 
	 * LinkType with Value = 1
	 */
	public static final LinkType LINK = new LinkType(1);

	/** 
	 * LinkType with Value = 2
	 */
	public static final LinkType IMAGE = new LinkType(2);

	/** 
	 * LinkType with Value = 3
	 */
	public static final LinkType FRAME = new LinkType(3);

	/** 
	 * LinkType with Value = 4
	 */
	public static final LinkType OBJECT = new LinkType(4);

	/** 
	 * LinkType with Value = 5
	 */
	public static final LinkType APPLET = new LinkType(5);

	private static final LinkType[] ALL = new LinkType[] {
		UNSPECIFIED,
		LINK,
		IMAGE,
		FRAME,
		OBJECT,
		APPLET,
	};
	
	private final int mValue;

	/**
	 * Private constructor.
	 */
	private LinkType(int value) {
		mValue = value;
	}

	/**
	 * Returns a copy of the array with all instances of this class.
	 * <p>
	 * Modifying the returned array will not affect this class.
	 * 
	 * @return an array with all instances of this class
	 */
	public static LinkType[] all() {

		LinkType[] result = new LinkType[ALL.length];
		System.arraycopy(ALL, 0, result, 0, ALL.length);
		return result;
	}
	
	/**
	 * Returns the <code>LinkType</code> for the specified key field(s),
	 * or returns <code>UNSPECIFIED</code>
	 * if no <code>LinkType</code> exists for the specified key field(s).
	 * 
	 * @param value the value of the <code>LinkType</code> to find
	 * @return the <code>LinkType</code> for the specified key field(s)
	 */
	public static LinkType lookup(int value) {
		for (int i = 0; i < ALL.length; i++) {
			if (ALL[i].getValue() != value) {
				continue;
			}
			return ALL[i];
		}
		return UNSPECIFIED;
	}
	
	/**
	 * Returns whether this instance is the {@link LinkType#UNSPECIFIED} instance.
	 * 
	 * @return whether this instance is the {@link LinkType#UNSPECIFIED} instance
	 */
	public boolean isUnspecified() {
		return this == LinkType.UNSPECIFIED; 
	}
	/**
	 * Returns whether this instance is the {@link LinkType#LINK} instance.
	 * 
	 * @return whether this instance is the {@link LinkType#LINK} instance
	 */
	public boolean isLink() {
		return this == LinkType.LINK; 
	}
	/**
	 * Returns whether this instance is the {@link LinkType#IMAGE} instance.
	 * 
	 * @return whether this instance is the {@link LinkType#IMAGE} instance
	 */
	public boolean isImage() {
		return this == LinkType.IMAGE; 
	}
	/**
	 * Returns whether this instance is the {@link LinkType#FRAME} instance.
	 * 
	 * @return whether this instance is the {@link LinkType#FRAME} instance
	 */
	public boolean isFrame() {
		return this == LinkType.FRAME; 
	}
	/**
	 * Returns whether this instance is the {@link LinkType#OBJECT} instance.
	 * 
	 * @return whether this instance is the {@link LinkType#OBJECT} instance
	 */
	public boolean isObject() {
		return this == LinkType.OBJECT; 
	}
	/**
	 * Returns whether this instance is the {@link LinkType#APPLET} instance.
	 * 
	 * @return whether this instance is the {@link LinkType#APPLET} instance
	 */
	public boolean isApplet() {
		return this == LinkType.APPLET; 
	}
	
	/**
	 * Returns the value.
	 *
	 * @return the value. 
	 */
	public int getValue() {
		return mValue;
	}

	/**
	 * Returns a String description of this <code>LinkType</code>.
	 *
	 * @return a String description of this object.
	 */
	public String toString() {

		StringBuffer result = new StringBuffer();
		result.append("LinkType[");
		result.append("value=").append(getValue());
		result.append("]");
		return result.toString();
	}
	
	/*
	 * Non-javadoc.
	 * @see java.lang.Object#equals(Object)
	 */
	// Algorithm from "Effective Java" by Joshua Bloch.
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof LinkType)) {
			return false;
		}
		LinkType other = (LinkType) o;
		return true
			&& getValue() == other.getValue()
			;
	}
	
	/*
	 * Non-javadoc.
	 * @see java.lang.Object#hashCode()
	 */
	// Algorithm from "Effective Java" by Joshua Bloch.
	public int hashCode() {
		int result = 17 * getClass().getName().hashCode();
		result = 37 * result + getValue();
		return result;
	}

	/**
	 * If this class implements <code>java.io.Serializable</code>,
	 * the Java serialization mechanism provides a "hidden constructor".
	 * To ensure that no other instances are created than the
	 * ones declared above, we implement <code>readResolve</code>.
	 * (This is not necessary if this class does not
	 * implement <code>java.io.Serializable</code>).
	 */
	// Algorithm from "Effective Java" by Joshua Bloch.
	private Object readResolve() throws java.io.ObjectStreamException {
		
		// look at the key attribute values of the instance 
		// that was just deserialized,
		// and replace the deserialized instance 
		// with one of the static objects
		return lookup(mValue); 
	}
}
