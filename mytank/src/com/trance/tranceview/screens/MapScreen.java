package com.trance.tranceview.screens;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.microedition.khronos.opengles.GL10;

import com.alibaba.fastjson.JSON;
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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.model.Result;
import com.trance.trancetank.modules.army.model.ArmyDto;
import com.trance.trancetank.modules.battle.handler.BattleCmd;
import com.trance.trancetank.modules.building.model.BuildingDto;
import com.trance.trancetank.modules.mapdata.handler.MapDataCmd;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.trancetank.modules.reward.result.ValueResultSet;
import com.trance.trancetank.modules.reward.service.RewardService;
import com.trance.trancetank.modules.world.handler.WorldCmd;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.Building;
import com.trance.tranceview.actors.MapImage;
import com.trance.tranceview.actors.ResImage;
import com.trance.tranceview.constant.ControlType;
import com.trance.tranceview.constant.UiType;
import com.trance.tranceview.dialog.DialogArmyStage;
import com.trance.tranceview.dialog.DialogBuildingStage;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.textinput.RenameInputListener;
import com.trance.tranceview.utils.FontUtil;
import com.trance.tranceview.utils.FormulaUtil;
import com.trance.tranceview.utils.MsgUtil;
import com.trance.tranceview.utils.RandomUtil;
import com.trance.tranceview.utils.ResUtil;
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
	private Image toChange;
	private Image toTrain;
	private Image toUpBuilding;
	private Image rename;
	private boolean init;
	private TextInputListener listener;
	private PlayerDto playerDto;
	private OrthographicCamera camera;
	private Image bg;
//	private GestureController controller;
	
//	private ConcurrentMap<Integer,BuildingDto> buildings = new ConcurrentHashMap<Integer,BuildingDto>();
//	private ConcurrentMap<Integer,CoolQueueDto> coolQueues = new ConcurrentHashMap<Integer,CoolQueueDto>();
	public ShapeRenderer shapeRenderer;
	
	private InputMultiplexer inputMultiplexer;
    public DialogArmyStage dialogArmyStage;
    public DialogBuildingStage dialogBuildingStage;
	
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
		
		
		spriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer(); 
		
		dialogArmyStage = new DialogArmyStage(game);
		dialogBuildingStage = new DialogBuildingStage(game);
		
		bg = new MapImage(ResUtil.getInstance().get("world/bg.jpg",Texture.class));
		
		int side = width/6;
		
		toWorld = new Image(ResUtil.getInstance().getControlTextureRegion(ControlType.WORLD));
		toWorld.setBounds(0, 0, side, side);
		toWorld.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				toWorld();
			}
		});
		
		//Rename
		listener = new RenameInputListener();
		rename = new Image(ResUtil.getInstance().getControlTextureRegion(ControlType.RENAME));
		rename.setBounds(side * 1, 0, side, side);
		rename.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.input.getTextInput(listener, "请输入新名字", MainActivity.player.getPlayerName());
			}
		});
		
		toTrain = new Image(ResUtil.getInstance().getUi(UiType.TRAIN));
		toTrain.setBounds(side * 2, 0, side, side);
		toTrain.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				train();
			}
		});
		
		toUpBuilding = new Image(ResUtil.getInstance().getUi(UiType.TRAIN));
		toUpBuilding.setBounds(side * 3, 0, side, side);
		toUpBuilding.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				upBuilding();
			}
		});
		

		toChange = new Image(ResUtil.getInstance().getUi(UiType.CHANGE));
		toChange.setBounds(side * 4, 0, side, side);
		toChange.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				change();
			}
		});
		
		attack = new Image(ResUtil.getInstance().getControlTextureRegion(ControlType.ATTACK));
		attack.setBounds(side * 5, 0, side, side);
		attack.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				attack();
			}
		});
		
		
//		coolQueues = playerDto.getCoolQueues();
//		buildings = playerDto.getBuildings();
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
		//文字 
		font = FontUtil.getFont(30, "可拖动建筑放置等级金银币粮食" + playerDto.getPlayerName(), Color.WHITE);
		
		stage.clear();
		float w = bg.getWidth();
		float h = bg.getHeight();
		for(float x = -w ; x < stage.getWidth(); x += w){//background;
			for(float y = -h ; y < stage.getHeight() ; y += h){
				bg = new MapImage(ResUtil.getInstance().get("world/bg.jpg",Texture.class));
				bg.setPosition(x, y);
				stage.addActor(bg);
			}
		}
		
		for(int i = 0 ; i < 5; i ++){
			int index = RandomUtil.nextInt(4) + 1;
			int x = RandomUtil.nextInt((int)width);
			int y = RandomUtil.nextInt((int)height);
			Image grass = new MapImage(ResUtil.getInstance().get("world/soil" + index +".png", Texture.class));
			grass.setPosition(x, y);
			stage.addActor(grass);
		}
		
		initMap();//初始化地图
		if(isEdit()){
			refreshPlayerDtoData();
			refreshLeftBuiding();
			stage.addActor(rename);
			stage.addActor(toTrain);
			stage.addActor(toUpBuilding);
		}else{
			stage.addActor(toChange);
			stage.addActor(attack);
		}
		initPlayerInfo();
		
		stage.addActor(toWorld);
		
		inputMultiplexer = new InputMultiplexer(); 
