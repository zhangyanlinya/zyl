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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.trance.tranceview.constant.BlockType;
import com.trance.tranceview.stages.GameStage;

public class WorldUtils {

    public static World createWorld() {
        return new World(new Vector2(0, 0), true);
    }

    public static Body createBorder(World world,float x, float y, float width, float height) {
    	// Create ball body and shape
    	x = x  * GameStage.WORLD_TO_BOX;
    	y = y * GameStage.WORLD_TO_BOX;
        width=width* GameStage.WORLD_TO_BOX;
        height=height* GameStage.WORLD_TO_BOX;
    	
    	BodyDef bodyDef = new BodyDef();
    	bodyDef.type = BodyType.StaticBody;
    	Body body = world.createBody(bodyDef);
    	EdgeShape edge = new EdgeShape();
        FixtureDef boxShapeDef = new FixtureDef();
        boxShapeDef.shape = edge;
        edge.set(new Vector2(x, y), new Vector2(width, y));
        body.createFixture(boxShapeDef);
        edge.set(new Vector2(x, y), new Vector2(x, height));
        body.createFixture(boxShapeDef);
        edge.set(new Vector2(width, y), new Vector2(width, height));
        body.createFixture(boxShapeDef);
        edge.set(new Vector2(width, height), new Vector2(x, height));
        body.createFixture(boxShapeDef);
        edge.dispose();
        return body;
        
    }
    public static Body createBlock(World world, int type,float x, float y, float width, float height) {
    	BodyDef bodyDef = new BodyDef();
    	bodyDef.type = BodyType.DynamicBody;
    	if(type == BlockType.KING.getValue() || type == BlockType.WATER.getValue() || type == BlockType.STEEL.getValue()){
    		bodyDef.type = BodyType.StaticBody;
    	}
    	if(type == BlockType.TANK_ENEMY.getValue() || type == BlockType.TANK_MAIN.getValue()){
    		bodyDef.fixedRotation = true;
    	}
//    	bodyDef.linearDamping = 0.1f;
    	bodyDef.position.set((x + width/2) * GameStage.WORLD_TO_BOX, (y + height/ 2) * GameStage.WORLD_TO_BOX);
    	PolygonShape shape = new PolygonShape();
    	shape.setAsBox((width/ 2 - 2) * GameStage.WORLD_TO_BOX, (height / 2 - 2) * GameStage.WORLD_TO_BOX);
    	Body body = world.createBody(bodyDef);
    	FixtureDef f = new FixtureDef();
    	f.shape = shape;//夹具的形状
    	f.density = 2f;//夹具的密度
    	f.friction = 0.9f;//夹具的摩擦力
    	f.restitution = 0.1f; //弹力
    	if(type < BlockType.TANK_MAIN.getValue()){
    		f.filter.categoryBits = 2;
        	f.filter.maskBits = 4;
    	}else{
    		f.filter.categoryBits = 4;
        	f.filter.maskBits = 4;
    	}
    	
    	body.createFixture(f);//刚体创建夹具.
    	shape.dispose();
    	return body;
    }
    
    public static Body createBullet(World world, float x, float y,float width,float height,float rotation) {
    	BodyDef bodyDef = new BodyDef();
    	bodyDef.type = BodyType.DynamicBody;
    	bodyDef.fixedRotation  = true;
    	PolygonShape shape = new PolygonShape();
    	
    	float hx = 0;
    	float hy = 0;
    	if(rotation == 0 ||rotation == 180){
    		hx = width/2 * GameStage.WORLD_TO_BOX;
    		hy = height/2 * GameStage.WORLD_TO_BOX;
    	}else{
    		hx = height/2 * GameStage.WORLD_TO_BOX;
        	hy = width/2 * GameStage.WORLD_TO_BOX;
    	}
    	shape.setAsBox(hx,hy);
    	bodyDef.position.set(x * GameStage.WORLD_TO_BOX, y * GameStage.WORLD_TO_BOX);
    	Body body = world.createBody(bodyDef);
    	FixtureDef f = new FixtureDef();
    	f.shape = shape;//夹具的形状
    	f.density = 1f;//夹具的密度
    	f.friction = 0f;//夹具的摩擦力
    	f.restitution = 0.1f;//反弹
		f.filter.categoryBits = 4;
		f.filter.maskBits = 4;
    	body.createFixture(f);//刚体创建夹具.
    	shape.dispose();
    	return body;
    }

}
