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

package com.trance.tranceview.stages;

import android.util.Log;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.trance.tranceview.actors.Block;
import com.trance.tranceview.actors.Bullet;
import com.trance.tranceview.actors.Control;
import com.trance.tranceview.actors.GameActor;
import com.trance.tranceview.constant.BlockType;
import com.trance.tranceview.constant.ControlType;
import com.trance.tranceview.constant.LogTag;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.screens.GameScreen;
import com.trance.tranceview.screens.MapScreen;
import com.trance.tranceview.utils.WorldUtils;

public class GameStage extends Stage implements ContactListener {

	/** 数组宽数量 */
	public final static int ARR_WIDTH_SIZE = 16;
	/** 数组高数量 */
	public final static int ARR_HEIGHT_SIZE = 20;
	
	/** 中间游戏区域的百分比 */
	public static double percent = 0.9;
	/** 每格的边长 */
	public static float length = 32;
	/** 游戏区域宽 */
	public static float game_width = 512;
	/** 游戏区域高 */
	public static float game_height = 832;
	/** 菜单区域宽度 */
	public static float menu_width = 208;
	/** 控制区域高度 */
	public static float control_height = 368;
	
    private World world;
    private ShapeRenderer renderer;
    private final float TIME_STEP = 1 / 50f;;
    private float accumulator = 0f;
    private Block mainTank;
    
    public static final float WORLD_TO_BOX = 0.05f;
    public static final float BOX_TO_WORLD = 20f;
//  private OrthographicCamera camera;
//  Box2DDebugRenderer debugRenderer;
	
	public final static Array<Block> blocks = new Array<Block>();
	
	public final static Array<Block> tanks = new Array<Block>();
	
	private final Array<Body> bodies = new Array<Body>();
	
	public GameStage(float width,float height,boolean keepAspectRatio) {
        super(width, height,keepAspectRatio);
        
        
//      camera = new OrthographicCamera(); 
//      camera.setToOrtho(false, width* WORLD_TO_BOX, height * WORLD_TO_BOX);
//      camera.position.set(width/2 *WORLD_TO_BOX, height/2 * WORLD_TO_BOX, 0);
        
		length = (int) (width * percent / ARR_WIDTH_SIZE);
		game_width   = length * ARR_WIDTH_SIZE;
		game_height  = length * ARR_HEIGHT_SIZE;
		menu_width     = (width - game_width)/2;
		control_height = height - game_height -length;//再减少一格
        
//		debugRenderer = new Box2DDebugRenderer();
		renderer = new ShapeRenderer();
        init();
        
//        Gdx.input.setInputProcessor(this);
        
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
        
        accumulator += delta;
        while (accumulator >= delta) {
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }
        
        world.getBodies(bodies);
        for(int i = 0 ; i< bodies.size ; i++){
        	destoryBody(bodies.get(i));
        }
        
//      debugRenderer.render(world, camera.combined);
    }
    
    
    //DestoryBody
	private void destoryBody(Body body) {
		GameActor ga = (GameActor) body.getUserData();
		if(ga == null){
			return;
		}
		if(!ga.alive){
		   world.destroyBody(body);
		}
	}
	
    private void setUpWorld() {
    	world = WorldUtils.createWorld();
        world.setContactListener(this);
        world.setContactFilter( new ContactFilter() {
			
			@Override
			public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
		        Body bodyA = fixtureA.getBody();
		        Body bodyB = fixtureB.getBody();
		        GameActor a =(GameActor) bodyA.getUserData();
		        GameActor b =(GameActor) bodyB.getUserData();
		        if(a != null){
		        	if(a.role == 0){
			        	Block block = (Block)a;
			        	if(block.type == BlockType.GRASS.getValue()){
			        		return false;
			        	}
		        	}else if(a.role == 1){
		        		if(b!= null && b.role == 0){
		        			Block block = (Block)b;
		        			if(block.type == BlockType.WATER.getValue()){
				        		return false;
				        	}
		        		}
		        	}
		        }
		        if(b != null ){
		        	if( b.role == 0){
			        	Block block = (Block)b;
			        	if(block.type == BlockType.GRASS.getValue()){
			        		return false;
			        	}
		        	}else if(b.role == 1){
		        		if(a!= null && a.role == 0){
		        			Block block = (Block)a;
		        			if(block.type == BlockType.WATER.getValue()){
				        		return false;
				        	}
		        		}
		        	}
		        }
				return true;
			}
		});
        
