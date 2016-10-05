package com.trance.tranceview.screens;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.StringBuilder;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.model.Result;
import com.trance.trancetank.modules.dailyreward.handler.DailyRewardCmd;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.trancetank.modules.reward.result.ValueResultSet;
import com.trance.trancetank.modules.reward.service.RewardService;
import com.trance.trancetank.modules.world.handler.WorldCmd;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.WorldImage;
import com.trance.tranceview.constant.ControlType;
import com.trance.tranceview.controller.GestureController;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.utils.ResUtil;
import com.trance.tranceview.utils.FontUtil;
import com.trance.tranceview.utils.MsgUtil;
import com.trance.tranceview.utils.RandomUtil;
import com.trance.tranceview.utils.SocketUtil;

public class WorldScreen implements Screen, InputProcessor {
	
	private final static int BASE = 10;
	private TranceGame tranceGame;
	private OrthographicCamera camera;
	private Stage stage;
	private TiledMap tilemap;
	private float WIDTH;
	private float HEIGHT;
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private Music music ;
	private boolean init;
	private Image home;
	private Image dailyReward;
	private float sw = 480 * BASE;
	private float sh = 800 * BASE;
	public final static Map<String,WorldImage> locations = new HashMap<String,WorldImage>();
	public final static Map<String,PlayerDto> playerDtos = new HashMap<String,PlayerDto>();
	
	public WorldScreen(TranceGame tranceGame) {
		this.tranceGame = tranceGame;
	}
	
	public static PlayerDto getWorldPlayerDto(int x, int y) {
		String key = createKey(x, y);
		return playerDtos.get(key);
	}
	
	public static void setWorldPlayerDto(int x, int y, PlayerDto newPlayerDto) {
		String key = createKey(x, y);
		playerDtos.put(key, newPlayerDto);
		locations.get(key).setPlayerDto(newPlayerDto);
	}
	
	public static void remove(int x, int y){
		String key = createKey(x, y);
		playerDtos.remove(key);
		locations.get(key).setPlayerDto(null);
	}
	
	public static String createKey(int x ,int y){
		return new StringBuilder().append(x).append("_").append(y).toString();
	}

	@Override
	public void show() {
		if(!init){
			init();
			init = true;
		}
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		GestureController controller = new GestureController(camera, 0, sw, 0, sh);
		GestureDetector gestureHandler = new GestureDetector(controller);
		inputMultiplexer.addProcessor(gestureHandler);
		inputMultiplexer.addProcessor(this);
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
		
	}
	