//		controller = new GestureController(camera, 0, width * 2, 0, height * 2);
//		camera.position.set(width/2, height/2, 0);
//		GestureDetector gestureHandler = new GestureDetector(controller);
//		inputMultiplexer.addProcessor(gestureHandler);
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(this);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	

	/**
	 *  地图是否可编辑
	 * @return
	 */
	private boolean isEdit(){
		return(playerDto.isMyself());//
	}
	
	
	/**
	 * attack other player
	 */
	private void attack(){
		Map<Integer,ArmyDto> armys = MainActivity.player.getArmys();
		if(armys == null || armys.isEmpty()){
			return;
		}
		
		HashMap<String,Object> params = new HashMap<String,Object>();
		params.put("x", playerDto.getX());
		params.put("y", playerDto.getY());
		Request request = Request.valueOf(Module.BATTLE, BattleCmd.START_BATTLE, params);
		Response response = SocketUtil.send(request, true);
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
			return;
		}
		
		byte[] bytes = response.getValueBytes();
		String text = new String(bytes);
		@SuppressWarnings("unchecked")
		HashMap<String, Object> result = JSON.parseObject(text, HashMap.class);
		Object codeObject = result.get("result");
		int code = Integer.valueOf(String.valueOf(codeObject));
		if(code != Result.SUCCESS){
			MsgUtil.showMsg(Module.BATTLE, code);
			return;
		}
		
		Object o = result.get("content");
		if(o != null){
			ValueResultSet valueResultSet =  JSON.parseObject(o.toString(), ValueResultSet.class);
			RewardService.executeRewards(valueResultSet);
		}
		
		GameScreen.playerDto = playerDto;
		game.setScreen(game.gameScreen);
	}
	
	/**
	 * change player
	 */
	private void change(){
		HashMap<String,Object> params = new HashMap<String,Object>();
		params.put("x", playerDto.getX());
		params.put("y", playerDto.getY());
		Request request = Request.valueOf(Module.WORLD, WorldCmd.CHANGE_PLAYER, params);
		Response response = SocketUtil.send(request, true);
		if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
			return;
		}
		byte[] bytes = response.getValueBytes();
		String text = new String(bytes);
		@SuppressWarnings("unchecked")
		HashMap<String, Object> result = JSON.parseObject(text, HashMap.class);
		Object codeObject = result.get("result");
		int code = Integer.valueOf(String.valueOf(codeObject));
		if(code != Result.SUCCESS){
			MsgUtil.showMsg(Module.WORLD, code);
			return;
		}
		
		Object pobj = result.get("content");
		if(pobj != null){
			PlayerDto newPlayerDto = JSON.parseObject(pobj.toString(), PlayerDto.class);
			newPlayerDto.setX(playerDto.getX());
			newPlayerDto.setY(playerDto.getY());
			WorldScreen.setWorldPlayerDto(playerDto.getX(), playerDto.getY(), newPlayerDto);
			playerDto = newPlayerDto;
			int[][] map = null;
			Object mobj = result.get("mapdata");
			if (mobj == null) {
				map = MapData.clonemap();
			}else{
				map = JSON.parseObject(mobj.toString(), int[][].class);
			}
			playerDto.setMap(map);
			show();
		}
	}
	
	private void train(){
		setArmyDailog(true);
	}
	
	private void upBuilding(){
		setBuildingDailog(true);
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
			font.draw(spriteBatch,"可拖动建筑放置",length,control_height -length * 2);
		}
		if(playerDto.isMyself()){
			font.draw(spriteBatch, MainActivity.player.getPlayerName(),0,height - length);
			font.draw(spriteBatch, MainActivity.player.getExperience()+"/"+levelExp ,0,height - length * 2);
		}else{
			font.draw(spriteBatch, playerDto.getPlayerName(),0,height - length);
		}
		spriteBatch.end();
		
		if(dialogArmyStage.isVisible()){
			dialogArmyStage.act();
			dialogArmyStage.draw();
		}
		if(dialogBuildingStage.isVisible()){
			dialogBuildingStage.act();
			dialogBuildingStage.draw();
		}
	}
	
	public void setArmyDailog(boolean visible) {
		if(visible){
			dialogArmyStage.show();
			inputMultiplexer.addProcessor(dialogArmyStage);
			inputMultiplexer.removeProcessor(stage);
			inputMultiplexer.removeProcessor(this);
		}else{
			dialogArmyStage.hide();
			inputMultiplexer.addProcessor(stage);
			inputMultiplexer.addProcessor(this);
			inputMultiplexer.removeProcessor(dialogArmyStage);
		}
	}
	
	public void setBuildingDailog(boolean visible) {
		if(visible){
			dialogBuildingStage.show();
			inputMultiplexer.addProcessor(dialogBuildingStage);
			inputMultiplexer.removeProcessor(stage);
			inputMultiplexer.removeProcessor(this);
		}else{
			dialogBuildingStage.hide();
			inputMultiplexer.addProcessor(stage);
			inputMultiplexer.addProcessor(this);
			inputMultiplexer.removeProcessor(dialogBuildingStage);
		}
	}
	
	private long levelExp;
	
	private void initPlayerInfo(){
		int side = width / 5;
		ResImage level = new ResImage(ResUtil.getInstance().getUi(UiType.LEVEL),font, String.valueOf( playerDto.getLevel()));
		level.setBounds(side, height - length , length, length);
		stage.addActor(level);
		ResImage gold = new ResImage(ResUtil.getInstance().getUi(UiType.GOLD),font, String.valueOf( playerDto.getGold()));
		gold.setBounds(side * 2, height - length, length, length);
		stage.addActor(gold);
		ResImage silver = new ResImage(ResUtil.getInstance().getUi(UiType.SILVER),font, String.valueOf( playerDto.getSilver()));
		silver.setBounds(side * 3,  height - length, length, length);
		stage.addActor(silver);
		ResImage foods = new ResImage(ResUtil.getInstance().getUi(UiType.FOODS),font, String.valueOf( playerDto.getFoods()));
		foods.setBounds(side * 4, height - length, length, length);
		stage.addActor(foods);
	}
	
	public void refreshPlayerDtoData(){
		levelExp = FormulaUtil.getExpByLevel(playerDto.getLevel() + 1);// 下一级所需经验
	}
	
	// 初始化关卡地图
	public void initMap() {
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
					Image grass = new MapImage(ResUtil.getInstance().get("world/tree" + index +".png", Texture.class));
					grass.setPosition(x, y + length);
					stage.addActor(grass);
				}else if(i == map.length-1){
					int index = RandomUtil.nextInt(5) + 1;
					Image grass = new MapImage(ResUtil.getInstance().get("world/tree" + index +".png", Texture.class));
					grass.setPosition(x, y - length * 2);
					stage.addActor(grass);
				}
				
				if(j == 0){
					int index = RandomUtil.nextInt(5) + 1;
					Image grass = new MapImage(ResUtil.getInstance().get("world/tree" + index +".png", Texture.class));
					grass.setPosition(x - length, y);
					stage.addActor(grass);
				}else if(j == map[i].length -1){
					int index = RandomUtil.nextInt(5) + 1;
					Image grass = new MapImage(ResUtil.getInstance().get("world/tree" + index +".png", Texture.class));
					grass.setPosition(x + length, y);
					stage.addActor(grass);
				}
				
				Building block = Building.buildingPool.obtain();
				block.setIndex(i, j);
				block.init(null,type, x, y, length,length,null);
				stage.addActor(block);
			}
		}
	}
	
	public void refreshLeftBuiding() {
		float side = width/10;
		int i = 0;
		Array<Actor> actors = stage.getActors();
		for(Actor ac :actors){
			if(ac instanceof Building){
				Building build = (Building)ac;
				if(build.getY() <= control_height - length){
					build.remove();
				}
			}
		}
	
		for(Entry<Integer, BuildingDto> e : MainActivity.player.getBuildings().entrySet()){
			BuildingDto dto = e.getValue();
			if(dto.getLeftAmount() <= 0){
				continue;
			}
			Building buiding = Building.buildingPool.obtain();
			int rate  = i % 5;
			float x = rate * side + length;
			int rate2 = i/5 + 1;
			float y = control_height - (length * 2 + rate2 * length * 2 );
			buiding.init(null,dto.getId(), x, y, length,length,null, font, dto);
			stage.addActor(buiding);
			i++;
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
		
		if(y < 0){
			return false;
		}
		
		Actor actor = stage.hit(x, y, true);
		if(actor == null || !(actor instanceof Building)){
			return false;
		}
		
		Building b = (Building) actor;
		if(actor.getY() <= control_height - length){//增加
			BuildingDto dto = playerDto.getBuildings().get(b.type);
//			updateBuilding(dto);
			if(dto.getLeftAmount() <= 0){//不够建造物
				return false;
			}
		}
		
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
		Vector3 vector3 = new Vector3(screenX, screenY, 0);  
		camera.unproject(vector3); // 坐标转化  
		float x = vector3.x;
		float y = vector3.y;
		
		if(y < 0){
			a.setPosition(oldx, oldy);
			a = null;
			return true;
		}
		
		Building b = compute(x,y);
		if(b == null){//移除
//			System.out.println("b = null");
			a.setPosition(oldx, oldy);//暂时不做移除
			a = null;
			return true;
		}
		
		if(oldy <= control_height - length){//增加
//			System.out.println("开始增加...");
			if(b.type != 0){
				a.setPosition(oldx, oldy);//不覆盖已经占坑的
				a = null;
				return false;
			}
			
			b.remove();
			Building.buildingPool.free(b);
			
			BuildingDto dto = playerDto.getBuildings().get(oldType);
			if(dto == null || dto.getLeftAmount() <= 0){
				a.setPosition(oldx, oldy);
				a = null;
				return false;
			}
			dto.setBuildAmount(dto.getBuildAmount() + 1);
			//增加
			a.setPosition(b.getX(), b.getY());
			a.setIndex(b.i, b.j);
			playerDto.getMap()[b.i][b.j] = oldType; 
			
			if(dto.getLeftAmount() > 0){
				Building block = Building.buildingPool.obtain();
				block.init(null,oldType, oldx, oldy, length, length, null, font, dto);
				stage.addActor(block);
			}
			
			StringBuilder to = new StringBuilder();
			to.append(b.i).append("|").append(b.j).append("|").append(oldType);
			saveMaptoServer(null,to.toString());
			a = null;
			return true;
		}
		
		if(oldType == b.type){
			a.setPosition(oldx, oldy);
			a = null;
			return false; //类型一样不用上传
		}
		
		////替换
		a.setPosition(b.getX(), b.getY());
		a.setIndex(b.i, b.j);
		playerDto.getMap()[b.i][b.j] = oldType; 
		
		b.setPosition(oldx, oldy);
		b.setIndex(oldi, oldj);
		playerDto.getMap()[oldi][oldj] = b.type;
		
//		System.out.println(oldType  +" 与 " + b.type +" 进行了交换~ ");
		
		StringBuilder from = new StringBuilder();
		from.append(oldi).append("|").append(oldj).append("|").append(oldType);
		StringBuilder to = new StringBuilder();
		to.append(a.i).append("|").append(a.j).append("|").append(b.type);
		saveMaptoServer(from.toString(),to.toString());
		a = null;
		return true;
	}
	
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(a != null){
			Vector3 vector3 = new Vector3(screenX, screenY, 0);  
			camera.unproject(vector3); // 坐标转化  
			float x = vector3.x;
			float y = vector3.y;
			if(y < 0){
				return true;
			}
			x = x - a.getWidth()/2;
			y = y - a.getHeight()/2;
			a.setTouchable(Touchable.disabled);//不让点中先
			a.setPosition(x, y);
		}
		return true;
	}
	
	/**
	 * compute new position
	 * @param newX
	 * @param newY
	 */
	private Building compute(float x, float y) {
		Actor at = stage.hit(x, y, true);
		a.setTouchable(Touchable.enabled);//比较后就可以点了
		if(at == null){
			return null;
		}
		if(at.getX() == oldx && at.getY() == oldy){//自身
			return null;
		}
		if(!(at instanceof Building)){
			System.out.println("at not instanceof Building!");
			return null;
		}
		Building b = (Building)at;
		if(b.getY() <= control_height - length){//与原始的不比较
			System.out.println("b.getY() <= control_height - length");
			return null;
		}
		return b;
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
		
		if(spriteBatch != null){
			spriteBatch.dispose();
		}
		
		if(shapeRenderer!= null){
			shapeRenderer.dispose();
		}
		
		if(font != null){
			font.dispose();
		}
		if(dialogArmyStage != null){
			dialogArmyStage.dispose();
		}
		if(dialogBuildingStage != null){
			dialogBuildingStage.dispose();
		}
	}

}
