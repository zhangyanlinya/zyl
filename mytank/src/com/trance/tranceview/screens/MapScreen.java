package com.trance.tranceview.screens;

import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.trance.common.socket.model.Request;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.mapdata.handler.MapDataCmd;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.Block;
import com.trance.tranceview.actors.GameActor;
import com.trance.tranceview.actors.MapImage;
import com.trance.tranceview.constant.ControlType;
import com.trance.tranceview.controller.GestureController;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.pools.BlockPool;
import com.trance.tranceview.textinput.RenameInputListener;
import com.trance.tranceview.utils.AssetsManager;
import com.trance.tranceview.utils.FontUtil;
import com.trance.tranceview.utils.RandomUtil;
import com.trance.tranceview.utils.SocketUtil;

public class MapScreen implements Screen ,InputProcessor{

	private TranceGame game;
	public static float menu_width = 0;
	/** 控制区域高度 */	
	public static int width;
	public static int height;
	/** 数组宽数量 */
	public final static int ARR_WIDTH_SIZE = 16;
	/** 数组高数量 */
	public final static int ARR_HEIGHT_SIZE = 20;
	
	/** 中间游戏区域的百分比 */
	public final static double percent = 0.9;
	/** 每格的边长 */
	public static float length = 45;
	/** 游戏区域宽 */
	public static float game_width = 720;
	/** 游戏区域高 */
	public static float game_height = 900;
	/** 菜单区域宽度 */
	public static float control_height = 300;
	private Stage stage;
	private BitmapFont font;
	private SpriteBatch spriteBatch;
	private Image attack;
	private Image toWorld;
	private Image rename;
	public final static Array<Block> blocks = new Array<Block>();
	private boolean init;
	private TextInputListener listener;
	private PlayerDto playerDto;
	private OrthographicCamera camera;
	private Image bg;
	
	public MapScreen(TranceGame game){
		this.game = game;
	}
	