        WorldUtils.createBorder(world,menu_width, control_height, game_width+menu_width, GameScreen.height - length);
    }
    
    public void init(){
    	bodies.clear();
		this.clear();
		setUpWorld();
		mainTank = initMap();
		if(mainTank == null){
			Log.e(LogTag.TAG, "no main tank");
			return;
		}
		initControl();
    }
    
    private void initControl(){
		int side = GameScreen.width/8; 
		Control up = new Control(ControlType.UP,mainTank,       GameScreen.width/4 - side/2, control_height - side - side/2,side,side);
		Control down = new Control(ControlType.DOWN,mainTank,   GameScreen.width/4 - side/2, side/2,side,side);
		Control left = new Control(ControlType.LEFT,mainTank,   GameScreen.width/4 - side - side/2,  control_height/2  - side/2,side,side);
		Control right = new Control(ControlType.RIGHT,mainTank, GameScreen.width/4 + side/2, control_height/2 - side/2,side,side);
		Control fire = new Control(ControlType.FIRE,mainTank,   GameScreen.width/2 + side , control_height/2 - side/2,side,side);
		addActor(up.image);
		addActor(down.image);
		addActor(left.image);
		addActor(right.image);
		addActor(fire.image);
    }
	//
	private Block initMap() {
		Block main = null;
		blocks.clear();
		tanks.clear();
		
		for (int i = 0; i < MapData.map.length; i++) {
			float n = MapData.map.length - 1 - i;
			for (int j = 0; j < MapData.map[i].length; j++) {
				int type = MapData.map[i][j];
				float x = menu_width + j * length;
				float y = control_height + n * length;
				if (type > 0){
					Block block = MapScreen.blockPool.obtain();
					if(type < 6){
						block.init(world,type, x,y, length,length,null);
						blocks.add(block);
					}else{
						block.init(world,type, x,y, length,length,renderer);
						block.move = true;
						tanks.add(block);
						if(type == BlockType.TANK_MAIN.getValue()){
							main = block;
						}
					}
				}
			}
		}
		//目的为了图层顺序
		for(int i = 0 ; i <tanks.size ;i++){
			Block block = tanks.get(i);
			addActor(block);
			//track
			if(block.type == BlockType.TANK_ENEMY.getValue()){
				block.setTrackBlock(mainTank);//tracking to mainTank;
			}
		}
		for(int i = 0 ; i <blocks.size ;i++){
			addActor(blocks.get(i));
		}
		
		return main;
	}
	
	

	@Override
	public void draw() {
		for(int i = 0 ; i <tanks.size ;i++){
			Block block = tanks.get(i);
			//track
			if(block.type == BlockType.TANK_ENEMY.getValue()){
				block.track(mainTank);
			}
		}
		super.draw();
	}

	@Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        Body bodyA = fa.getBody();
        Body bodyB = fb.getBody();
        GameActor a =(GameActor) bodyA.getUserData();
        GameActor b =(GameActor) bodyB.getUserData();
        if(a == null &&  b== null){
        	return;
        }
        if(a != null && b == null){
           if(a.role == 1){
        	   a.dead();
           }
           return;
        }
        if(a == null && b != null){
        	if(b.role == 1){
        		b.dead();
        	}
        	return;
        }

        if(a.role != b.role){//角色不一样
			if (a.good != b.good) {//敌对的
				if (a.role == 1) {
					b.byAttack(a);
				} else {
					a.byAttack(b);
				}
			}
        }
        if(a.role == 1){
     	   a.dead();
        }
        if(b.role == 1){
    		b.dead();
    	}
        a.collision = true;
        b.collision = true;
    }
	
	
    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        if(fa != null){
        	 Body bodyA = fa.getBody();
        	 GameActor a =(GameActor) bodyA.getUserData();
             if(a != null){
             	a.collision = false;
             }
        }
        if(fb != null){
        	 Body bodyB = fb.getBody();
        	 GameActor b =(GameActor) bodyB.getUserData();
        	 if(b != null){
             	b.collision = false;
             }
        }
       
       
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
    
	@Override
	public void dispose() {
		renderer.dispose();
//		debugRenderer.dispose();
		world.dispose();
		blocks.clear();
		tanks.clear();
		Bullet.bulletPool.clear();
		MapScreen.blockPool.clear();
		super.dispose();
	}
	
}



