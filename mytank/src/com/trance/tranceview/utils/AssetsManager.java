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

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.trance.trancetank.modules.army.model.ArmyType;
import com.trance.tranceview.constant.BulletType;
import com.trance.tranceview.constant.ControlType;

public class AssetsManager extends AssetManager{
	
	public TextureAtlas textureAtlas;
	public TextureAtlas textureAtlas2;
	private static AssetsManager assetsManager;
	public static AssetsManager getInstance(){
		if(assetsManager == null){
			assetsManager = new AssetsManager();
		}
		return assetsManager;
	}
	
    public void init() {
//    	Texture.setEnforcePotImages(false);//模拟器调试必须加上
    	load("block/block.pack", TextureAtlas.class);
    	load("blocks/pic.pack", TextureAtlas.class);
    	load("ui/bullet.png", Texture.class);
    	load("ui/attack.png", Texture.class);
    	load("ui/to_world.png", Texture.class);
    	load("ui/to_home.png", Texture.class);
    	load("ui/gotofight.png", Texture.class);
    	
    	load("world/me1.png", Texture.class);
    	load("world/tips.png", Texture.class);
    	load("world/f-28.png", Texture.class);
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
    	
//    	ininSound();
    }
    
    /**
     * 初始化声音资源
     */
    private void ininSound() {
    	load("audio/barrett.wav",Sound.class);
    	load("audio/begin.mp3",Music.class);
    	load("audio/game_over.mp3",Music.class);
    	load("audio/get_bomber.mp3",Music.class);
    	load("audio/get_barrett.mp3",Music.class);
		
	}
    
	public TextureRegion getBlockTextureRegion(int value) {
    	if(textureAtlas == null){
    		textureAtlas = this.get("block/block.pack", TextureAtlas.class);
    	}
    	AtlasRegion atlasRegion = textureAtlas.findRegion("b", value);
        return atlasRegion;
    }
	
	public TextureRegion getBuildingTextureRegion(int value) {
		if(textureAtlas2 == null){
			textureAtlas2 = this.get("blocks/pic.pack", TextureAtlas.class);
		}
		AtlasRegion atlasRegion = textureAtlas2.findRegion("f-"+value);
		return atlasRegion;
	}
	
	public TextureRegion getArmyTextureRegion(int armyId) {
		int test = 6;
		if(armyId == ArmyType.TANK){
			test = 7;
		}
		return getBlockTextureRegion(test);
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
//    	Texture dirs = get("ui/controls.png", Texture.class);
//    	TextureRegion[] regions = TextureRegion.split(dirs, 64 ,64 )[0];
    	
    	Texture texture = null;
    	String fileName = null;
    	switch(value){
    	case GOTOFIGHT:
    		fileName = "ui/gotofight.png";
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
			fileName ="ui/dir.png"; 
			texture = get(fileName, Texture.class);
			break;
    	}
        return texture;
    }

    public void dispose() {
    	super.dispose();
    	if(textureAtlas != null){
    		textureAtlas.dispose();
    	}
    	if(textureAtlas2 != null){
    		textureAtlas2.dispose();
    	}
    }


}
