package com.trance.tranceview.screens;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.model.Request;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.player.handler.PlayerCmd;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.Block;
import com.trance.tranceview.actors.Bullet;
import com.trance.tranceview.actors.Control;
import com.trance.tranceview.actors.GameActor;
import com.trance.tranceview.constant.BlockType;
import com.trance.tranceview.constant.ControlType;
import com.trance.tranceview.constant.LogTag;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.utils.AssetsManager;
import com.trance.tranceview.utils.FontUtil;
import com.trance.tranceview.utils.WorldUtils;

public class GameScreen implements Screen , ContactListener{
	
	private TranceGame tranceGame;
	public static int width;
	public static int height;
	private Stage stage;
	private Image toWorld;
	private Window window;
	private ImageButton btn_up;
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private Music music;
	
	
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
//    private OrthographicCamera camera;
//    Box2DDebugRenderer debugRenderer;
	
	public final static Array<Block> blocks = new Array<Block>();
	
	public final static Array<Block> tanks = new Array<Block>();
	
	private final Array<Body> bodies = new Array<Body>();
	
	
	/**
	 * 一局所用总时间
	 */
	private final static int TOTAL_TIME = 2 * 60;
	private Action[] sAction;
	/**
	 * 当前时间
	 */
	private int currTime = TOTAL_TIME;
	private Timer timer;
	private TimerTask timeTask;
	private boolean init;
	
	public GameScreen(TranceGame tranceGame) {
		this.tranceGame = tranceGame;
	}
	
	private void init(){
		spriteBatch = new SpriteBatch();
		font = FontUtil.getInstance().getFont(35, "点赞倒计时：", Color.RED);
		music = AssetsManager.getInstance().get("audio/begin.mp3");
//		music.play();
		width = Gdx.graphics.getWidth(); // 720
		height = Gdx.graphics.getHeight(); // 1200
		stage = new Stage(width, height, true);
		
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
		
		//返回家
		toWorld = new Image(AssetsManager.getInstance().getControlTextureRegion(ControlType.WORLD));
		toWorld.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				tranceGame.setScreen(tranceGame.worldScreen);
			}
		});
		stage.addActor(toWorld);
		
		//提示框
		TextureRegionDrawable tips = new TextureRegionDrawable( new TextureRegion(
				AssetsManager.getInstance().get("world/tips.png",Texture.class)));
		Drawable background = new TextureRegionDrawable(tips);
		WindowStyle style = new WindowStyle(font, Color.MAGENTA, background);
		window = new Window("点赞",style);
		window.setPosition(width/2 - window.getWidth()/2, height/2 - window.getHeight()/2);
		
		//点赞
		TextureRegionDrawable up = new TextureRegionDrawable( new TextureRegion(
				AssetsManager.getInstance().get("ui/up.png",Texture.class)));
		btn_up = new ImageButton(up);
		btn_up.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(MapData.playerId > 0L && MainActivity.player != null && MapData.playerId != MainActivity.player.getId()){
					Map<String,Object> params = new HashMap<String,Object>();
					params.put("targetId", MapData.playerId);
					SimpleSocketClient.socket.sendAsync(Request.valueOf(Module.PLAYER, PlayerCmd.UP, params));
//					Music music = AssetsManager.getInstance().get("audio/get_bomber.mp3");
//					music.play();
				}
			}
		});
		window.addActor(btn_up);
    	world = WorldUtils.createWorld();
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
    	world.clearForces();
        world.getBodies(bodies);
        for(int i = 0 ; i < bodies.size ; i++){
        	world.destroyBody(bodies.get(i));
        }
        bodies.clear();
        
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
	
	private void initClock() {
		sAction = new Action[TOTAL_TIME];// 一共执行120次
		// 使用action实现定时器
		for (int i = 0; i < sAction.length; i++) {
			Action delayedAction = Actions.run(new Runnable() {

				@Override
				public void run() {
					currTime--;
					if(currTime <= 0){
						MapData.over = true;
					}
				}
			});
			// 延迟1s后执行delayedAction
			Action action = Actions.delay(1f, delayedAction);
			sAction[i] = action;
		}
		stage.addAction(Actions.sequence(sAction));
	}
	
	//
	private void initMap() {
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
							this.mainTank = block;
						}
					}
				}
			}
		}
		//目的为了图层顺序
		for(int i = 0 ; i <tanks.size ;i++){
			Block block = tanks.get(i);
			stage.addActor(block);
		}
		for(int i = 0 ; i <blocks.size ;i++){
			stage.addActor(blocks.get(i));
		}
	}
	
	@Override
	public void show() {
		if(!init){
			init();
			init = true;
		}
		MapData.win = false;
		MapData.over = false;
		currTime = TOTAL_TIME;//初始化时间 
		stage.clear();
		stage.addActor(toWorld);
		// 将Action加到Stage中进行执行
		setUpWorld();
		initMap();
		initClock();
		if(mainTank == null){
			Log.e(LogTag.TAG, "no main tank");
			return;
		}
		initControl();
		InputMultiplexer inputMultiplexer = new InputMultiplexer(); 
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
    private void initControl(){
		int side = GameScreen.width/8; 
		Control up = new Control(ControlType.UP,mainTank,       GameScreen.width/4 - side/2, control_height - side - side/2,side,side);
		Control down = new Control(ControlType.DOWN,mainTank,   GameScreen.width/4 - side/2, side/2,side,side);
		Control left = new Control(ControlType.LEFT,mainTank,   GameScreen.width/4 - side - side/2,  control_height/2  - side/2,side,side);
		Control right = new Control(ControlType.RIGHT,mainTank, GameScreen.width/4 + side/2, control_height/2 - side/2,side,side);
		Control fire = new Control(ControlType.FIRE,mainTank,   GameScreen.width/2 + side , control_height/2 - side/2,side,side);
		stage.addActor(up.image);
		stage.addActor(down.image);
		stage.addActor(left.image);
		stage.addActor(right.image);
		stage.addActor(fire.image);
    }

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		
//		clock();
		if(MapData.win || MapData.over){
			stage.addActor(window);
		}
		spriteBatch.begin();
		font.draw(spriteBatch,"倒计时:" + currTime,0,height);
		spriteBatch.end();
		stage.draw();
		stage.act(delta);
		
		//box2d
        accumulator += delta;
        while (accumulator >= delta) {
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }
        
        world.getBodies(bodies);
        for(int i = 0 ; i< bodies.size ; i++){
        	destoryBody(bodies.get(i));
        }
	}
	
//	private long time = 0;
//	private void clock(){
//		long now = System.currentTimeMillis();
//		if( (now - time) > 1000){
//			time = now;
//			currTime--;
//			if(currTime <= 0){
//				MapData.over = true;
//			}
//		}
//	}
	
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
    }
	
	
    @Override
    public void endContact(Contact contact) {
    	
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
	
	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void hide() {
		MapData.over = true;
		if(timeTask != null){
			timeTask.cancel();
		}
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		if(!init){
			return;
		}
		init = false;
		if (stage != null){
			stage.dispose();
		}
		
		if(spriteBatch != null){
			spriteBatch.dispose();
		}
		
		if(timeTask != null){
			timeTask.cancel();
			timeTask = null;
		}
		
		if(timer != null){
			timer.cancel();
			timer = null;
		}
		
		if(music != null){
			music.dispose();
		}
		
		if(font != null){
			font.dispose();
		}
		
		renderer.dispose();
//		debugRenderer.dispose();
		if(world != null){
			world.dispose();
		}
		blocks.clear();
		tanks.clear();
		Bullet.bulletPool.clear();
		MapScreen.blockPool.clear();
	}
	
}
