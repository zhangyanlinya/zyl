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

import android.annotation.SuppressLint;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.trance.tranceview.constant.BulletType;
import com.trance.tranceview.constant.ControlType;

@SuppressLint("UseSparseArrays")
public class AssetsManager{
	public static AssetManager assetManager = new AssetManager();
	public static TextureAtlas textureAtlas;

    public void init() {
//    	Texture.setEnforcePotImages(false);//模拟器调试必须加上
    	assetManager.load("block/block.pack", TextureAtlas.class);
    	assetManager.load("ui/dir.png", Texture.class);
    	assetManager.load("ui/bullet.png", Texture.class);
//    	assetManager.load("ui/fire.png", Texture.class);
    	assetManager.load("ui/attack.png", Texture.class);
    	assetManager.load("ui/to_world.png", Texture.class);
    	assetManager.load("ui/to_home.png", Texture.class);
    	assetManager.load("ui/up.png", Texture.class);
    	assetManager.load("ui/controls.png", Texture.class);
    	assetManager.load("ui/gotofight.png", Texture.class);
//    	assetManager.load("ui/start.jpg", Texture.class);
    	
    	assetManager.load("world/me.png", Texture.class);
    	assetManager.load("world/enemy.png", Texture.class);
    	assetManager.load("world/tips.png", Texture.class);
    	assetManager.load("world/f-28.png", Texture.class);
    	
    	ininSound();
    	
		assetManager.finishLoading();
    }
    
    /**
     * 初始化声音资源
     */
    private void ininSound() {
    	assetManager.load("audio/barrett.wav",Sound.class);
    	assetManager.load("audio/begin.mp3",Music.class);
    	assetManager.load("audio/game_over.mp3",Music.class);
    	assetManager.load("audio/get_bomber.mp3",Music.class);
    	assetManager.load("audio/get_barrett.mp3",Music.class);
		
	}
    
	public static TextureRegion getBlockTextureRegion(int value) {
    	if(textureAtlas == null){
    		textureAtlas = assetManager.get("block/block.pack", TextureAtlas.class);
    	}
    	AtlasRegion atlasRegion = textureAtlas.findRegion("b", value);
        return atlasRegion;
    }
    
    public static TextureRegion getBulletTextureRegion(int value) {
    	String fileName ="ui/bullet.png";
    	if(value == BulletType.COMMON.getValue()){
    		fileName = "ui/bullet.png";
    	}
    	
    	Texture texture = assetManager.get(fileName, Texture.class);
    	TextureRegion textureRegion = new TextureRegion(texture);
        return textureRegion;
    }
    
    public static TextureRegion getControlTextureRegion(ControlType value) {
    	Texture dirs = assetManager.get("ui/controls.png", Texture.class);
    	TextureRegion[] regions = TextureRegion.split(dirs, 64 ,64 )[0];
    	
    	TextureRegion textureRegion = null;
    	String fileName = null;
    	switch(value){
    	case GOTOFIGHT:
    		fileName = "ui/gotofight.png";
    		textureRegion = new TextureRegion(assetManager.get(fileName, Texture.class));
    		break;
    	case ATTACK:
    		fileName = "ui/attack.png";
    		textureRegion = new TextureRegion(assetManager.get(fileName, Texture.class));
    		break;
    	case WORLD:
    		fileName = "ui/to_world.png";
    		textureRegion = new TextureRegion(assetManager.get(fileName, Texture.class));
    		break;
    	case HOME:
    		fileName = "ui/to_home.png";
    		textureRegion = new TextureRegion(assetManager.get(fileName, Texture.class));
    		break;
    	case LEFT:
    		textureRegion = regions[0];
    		break;
    	case RIGHT:
    		textureRegion = regions[1];
    		break;
    	case UP:
    		textureRegion = regions[2];
    		break;
    	case DOWN:
    		textureRegion = regions[0];
    		break;
    	case FIRE:
    		textureRegion = regions[3];
    		break;
		default:
			fileName ="ui/dir.png"; 
			textureRegion = new TextureRegion(assetManager.get(fileName, Texture.class));
			break;
    	}
    	
        return textureRegion;
    }

    public void dispose() {
    	assetManager.dispose();
    	if(textureAtlas != null)
    	textureAtlas.dispose();
    }
}
