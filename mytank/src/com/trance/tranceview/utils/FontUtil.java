package com.trance.tranceview.utils;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;

public class FontUtil {
	
	private static BitmapFont font;
	private static FreeTypeFontGenerator generator;
	private final static Set<String> set = new HashSet<String>();
	
	public static BitmapFont getSingleFont(){
		if(font == null){
			font = getFont();
		}
		return font;
	}
	
	/**
	 * get BitmapFont from config
	 * 
	 * @param size    font size
	 * @param append  追加String 
	 * @param color   font color
	 * @return
	 */
	public static BitmapFont getFont(int size, String append, Color color){
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
	
	public static BitmapFont getFont(){
		BitmapFont font = getFont(25);
		font.setColor(Color.WHITE);
		return font;
	}
	
	public BitmapFont getFont(int size, Color color){
		BitmapFont font = getFont(size);
		font.setColor(color);
		return font;
	}
	
	public static BitmapFont getFont(int size){
		generator = new FreeTypeFontGenerator(
				Gdx.files.internal("font/font.ttf"));
		FreeTypeBitmapFontData fontData = generator.generateData(size,
				FreeTypeFontGenerator.DEFAULT_CHARS, false);
		generator.dispose();
		BitmapFont font = new BitmapFont(fontData, fontData.getTextureRegions(), false);
		return font;
	}
	
	public static void dispose(){
		if(font != null){
			font.dispose();
		}
	}
}
