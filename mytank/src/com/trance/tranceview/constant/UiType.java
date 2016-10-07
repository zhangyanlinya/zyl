package com.trance.tranceview.constant;

public enum UiType {
	
	LEVEL("ui/level.png"),
	FOODS("ui/foods.png"),
	GOLD("ui/gold.png"),
	SILVER("ui/silver.png"),
	ITEMBOX("ui/itembox.png"),
	CHANGE("ui/change.png"),
	;
	
	private final String value;
	
	private UiType(String value){
		this.value = value;
	}
	
	public String getVlaue(){
		return value;
	}
}
