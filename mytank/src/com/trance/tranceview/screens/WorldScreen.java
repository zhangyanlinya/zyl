package com.trance.tranceview.screens;

import java.util.HashMap;
import java.util.Map;

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
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.StringBuilder;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.common.util.JsonUtils;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.trancetank.modules.world.handler.WorldCmd;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.actors.WorldImage;
import com.trance.tranceview.constant.ControlType;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.utils.AssetsManager;
import com.trance.tranceview.utils.FontUtil;
import com.trance.tranceview.utils.RandomUtil;
import com.trance.tranceview.utils.SocketUtil;

public class WorldScreen implements Screen, GestureListener, InputProcessor {
	
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
	private float sw = 480 * BASE;
	private float sh = 800 * BASE;
	public final static Map<String,WorldImage> worldImages = new HashMap<String,WorldImage>();
	
	public WorldScreen(TranceGame tranceGame) {
		this.tranceGame = tranceGame;
		
	}

	@Override
	public void show() {
		if(!init){
			init();
			init = true;
		}
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		GestureDetector gestureHandler = new GestureDetector(this);
		inputMultiplexer.addProcessor(this);
		inputMultiplexer.addProcessor(gestureHandler);
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
		
	}
	
	private void init(){
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		leftX = 0;
		rightX = sw;
		donwY = 0;
		upY = sh;
		
		spriteBatch = new SpriteBatch();
		
		StringBuilder sb = new StringBuilder();
		sb.append(MainActivity.player.getPlayerName());
		if(!MainActivity.worldPlayers.isEmpty()){
			for(PlayerDto dto : MainActivity.worldPlayers.values() ){
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
		
		Image bg = new Image(AssetsManager.getInstance().get("world/bg.jpg",Texture.class));
		float w = bg.getWidth();
		float h = bg.getHeight();
		for(float x = -w * 2 ; x <= sw + w; x += w){//background;
			for(float y = -h * 4 ; y <= sh + h * 4 ; y += h){
				bg = new Image(AssetsManager.getInstance().get("world/bg.jpg",Texture.class));
				bg.setPosition(x, y);
				stage.addActor(bg);
			}
		}
		
		//grass
		for(int i = 0; i < 100; i++){
			int index = RandomUtil.nextInt(5) + 1;
			int x = RandomUtil.nextInt((int)sw);
			int y = RandomUtil.nextInt((int)sh);
			Image grass = new Image(AssetsManager.getInstance().get("world/grass" + index +".png", Texture.class));
			grass.setPosition(x,y);
			stage.addActor(grass);
		}
		for(int i = 0; i < 100; i++){
			int index = RandomUtil.nextInt(4) + 1;
			int x = RandomUtil.nextInt((int)sw);
			int y = RandomUtil.nextInt((int)sh);
			Image grass = new Image(AssetsManager.getInstance().get("world/soil" + index +".png", Texture.class));
			grass.setPosition(x,y);
			stage.addActor(grass);
		}
		
		for(int i = 0; i < 50; i++){
			int index = RandomUtil.nextInt(2) + 1;
			int x = RandomUtil.nextInt((int)sw);
			int y = RandomUtil.nextInt((int)sh);
			Image grass = new Image(AssetsManager.getInstance().get("world/stone" + index +".png", Texture.class));
			grass.setPosition(x,y);
			stage.addActor(grass);
		}
		
		for(int i = 0; i < 500; i++){
			int index = RandomUtil.nextInt(5) + 1;
			int x = RandomUtil.nextInt((int)sw);
			int y = RandomUtil.nextInt((int)sh);
			Image grass = new Image(AssetsManager.getInstance().get("world/tree" + index +".png", Texture.class));
			grass.setPosition(x,y);
			stage.addActor(grass);
		}
	
		
		for(int x = 0; x < BASE; x ++){
			for(int y = 0 ; y < BASE; y ++){
				PlayerDto dto = null;
				if(x == BASE/2 && y == BASE/2){
					dto = MainActivity.player;
				}else{
					dto = MainActivity.getWorldPlayerDto(x, y);
				}
				
				final WorldImage location = new WorldImage(AssetsManager.getInstance().get("world/me.png", Texture.class), font, dto);
				float opx =  x * 480 +(x ^ y) * 20;
				float opy =  y * 800 + ((BASE - x) ^ (BASE - y)) * 40;
				location.setPosition(opx , opy);
				
				if(x == BASE/2 && y == BASE/2){
					location.setColor(255,0,255,1);
				}
				
				String key = new StringBuilder().append(x).append("_").append(y).toString();
				worldImages.put(key, location);
				stage.addActor(location);
				
				
				final int ox = x;
				final int oy = y;
				location.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						PlayerDto dto = location.getPlayerDto();
						if(dto == null){
							Map<String,Object> params = new HashMap<String,Object>();
							params.put("x", ox);
							params.put("y", oy);
							SocketUtil.sendAsync(Request.valueOf(Module.WORLD, WorldCmd.ALLOCATION, params));
							return;
						}
						if(ox == 5 && oy == 5){
							dto.setMyself(true);
							gotoHome();
							return;
						}
						MapData.playerId = dto.getId();
						HashMap<String,Object> params = new HashMap<String,Object>();
						params.put("targetId", dto.getId());
						Response response = SocketUtil.send(Request.valueOf(Module.WORLD, WorldCmd.QUERY_PLAYER, params),true);
						if(response == null){
							return;
						}
						ResponseStatus status = response.getStatus();
						if (status == ResponseStatus.SUCCESS) {
							HashMap<?, ?> result = (HashMap<?, ?>) response.getValue();
							int code = (Integer) result.get("result");
							if (code == 0) {
								if (result.get("mapJson") != null) {
									MapData.map = JsonUtils.jsonString2Object(
											result.get("mapJson").toString(),
											int[][].class);
								}else{
									MapData.map = MapData.baseMap[0].clone();//原始的
								}
								MapData.other = true;
								tranceGame.mapScreen.setPlayerDto(dto);
								tranceGame.setScreen(tranceGame.mapScreen);
							}
						}
					}
				});
			}
		}
	
		//Home
		home = new Image(AssetsManager.getInstance().getControlTextureRegion(ControlType.HOME));
		home.setBounds(10, 10, home.getWidth() + home.getWidth()/2, home.getHeight() + home.getHeight()/2);
		
	}
	
	private void gotoHome(){
		MapData.map = MapData.myMap;
		MapData.other = false;
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
		worldImages.clear();
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		initialScale = zoom;
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	private float leftX;
	private float rightX;
	private float donwY;
	private float upY;
	
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		float cx = camera.position.x ;
		float cy = camera.position.y;
		if(cx < leftX ){
			camera.position.x = leftX;
			return true;
		}
		
		if(cy < donwY ){
			camera.position.y = donwY;
			return true;
		}
		
		if(cx > rightX){
			camera.position.x = rightX;
			return true;
		}
		
		if(cy > upY){
			camera.position.y = upY;
			return true;
		}
		
		camera.translate(-deltaX , deltaY);
		return true;
	}
	
	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	public float zoom = 1.0f;
	public float initialScale = 1.0f;

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// 与pinch对应，也是是一个多点触摸的手势，并且两个手指做出放大的动作
		// Calculate pinch to zoom
		float ratio = initialDistance / distance;

		// Clamp range and set zoom
		zoom = MathUtils.clamp(initialScale * ratio, 0.5f, 2.0f);
		camera.zoom = zoom;
		
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		return false;
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
