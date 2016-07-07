package com.trance.tranceview.screens;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.common.util.JsonUtils;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.trancetank.modules.world.handler.WorldCmd;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.constant.ControlType;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.utils.AssetsManager;
import com.trance.tranceview.utils.CharUtil;

public class WorldScreen implements Screen, GestureListener {

	private TranceGame tranceGame;
	private OrthographicCamera camera;
	private Stage stage;
	private TiledMap tilemap;
	private float WIDTH;
	private float HEIGHT;
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private FreeTypeFontGenerator generator;
	private FreeTypeBitmapFontData fontData;
	private Music music ;
	private boolean init;
	
	private void init(){
		spriteBatch = new SpriteBatch();
		generator = new FreeTypeFontGenerator(
	               Gdx.files.internal("font/haibao.ttf"));
		
		Set<String> set = new HashSet<String>();
		set.add("点");
		set.add("赞");
		if(!MainActivity.worldPlayers.isEmpty()){
			for(PlayerDto dto : MainActivity.worldPlayers.values() ){
				String name = dto.getPlayerName();
				for(int i = 0; i < name.length(); i++){
					char c = name.charAt(i);
					if(CharUtil.isChinese(c)){
						set.add(String.valueOf(c));
					}
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for(String s : set){
			sb.append(s);
		}
		
		fontData = generator.generateData(35, FreeTypeFontGenerator.DEFAULT_CHARS
	               + sb.toString(), false);
		
		font = new BitmapFont(fontData, fontData.getTextureRegions(), false);
		font.setColor(Color.RED);
		generator.dispose();//别忘记释放
		
		
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(WIDTH, HEIGHT);
		camera.setToOrtho(false, WIDTH/2, HEIGHT/2);
		stage = new Stage();
		
		for(int x = 0; x < 1000; x += 200 ){
			for(int y = 0 ; y < 1000; y += 200){
				Image location = new Image(AssetsManager.getInstance().get("world/f-28.png", Texture.class));
				location.setPosition(x , y);
				stage.addActor(location);
				final PlayerDto dto = MainActivity.getWorldPlayerDto(x, y);
				final int ox = x;
				final int oy = y;
				location.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						if(dto != null){
							dto.setX(ox);
							dto.setY(oy);
							MapData.playerId = dto.getId();
							HashMap<String,Object> params = new HashMap<String,Object>();
							params.put("targetId", dto.getId());
							Response response = SimpleSocketClient.socket.send(Request.valueOf(Module.WORLD, WorldCmd.QUERY_PLAYER, params));
							if(response != null){
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
										tranceGame.setScreen(tranceGame.mapScreen);
									}
								}
							}
						}else{
							Map<String,Object> params = new HashMap<String,Object>();
							params.put("x", ox);
							params.put("y", oy);
							SimpleSocketClient.socket.sendAsync(Request.valueOf(Module.WORLD, WorldCmd.ALLOCATION, params));
						}
					}
				});
				
			}


		}
		
		//Home
		Image image = new Image(
				AssetsManager.getInstance().getControlTextureRegion(ControlType.HOME));
		image.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				MapData.map = MapData.myMap.clone();
				MapData.other = false;
				tranceGame.setScreen(tranceGame.mapScreen);
			}

		});
		image.setPosition(0, 0);
		stage.addActor(image);
	}
	
	public WorldScreen(final TranceGame tranceGame) {
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
		inputMultiplexer.addProcessor(gestureHandler);
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	@Override
	public void pause() {

	}

	@Override
	public void render(float delatime) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);

		camera.update();
		stage.draw();
		spriteBatch.begin();
		if(MainActivity.player != null){
			font.draw(spriteBatch, "点赞： " + MainActivity.player.getUp() ,0,HEIGHT);
		}
		if(!MainActivity.worldPlayers.isEmpty()){
			for(PlayerDto dto : MainActivity.worldPlayers.values() ){
				int length = dto.getPlayerName().length();
				String name = dto.getPlayerName().substring(0, length > 6 ? 6 : length);
				font.draw(spriteBatch, name + ": " + dto.getUp(), dto.getX(), dto.getY());
			}
		}
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
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
//		initialScale = zoom;
		System.out.println("x:  ---> "+x);
		System.out.println("y:  ---> "+y);
		System.out.println("pointer:  ---> "+pointer);
		System.out.println("button:  ---> "+button);
		System.out.println();
		
		int ox = (int) (x/10);
		int oy = (int) (y/10);
		System.out.println("ox:  ---> "+ox);
		System.out.println("oy:  ---> "+oy);
		PlayerDto dto = MainActivity.getWorldPlayerDto(ox,oy);
		if(dto == null){
			return false;
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("x", ox);
		params.put("y", ox);
		SimpleSocketClient.socket.sendAsync(Request.valueOf(Module.WORLD, WorldCmd.ALLOCATION, params));
//		music = AssetsManager.getInstance().get("audio/get_barrett.mp3");
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

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		float xx = camera.position.x + deltaX;
		float yy = camera.position.y + deltaY;
//		if (xx < WIDTH/2 || xx > WIDTH + 200 || yy < HEIGHT/2 - 60  || yy > HEIGHT/2+60) { //超过边界不再移动
//			 return false;
//		}
		camera.position.set(xx, yy, 0);
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
		zoom = MathUtils.clamp(initialScale * ratio, 0.5f, 1.0f);
		camera.zoom = zoom;
		
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

}