	private void init(){
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		
		spriteBatch = new SpriteBatch();
		
		StringBuilder sb = new StringBuilder();
		sb.append(MainActivity.player.getPlayerName());
		if(!playerDtos.isEmpty()){
			for(PlayerDto dto : playerDtos.values() ){
				String name = dto.getPlayerName();
				sb.append(name);
			}
		}
		
		font = FontUtil.getInstance().getFont(25, sb.toString(), Color.WHITE);;

		camera = new OrthographicCamera(WIDTH, HEIGHT);
		stage = new Stage(sw, sh);
		camera.setToOrtho(false, WIDTH, HEIGHT);
		camera.translate(sw / 2 - 480, sh / 2 - 800);
		stage.setCamera(camera);
		
		Image bg = new Image(ResUtil.getInstance().get("world/bg.jpg",Texture.class));
		float w = bg.getWidth();
		float h = bg.getHeight();
		for(float x = -w * 2 ; x <= sw + w; x += w){//background;
			for(float y = -h * 4 ; y <= sh + h * 4 ; y += h){
				bg = new Image(ResUtil.getInstance().get("world/bg.jpg",Texture.class));
				bg.setPosition(x, y);
				stage.addActor(bg);
			}
		}
		
		//grass
		for(int i = 0; i < 100; i++){
			int index = RandomUtil.nextInt(5) + 1;
			int x = RandomUtil.nextInt((int)sw);
			int y = RandomUtil.nextInt((int)sh);
			Image grass = new Image(ResUtil.getInstance().get("world/grass" + index +".png", Texture.class));
			grass.setPosition(x,y);
			stage.addActor(grass);
		}
		for(int i = 0; i < 100; i++){
			int index = RandomUtil.nextInt(4) + 1;
			int x = RandomUtil.nextInt((int)sw);
			int y = RandomUtil.nextInt((int)sh);
			Image grass = new Image(ResUtil.getInstance().get("world/soil" + index +".png", Texture.class));
			grass.setPosition(x,y);
			stage.addActor(grass);
		}
		
		for(int i = 0; i < 50; i++){
			int index = RandomUtil.nextInt(2) + 1;
			int x = RandomUtil.nextInt((int)sw);
			int y = RandomUtil.nextInt((int)sh);
			Image grass = new Image(ResUtil.getInstance().get("world/stone" + index +".png", Texture.class));
			grass.setPosition(x,y);
			stage.addActor(grass);
		}
		
		for(int i = 0; i < 500; i++){
			int index = RandomUtil.nextInt(5) + 1;
			int x = RandomUtil.nextInt((int)sw);
			int y = RandomUtil.nextInt((int)sh);
			Image grass = new Image(ResUtil.getInstance().get("world/tree" + index +".png", Texture.class));
			grass.setPosition(x,y);
			stage.addActor(grass);
		}
	
		
		for(int x = 0; x < BASE; x ++){
			for(int y = 0 ; y < BASE; y ++){
				PlayerDto dto = null;
				if(x == BASE/2 && y == BASE/2){
					dto = MainActivity.player;
				}else{
					dto = getWorldPlayerDto(x, y);
				}
				
				final WorldImage location = new WorldImage(ResUtil.getInstance().get("world/me1.png", Texture.class), font, dto);
				float opx =  x * 480 +(x ^ y) * 20;
				float opy =  y * 800 + ((BASE - x) ^ (BASE - y)) * 40;
				location.setPosition(opx , opy);
				
				if(x == BASE/2 && y == BASE/2){
					location.setColor(Color.WHITE);
				}
				
				String key = createKey(x, y);
				locations.put(key, location);
				
				stage.addActor(location);
				
				final int ox = x;
				final int oy = y;
				location.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						PlayerDto dto = location.getPlayerDto();
						if(dto != null ){
							if( ox == 5 && oy == 5){
								dto.setMyself(true);
								location.setColor(Color.MAGENTA);
								gotoHome();
							}else{//spy get the map
								HashMap<String,Object> params = new HashMap<String,Object>();
								params.put("x", ox);
								params.put("y", oy);
								Response response = SocketUtil.send(Request.valueOf(Module.WORLD, WorldCmd.SPY, params),true);
								if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
									MsgUtil.showMsg("network error!");
									return;
								}
								byte[] bytes = response.getValueBytes();
								String text = new String(bytes);
								@SuppressWarnings("unchecked")
								HashMap<String, Object> result = JSON.parseObject(text,HashMap.class);
								int code = (Integer) result.get("result");
								if(code != Result.SUCCESS){
									MsgUtil.showMsg(Module.WORLD, code);
									return;
								}
								Object mobj = result.get("content");
								if (mobj != null) {
									int[][] map = JSON.parseObject(	mobj.toString(),int[][].class);
									dto.setMap(map);
								}else{
									dto.setMap(MapData.baseMap.clone());
								}
								dto.setX(ox);
								dto.setY(oy);
								location.setPlayerDto(dto);
								tranceGame.mapScreen.setPlayerDto(dto);
								tranceGame.setScreen(tranceGame.mapScreen);
							}
						}else{
							HashMap<String,Object> params = new HashMap<String,Object>();
							params.put("x", ox);
							params.put("y", oy);
							Response response = SocketUtil.send(Request.valueOf(Module.WORLD, WorldCmd.ALLOCATION, params),true);
							if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
								MsgUtil.showMsg("network error!");
								return;
							}
							byte[] bytes = response.getValueBytes();
							String text = new String(bytes);
							@SuppressWarnings("unchecked")
							HashMap<String, Object> result = JSON.parseObject(text,HashMap.class);
							int code = (Integer) result.get("result");
							if(code != Result.SUCCESS){
								MsgUtil.showMsg(Module.WORLD, code);
								return;
							}
							
							Object pobj = result.get("content");
							dto = JSON.parseObject(pobj.toString(), PlayerDto.class);
							location.setPlayerDto(dto);
					   }
					}
				});
			}
		}
	
		//Home
		home = new Image(ResUtil.getInstance().getControlTextureRegion(ControlType.HOME));
		home.setBounds(10, 10, home.getWidth() + home.getWidth()/2, home.getHeight() + home.getHeight()/2);
		
		//Home
		dailyReward = new Image(ResUtil.getInstance().getControlTextureRegion(ControlType.HOME));
		int x = RandomUtil.betweenValue(20, 480);
		int y = RandomUtil.betweenValue(20, 800);
		dailyReward.setBounds(x, y, dailyReward.getWidth() + dailyReward.getWidth()/2, dailyReward.getHeight() + dailyReward.getHeight()/2);
		stage.addActor(dailyReward);
		
		dailyReward.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Response response = SocketUtil.send(Request.valueOf(Module.DAILY_REWARD, DailyRewardCmd.GET_DAILY_REWARD, null),true);
				if(response == null || response.getStatus() != ResponseStatus.SUCCESS){
					MsgUtil.showMsg("网络连接失败");
					return;
				}
				
				byte[] bytes = response.getValueBytes();
				String text = new String(bytes);
				@SuppressWarnings("unchecked")
				HashMap<String, Object> result = JSON.parseObject(text,HashMap.class);
				int code = (Integer) result.get("result");
				if(code != Result.SUCCESS){
					MsgUtil.showMsg(Module.DAILY_REWARD, code);
					return;
				}
				
				Object reward = result.get("content");
				if(reward != null){
					ValueResultSet valueResultSet = JSON.parseObject(reward.toString(), ValueResultSet.class);
					RewardService.executeRewards(valueResultSet);
				}
			}
			
		});
		
	}
	
	private void gotoHome(){
		tranceGame.mapScreen.setPlayerDto(MainActivity.player);
		tranceGame.setScreen(tranceGame.mapScreen);
	}
	
	@Override
	public void pause() {

	}

	@Override
	public void render(float delatime) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		stage.draw();
		spriteBatch.begin();
		home.draw(spriteBatch, 1);
		spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		
	}
	
	@Override
	public void dispose() {
		if(!init){
			return;
		}
		init = false;
		if(tilemap != null)
			tilemap.dispose();
		if(stage !=  null)
			stage.dispose();
		if(spriteBatch != null)
			spriteBatch.dispose();
		if(font != null)
			font.dispose();
		if(music != null){
			music.dispose();
		}
//		worldImages.clear();
		playerDtos.clear();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(screenX < 150 && screenY > HEIGHT - 150 ){
			gotoHome();
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
