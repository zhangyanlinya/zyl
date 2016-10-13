/*
 * Copyright (c) 2014. William Mora
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trance.tranceview.utils;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.trance.trancetank.modules.army.model.ArmyType;
import com.trance.tranceview.constant.BulletType;
import com.trance.tranceview.constant.ControlType;
import com.trance.tranceview.constant.UiType;

@SuppressLint("UseSparseArrays") 
public class ResUtil extends AssetManager{
	
	public TextureAtlas textureAtlas;
	public TextureAtlas textureAtlas2;
	private static ResUtil resUtil;
	public static ResUtil getInstance(){
		if(resUtil == null){
			resUtil = new ResUtil();
		}
		return resUtil;
	}
	
    public void init() { 
//    	Texture.setEnforcePotImages(false);//模拟器调试必须加上
    	load("building/1.png", Texture.class);
    	load("building/2.png", Texture.class);
    	load("building/3.png", Texture.class);
    	load("building/4.png", Texture.class);
    	load("building/5.png", Texture.class);
    	load("building/6.png", Texture.class);
    	load("building/7.png", Texture.class);
    	load("building/8.png", Texture.class);
    	load("building/9.png", Texture.class);
    	load("ui/bullet.png", Texture.class);
    	load("ui/attack.png", Texture.class);
    	load("ui/to_world.png", Texture.class);
    	load("ui/to_home.png", Texture.class);
    	load("ui/rename.png", Texture.class);
    	
    	//UI
       	load("ui/level.png", Texture.class);
       	load("ui/gold.png", Texture.class);
       	load("ui/foods.png", Texture.class);
       	load("ui/silver.png", Texture.class);
       	load("ui/itembox.png", Texture.class);
       	load("ui/change.png", Texture.class);
       	load("ui/train.png", Texture.class);
       	load("ui/blank.png", Texture.class);
       	load("ui/close.png", Texture.class);
       	load("ui/levelup.png", Texture.class);
       	load("ui/upbuilding.png", Texture.class);
    	
    	load("world/me1.png", Texture.class);
    	load("world/me2.png", Texture.class);
    	load("world/me3.png", Texture.class);
    	load("world/tips.png", Texture.class);
    	load("world/bg.jpg", Texture.class);
    	
    	load("world/tree1.png", Texture.class);
    	load("world/tree2.png", Texture.class);
    	load("world/tree3.png", Texture.class);
    	load("world/tree4.png", Texture.class);
    	load("world/tree5.png", Texture.class);
    	
    	load("world/grass1.png", Texture.class);
    	load("world/grass2.png", Texture.class);
    	load("world/grass3.png", Texture.class);
    	load("world/grass4.png", Texture.class);
    	load("world/grass5.png", Texture.class);
    	
    	load("world/soil1.png", Texture.class);
    	load("world/soil2.png", Texture.class);
    	load("world/soil3.png", Texture.class);
    	load("world/soil4.png", Texture.class);
    	
    	load("world/stone1.png", Texture.class);
    	load("world/stone2.png", Texture.class);
    	
    	initAnimation();
    	
//    	ininSound();
    }
    
    /**
     * 获得UI
     * @param UiType
     * @return
     */
    public Texture getUi(UiType uiType){
    	return this.get(uiType.getVlaue(),Texture.class);
    }
    
    private void initAnimation() {
    	for(ArmyType type :ArmyType.values()){
    	 for (int i = 0; i < 6; i++) {
    		 load("army/"+type.id + "/zoulu/" + i + ".png", Texture.class);
         }
    	}
	}

//	/**
//     */
//    private void ininSound() {
//    	load("audio/barrett.wav",Sound.class);
//    	load("audio/begin.mp3",Music.class);
//    	load("audio/game_over.mp3",Music.class);
//    	load("audio/get_bomber.mp3",Music.class);
//    	load("audio/get_barrett.mp3",Music.class);
//		
//	}
	
	public TextureRegion getBuildingTextureRegion(int value) {
		return new TextureRegion(getBuildingTexture(value));
	}
	
	public Texture getBuildingTexture(int value) {
		return this.get("building/"+value+".png",Texture.class);
	}
	
	public TextureRegion getArmyTextureRegion(int armyId) {
		return new TextureRegion(this.get("army/"+armyId+"/zoulu/0.png",Texture.class));
	}
	
	private Map<Integer, TextureRegion[]> armyAnimations = new HashMap<Integer, TextureRegion[]>();
	
	public TextureRegion[] getArmyAnimation(int armyId) {
		TextureRegion[] regions = armyAnimations.get(armyId);
		if(regions != null && regions.length > 0){
			return regions;
		}
		
		regions = new TextureRegion[6];
        //把Texture转换下
        for (int i = 0; i < 6; i++) {
        	Texture animation = this.get("army/"+armyId+"/zoulu/"+i+".png", Texture.class);
        	regions[i] = new TextureRegion(animation);
        }
        armyAnimations.put(armyId, regions);
		return regions;
	}
    
    public  TextureRegion getBulletTextureRegion(int value) {
    	String fileName ="ui/bullet.png";
    	if(value == BulletType.COMMON.getValue()){
    		fileName = "ui/bullet.png";
    	}
    	
    	Texture texture = get(fileName, Texture.class);
    	TextureRegion textureRegion = new TextureRegion(texture);
        return textureRegion;
    }
    
    public Texture getControlTextureRegion(ControlType value) {
    	Texture texture = null;
    	String fileName = null;
    	switch(value){
    	case RENAME:
    		fileName = "ui/rename.png";
    		texture = get(fileName, Texture.class);
    		break;
    	case ATTACK:
    		fileName = "ui/attack.png";
    		texture = get(fileName, Texture.class);
    		break;
    	case WORLD:
    		fileName = "ui/to_world.png";
    		texture = get(fileName, Texture.class);
    		break;
    	case HOME:
    		fileName = "ui/to_home.png";
    		texture = get(fileName, Texture.class);
    		break;
		default:
			break;
    	}
        return texture;
    }

    public void dispose() {
    	super.dispose();
    	armyAnimations.clear();
    }
}
