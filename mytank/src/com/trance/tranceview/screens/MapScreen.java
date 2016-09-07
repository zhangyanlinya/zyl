package com.trance.tranceview.screens;

import java.util.HashMap;
import java.util.Map;

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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.trance.common.socket.model.Request;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.army.model.ArmyDto;
import com.trance.trancetank.modules.army.model.ArmyType;
import com.trance.trancetank.modules.building.model.PlayerBuildingDto;
import com.trance.trancetank.modules.mapdata.handler.MapDataCmd;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.Building;
import com.trance.tranceview.actors.GameActor;
import com.trance.tranceview.actors.MapImage;
import com.trance.tranceview.constant.ControlType;
import com.trance.tranceview.controller.GestureController;
import com.trance.tranceview.mapdata.MapData;
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
	private Image toUpgrade;
	private Image rename;
	public final static Array<Building> buildings = new Array<Building>();
	private boolean init;
	private TextInputListener listener;
	private PlayerDto playerDto;
	private OrthographicCamera camera;
	private Image bg;
	private GestureController controller;
	
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
		font = FontUtil.getInstance().getFont(35, "可拖动砖块编辑攻击等级金银币粮食" + playerDto.getPlayerName(), Color.WHITE);
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
		
		//升级
		toUpgrade = new Image(AssetsManager.getInstance().getControlTextureRegion(ControlType.WORLD));
		toUpgrade.setBounds(10 + toUpgrade.getWidth(), 10, toUpgrade.getWidth() + toUpgrade.getWidth()/2, toUpgrade.getHeight() + toUpgrade.getHeight()/2);
		toUpgrade.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				toProgress();
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
		
		MapData.gamerunning = false;