	public void init(){
		width = Gdx.graphics.getWidth(); // 720
		height = Gdx.graphics.getHeight(); // 1200
		length = (int) (width * percent / ARR_WIDTH_SIZE);
		game_width   = length * ARR_WIDTH_SIZE;
		game_height  = length * ARR_HEIGHT_SIZE;
		menu_width  = (width - game_width)/2;
		control_height = height - game_height-length * 2;//再减2格
		
		stage = new Stage(width, height, true);
		camera = new OrthographicCamera(width, height);
		stage.setCamera(camera);
		camera.setToOrtho(false, width, height);
		
		//文字 
		spriteBatch = new SpriteBatch();
		
		bg = new MapImage(AssetsManager.getInstance().get("world/bg.jpg",Texture.class));
		
		//攻击
		attack = new Image(AssetsManager.getInstance().getControlTextureRegion(ControlType.ATTACK));
		attack.setPosition(width - attack.getWidth() * 2, attack.getHeight());
		attack.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				attack();
			}
		});
		
		//返回世界地图
		toWorld = new Image(AssetsManager.getInstance().getControlTextureRegion(ControlType.WORLD));
		toWorld.setBounds(10, 10, toWorld.getWidth() + toWorld.getWidth()/2, toWorld.getHeight() + toWorld.getHeight()/2);
		toWorld.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				toWorld();
			}
		});
		
		//Rename
		listener = new RenameInputListener();
		rename = new Image(AssetsManager.getInstance().getControlTextureRegion(ControlType.GOTOFIGHT));
		rename.setBounds(10 + toWorld.getWidth() + toWorld.getWidth()/2, 10, rename.getWidth() + rename.getWidth()/2, rename.getHeight() + rename.getHeight()/2);
		rename.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.input.getTextInput(listener, "请输入要改的名字", MainActivity.player.getPlayerName());
			}
		});
	}
	
	public void setPlayerDto(PlayerDto playerDto){
		this.playerDto = playerDto;
	}

	@Override
	public void show() {
		if(!init){
			init();
			init = true;
		}
		
		MapData.gameover = true;
		stage.clear();
		float w = bg.getWidth();
		float h = bg.getHeight();
		for(float x = -w ; x < stage.getWidth(); x += w){//background;
			for(float y = -h ; y < stage.getHeight() ; y += h){
				bg = new MapImage(AssetsManager.getInstance().get("world/bg.jpg",Texture.class));
				bg.setPosition(x, y);
				stage.addActor(bg);
			}
		}
		
		for(int i = 0 ; i < 5; i ++){
			int index = RandomUtil.nextInt(4) + 1;
			int x = RandomUtil.nextInt((int)width);
			int y = RandomUtil.nextInt((int)height);
			Image grass = new MapImage(AssetsManager.getInstance().get("world/soil" + index +".png", Texture.class));
			grass.setPosition(x, y);
			stage.addActor(grass);
		}
		
		initMap();//初始化地图
		if(isEdit()){
			initPlayerBlock();
		}
		stage.addActor(attack);
		stage.addActor(toWorld);
		if(playerDto.isMyself()){
			stage.addActor(rename);
		}
		
		font = FontUtil.getInstance().getFont(35, "可拖动砖块编辑攻击" + playerDto.getPlayerName(), Color.RED);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer(); 
		GestureController controller = new GestureController(camera, 0, width * 2, 0, height * 2);
		GestureDetector gestureHandler = new GestureDetector(controller);
		inputMultiplexer.addProcessor(gestureHandler);
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(this);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	/**
	 *  地图是否可编辑
	 * @return
	 */
	private boolean isEdit(){
		return(playerDto.isMyself());//是自己的地图 且处于网络状态
	}
	
	private void attack(){
		game.gameScreen.setPlayerDto(playerDto);
		game.setScreen(game.gameScreen);
	}
	
	private void toWorld(){
		this.game.setScreen(game.worldScreen);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		stage.draw();
		spriteBatch.begin();
		if(playerDto.isMyself()){
			font.draw(spriteBatch,"可拖动砖块编辑",0,height);
		}
		font.draw(spriteBatch, playerDto.getPlayerName(),0,height - length);
		font.draw(spriteBatch,"攻击", width-300,100);
		spriteBatch.end();
	}
	
	// 初始化关卡地图
	public void initMap() {
		blocks.clear();
		if(playerDto == null){
			return;
		}
		int[][] map = playerDto.getMap();
		for (int i = 0; i < map.length; i++) {
			float n = map.length - 1 - i;
			for (int j = 0; j < map[i].length; j++) {
				int type = map[i][j];
				float x = menu_width + j * length;
				float y = control_height + n * length;
				if(i == 0 ){
					int index = RandomUtil.nextInt(5) + 1;
					Image grass = new MapImage(AssetsManager.getInstance().get("world/tree" + index +".png", Texture.class));
					grass.setPosition(x, y + length);
					stage.addActor(grass);
				}else if(i == map.length-1){
					int index = RandomUtil.nextInt(5) + 1;
					Image grass = new MapImage(AssetsManager.getInstance().get("world/tree" + index +".png", Texture.class));
					grass.setPosition(x, y - length * 2);
					stage.addActor(grass);
				}
				
				if(j == 0){
					int index = RandomUtil.nextInt(5) + 1;
					Image grass = new MapImage(AssetsManager.getInstance().get("world/tree" + index +".png", Texture.class));
					grass.setPosition(x - length, y);
					stage.addActor(grass);
				}else if(j == map[i].length -1){
					int index = RandomUtil.nextInt(5) + 1;
					Image grass = new MapImage(AssetsManager.getInstance().get("world/tree" + index +".png", Texture.class));
					grass.setPosition(x + length, y);
					stage.addActor(grass);
				}
				
				Block block = Block.blockPool.obtain();
				block.setIndex(i, j);
				if (type > 0){
					block.init(null,type, x, y, length,length,null);
					stage.addActor(block);
				}else{
					block.setPosition(x, y);
					blocks.add(block);
				}
			}
		}
	}
	
	/**
	 * 初始化下面的选择框
	 */
	private void initPlayerBlock() {
		float x = 0;
		for(int i = 1; i < 10; i++){
			if(i==8){//没有8
				continue;
			}
			Block block = Block.blockPool.obtain();
			x = i * length;
			block.init(null,i, x,control_height/2, length,length,null);
			stage.addActor(block);
		}
	}
	
	private Block a ;
	private float oldx;
	private float oldy;
	private int oldi;
	private int oldj;
	private int oldType;
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(!isEdit()){
			return false;
		}
		screenY = height - screenY;
		if(screenY < 0){
			return false;
		}
		Actor actor = stage.hit(screenX, screenY, true);
		if(actor == null || !(actor instanceof Block)){
			return false;
		}
		a = (Block) actor;
		Block b = (Block)a;
		oldx = b.getX();
		oldy = b.getY();
		oldi = b.i;
		oldj = b.j;
		oldType = b.type;
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(a == null){
			return true;
		}
		
		screenY = height - screenY;
		if(screenY < 0){
			return true;
		}
		
		//校正
		screenX -= a.getWidth()/2;
		screenY -= a.getHeight()/2;	
		
		Block b = compute(screenX,screenY,a);
		if(b == null){//移除
			if(oldy == control_height/2 ){//原始的不移除 
				a.setPosition(oldx, oldy);
				return true;
			}
			if(a.getY()  > control_height/2){ //没有移到控制区域下面不算移除
				a.setPosition(oldx, oldy);
				return true;
			}
			
			a.remove();
			Block.blockPool.free(a);
			playerDto.getMap()[oldi][oldj] = 0;
			
			Block block = Block.blockPool.obtain();
			block.i = oldi;
			block.j = oldj;
			block.setPosition(oldx, oldy);
			blocks.add(block);
			StringBuilder from = new StringBuilder();
			from.append(oldi).append("|").append(oldj).append("|").append(0);
			saveMaptoServer(1,from.toString(),null);
			return true;
		}
		
		a.setPosition(b.getX(), b.getY());
		a.setIndex(b.i, b.j);
		playerDto.getMap()[b.i][b.j] = oldType; 
		
		if(oldy == control_height/2){//增加
			if(b.type == 0){
				blocks.removeValue(b, false);
			}else{
				b.remove();
			}
			Block.blockPool.free(b);
			Block block = Block.blockPool.obtain();
			block.init(null,oldType, oldx, oldy, length, length,null);
			stage.addActor(block);
			StringBuilder to = new StringBuilder();
			to.append(b.i).append("|").append(b.j).append("|").append(b.type);
			saveMaptoServer(1,null,to.toString());
			return true;
		}
		
		//替换
		b.setPosition(oldx, oldy);
		b.setIndex(oldi, oldj);
		playerDto.getMap()[oldi][oldj] = b.type;
		
		if(oldType == b.type){
			return true; //类型一样不用上传
		}
		
		StringBuilder from = new StringBuilder();
		from.append(oldi).append("|").append(oldj).append("|").append(oldType);
		StringBuilder to = new StringBuilder();
		to.append(a.i).append("|").append(a.j).append("|").append(b.type);
		saveMaptoServer(1,from.toString(),to.toString());
		return true;
	}
	
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(a != null){
			screenY = height - screenY;
			if(screenY < 0){
				return true;
			}
			float x = screenX - a.getWidth()/2;
			float y = screenY - a.getHeight()/2;
			a.setPosition(x, y);
		}
		return true;
	}
	
	/**
	 * compute new position
	 * @param newX
	 * @param newY
	 */
	private Block compute(float x, float y ,Actor a) {
		float min = a.getWidth() > a.getHeight() ? a.getHeight()/2: a.getWidth()/2;
		Array<Actor> actors = stage.getActors();
		for(int i = 0 ;i < actors.size ; i++){
			Actor at = actors.get(i);
			if(at == a){//自身
				continue;
			}
			if(!(at instanceof GameActor)){
				continue;
			}
			Block b = (Block)at;
			if(b.i == 0){//与原始的不比较
				continue;
			}
			float dst = b.dst(x,y);
			if(dst <= min){
				return b;
			}
		}
		for( int i = 0 ;i< blocks.size ;i++){
			Block b =  blocks.get(i);
			float dst = b.dst(x,y);
			if(dst <= min){
				return b;
			}
		}
		return null;
	}
	
	/**
	 * save map to server
	 */
	private void saveMaptoServer(int level ,String from ,String to){
		HashMap<String,Object> parms = new HashMap<String,Object>();
		parms.put("level", level);
		parms.put("from", from);
		parms.put("to", to);
		SocketUtil.sendAsync(Request.valueOf(Module.MAP_DATA, MapDataCmd.SAVE_PLAYER_MAP_DATA, parms));
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	
	@Override
	public void resize(int width, int height) {
		Gdx.app.log(" #### mapsrceen resize()", width + " == " +height);
		stage.setViewport(width, height, true);
	}

	@Override
	public void hide() {
		
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
		blocks.clear();
		
		if(spriteBatch != null){
			spriteBatch.dispose();
		}
		
		if(font != null){
			font.dispose();
		}
	}
}
