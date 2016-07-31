package com.trance.tranceview.utils;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;

public class FontUtil {
	
	private static FontUtil fontUtil;
	private FreeTypeFontGenerator generator;
	private Set<String> set = new HashSet<String>();
	
	public static FontUtil getInstance(){
		if(fontUtil == null){
			fontUtil = new FontUtil();
		}
		return fontUtil;
	}
	
	/**
	 * get BitmapFont from config
	 * 
	 * @param size    font size
	 * @param append  追加String 
	 * @param color   font color
	 * @return
	 */
	public BitmapFont getFont(int size, String append, Color color){
		set.clear();
		for(int i = 0; i < append.length(); i++){
			char c = append.charAt(i);
			if(CharUtil.isChinese(c)){
				set.add(String.valueOf(c));
			}
		}
		StringBuilder sb = new StringBuilder(FreeTypeFontGenerator.DEFAULT_CHARS);
		for(String s : set){
			sb.append(s);
		}
		generator = new FreeTypeFontGenerator(
	               Gdx.files.internal("font/font.ttf"));
		FreeTypeBitmapFontData fontData = generator.generateData(size,
	              sb.toString(), false);
		generator.dispose();
		BitmapFont font = new BitmapFont(fontData, fontData.getTextureRegions(), false);
		font.setColor(color);
		return font;
	}
}