//		camera.position.set(width/2, height/2, 0);
//		camera.update();
		
		noArmy = false;
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
			initPlayerLeftBuiding();
		}
		stage.addActor(attack);
		stage.addActor(toWorld);
		stage.addActor(toUpgrade);
		if(playerDto.isMyself()){
			stage.addActor(rename);
		}
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer(); 
		controller = new GestureController(camera, 0, width * 2, 0, height * 2);
		camera.position.set(width/2, height/2, 0);
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
	
	boolean noArmy = false;
	private void attack(){
		Map<ArmyType,ArmyDto> armys =MainActivity.player.getArmys();
		if(armys == null || armys.isEmpty()){
			noArmy = true;
			return;
		}
		GameScreen.playerDto = playerDto;
		game.setScreen(game.gameScreen);
	}
	
	private void toWorld(){
		this.game.setScreen(game.worldScreen);
	}
	private void toProgress(){
		this.game.setScreen(game.upgradeScreen);
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
		if(noArmy){
			font.draw(spriteBatch,"没有可用部队",0,100);
		}
		renderPlayerInfo(spriteBatch,playerDto);
		
		font.draw(spriteBatch,"攻击", width-300,100);
		spriteBatch.end();
	}
	
	public void renderPlayerInfo(SpriteBatch spriteBatch, PlayerDto playerDto){
		font.draw(spriteBatch, playerDto.getPlayerName(),0,height - length);
		font.draw(spriteBatch, "等级：  " + playerDto.getLevel(), 0 , height - length * 2);
		font.draw(spriteBatch, "金币：  " + playerDto.getGold(), 0 , height - length * 3);
		font.draw(spriteBatch, "粮食: " +playerDto.getFoods(), 0 , height - length * 4);
		font.draw(spriteBatch, "银币: " +playerDto.getSilver(), 0 , height - length * 5);
	}
	
	// 初始化关卡地图
	public void initMap() {
		buildings.clear();
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
				
				Building block = Building.buildingPool.obtain();
				block.setIndex(i, j);
				if (type > 0){
					block.init(null,type, x, y, length,length,null);
					stage.addActor(block);
				}else{
					block.setPosition(x, y);
					buildings.add(block);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	private void initPlayerLeftBuiding() {
		Map<Integer,PlayerBuildingDto> map = playerDto.getBuildings();
		float x = 0;
		int i = 0;
		for(PlayerBuildingDto dto : map.values()){
			if(dto.getLeftAmount() <= 0){
				continue;
			}
			Building buiding = Building.buildingPool.obtain();
			x = i * length;
			buiding.init(null,dto.getId(), x,control_height/2, length,length,null, font, dto.getLeftAmount());
			stage.addActor(buiding);
			i ++;
		}
	}
	
	private Building a ;
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
		Vector3 vector3 = new Vector3(screenX, screenY, 0);  
		camera.unproject(vector3); // 坐标转化  
		float x = vector3.x;
		float y = vector3.y;
		
//		screenY = height - screenY;
		if(y < 0){
			return false;
		}
		
		Actor actor = stage.hit(x, y, true);
		if(actor == null || !(actor instanceof Building)){
			return false;
		}
		Building b = (Building) actor;
		if(actor.getY() <= control_height/2){//增加
			System.out.println("增加 " + b.type);
			PlayerBuildingDto dto = playerDto.getBuildings().get(b.type);
			if(dto.getLeftAmount() <= 0){//不够建造物
				return false;
			}
		}
		
		controller.setCanUpdate(false);
		a = b;
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
		controller.setCanUpdate(true);
		Vector3 vector3 = new Vector3(screenX, screenY, 0);  
		camera.unproject(vector3); // 坐标转化  
		float x = vector3.x;
		float y = vector3.y;
		
//		screenY = height - screenY;
		if(y < 0){
			return true;
		}
		
		//校正
		x -= a.getWidth()/2;
		y -= a.getHeight()/2;	
		
		Building b = compute(x,y,a);
		if(b == null){//移除
			a.setPosition(oldx, oldy);//暂时不做移除
			return true;
//			if(oldy == control_height/2 ){//原始的不移除 
//				a.setPosition(oldx, oldy);
//				return true;
//			}
//			if(a.getY()  > control_height/2){ //没有移到控制区域下面不算移除
//				a.setPosition(oldx, oldy);
//				return true;
//			}
//			
//			a.remove();
//			Building.buildingPool.free(a);
//			playerDto.getMap()[oldi][oldj] = 0;
//			
//			Building block = Building.buildingPool.obtain();
//			block.i = oldi;
//			block.j = oldj;
//			block.setPosition(oldx, oldy);
//			buildings.add(block);
//			StringBuilder from = new StringBuilder();
//			from.append(oldi).append("|").append(oldj).append("|").append(0);
//			saveMaptoServer(1,from.toString(),null);
//			return true;
		}
		
		a.setPosition(b.getX(), b.getY());
		a.setIndex(b.i, b.j);
		playerDto.getMap()[b.i][b.j] = oldType; 
		
		if(oldy <= control_height/2){//增加
			if(b.type == 0){
				buildings.removeValue(b, false);
			}else{
				b.remove();
			}
			
			PlayerBuildingDto dto = playerDto.getBuildings().get(b.type);
			if(dto != null){
				System.out.println("已增加 " + b.type);
				dto.setAmount(dto.getAmount() -1);
				dto.setBuildAmount(dto.getBuildAmount() + 1);
			}
			
			Building.buildingPool.free(b);
			Building block = Building.buildingPool.obtain();
			block.init(null,oldType, oldx, oldy, length, length,null);
			stage.addActor(block);
			StringBuilder to = new StringBuilder();
			to.append(b.i).append("|").append(b.j).append("|").append(b.type);
			saveMaptoServer(null,to.toString());
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
		saveMaptoServer(from.toString(),to.toString());
		return true;
	}
	
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(a != null){
			Vector3 vector3 = new Vector3(screenX, screenY, 0);  
			camera.unproject(vector3); // 坐标转化  
			float x = vector3.x;
			float y = vector3.y;
//			screenY = height - screenY;
			if(y < 0){
				return true;
			}
			x = x - a.getWidth()/2;
			y = y - a.getHeight()/2;
			a.setPosition(x, y);
		}
		return true;
	}
	
	/**
	 * compute new position
	 * @param newX
	 * @param newY
	 */
	private Building compute(float x, float y ,Actor a) {
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
			Building b = (Building)at;
			if(b.i == 0){//与原始的不比较
				continue;
			}
			float dst = b.dst(x,y);
			if(dst <= min){
				return b;
			}
		}
		for( int i = 0 ;i< buildings.size ;i++){
			Building b =  buildings.get(i);
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
	private void saveMaptoServer(String from ,String to){
		HashMap<String,Object> parms = new HashMap<String,Object>();
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
		buildings.clear();
		
		if(spriteBatch != null){
			spriteBatch.dispose();
		}
		
		if(font != null){
			font.dispose();
		}
	}
}
