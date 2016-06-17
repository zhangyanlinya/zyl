package com.trance.tranceview.constant;

public enum BulletType {

	COMMON(1);

	private final int value;

	private BulletType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static BulletType valueOf(int value) {
		for (BulletType type : BulletType.values()) {
			if (type.value == value) {
				return type;
			}
		}
		return null;
	}
}
