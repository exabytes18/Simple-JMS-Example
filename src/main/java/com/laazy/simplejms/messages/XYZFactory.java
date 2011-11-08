package com.laazy.simplejms.messages;

public final class XYZFactory {
	private XYZFactory() {
	}

	public static XYZ createMessage(int aX, int aY, int aZ) {
		return new XYZ(aX, aY, aZ);
	}
}
