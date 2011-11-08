package com.laazy.simplejms.messages;

import org.codehaus.jackson.annotate.JsonProperty;

public class XYZ {
	private final int x;
	private final int y;
	private final int z;

	XYZ(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("z") int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	@Override
	public String toString() {
		return "XYZ{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
	}
}
