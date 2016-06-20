package com.trance.tranceview.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
	private TiledMapRenderer tiledMapRenderer;

	private Stage stage;
	private TiledMap tilemap;
	private List<Image> locations = new ArrayList<Image>();
	private float WIDTH;
	private float HEIGHT;
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private FreeTypeFontGenerator generator;
	private FreeTypeBitmapFontData fontData;

	public WorldScreen(TranceGame tranceGame) {
		this.tranceGame = tranceGame;
	}

	@Override
	public void show() {
		stage = new Stage();
		spriteBatch = new SpriteBatch();
		generator = new FreeTypeFontGenerator(
	               Gdx.files.internal("font/haibao.ttf"));
		
		Set<String> set = new HashSet<String>();
		set.add("点");
		set.add("赞");
		if(!MainActivity.worldPlayers.isEmpty()){
			for(PlayerDto dto : MainActivity.worldPlayers ){
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
		
		TmxMapLoader loader = new TmxMapLoader();
		try {
			tilemap = loader.load("world/world.tmx");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	
		MapLayers layers = tilemap.getLayers();
		
		for (MapLayer layer : layers) {
			if (layer.getName().equals("actors")) {
				MapObjects objs = layer.getObjects();
				int index = 0;
				for(MapObject obj : objs){
					initMapObject(obj, index);
					index ++;
				}
			}
		}

		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(WIDTH, HEIGHT);
		camera.setToOrtho(false, WIDTH/2, HEIGHT/2);

		tiledMapRenderer = new OrthogonalTiledMapRenderer(tilemap);
		
		//Home
		Image image = new Image(
				AssetsManager.getControlTextureRegion(ControlType.HOME));
		image.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				MapData.map = MapData.myMap;
				MapData.other = false;
				tranceGame.setScreen(tranceGame.mapScreen);
				dispose();
			}

		});
		image.setPosition(0, 0);
		stage.addActor(image);
	
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		GestureDetector gestureHandler = new GestureDetector(this);
		inputMultiplexer.addProcessor(gestureHandler);
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);

	}
	
	/**
	 * 初始化世界地图界面数据
	 * @param mo
	 */
	private void initMapObject(MapObject mo, final int index){
		if(mo == null){
			return;
		}
		
		RectangleMapObject rmo = (RectangleMapObject) mo;
		
		String fileName = "world/f-28.png";
		
		Image location = new Image(AssetsManager.assetManager.get(fileName, Texture.class));
		float x = rmo.getRectangle().x;
		float y = rmo.getRectangle().y;
		location.setPosition(x , y);
		//设置坐标
		PlayerDto target = MainActivity.getWorldPlayerDto(index);
		if(target != null){
			target.setX(x);
			target.setY(y);
		}
		
		location.addListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
					PlayerDto target = MainActivity.getWorldPlayerDto(index);
					if(target == null){
						MainActivity.socket.sendAsync(Request.valueOf(Module.WORLD, WorldCmd.ALLOCATION, null));
						Music music = AssetsManager.assetManager.get("audio/get_barrett.mp3");
						music.play();
						return false;
					}
					
					MapData.playerId = target.getId();
					HashMap<String,Object> params = new HashMap<String,Object>();
					params.put("targetId", target.getId());
					Response response = MainActivity.socket.send(Request.valueOf(Module.WORLD, WorldCmd.QUERY_PLAYER, params));
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
									MapData.map = MapData.baseMap[0];//原始的
								}
								MapData.other = true;
								tranceGame.setScreen(tranceGame.mapScreen);
								dispose();
							}
						}
					}
				return true;
			}
		});
		
		stage.addActor(location);
		locations.add(location);
	}
	
	@Override
	public void dispose() {
		tilemap.dispose();
		stage.dispose();
		locations.clear();
		spriteBatch.dispose();
		font.dispose();
		
	}

	@Override
	public void pause() {

	}

	@Override
	public void render(float delatime) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);

		camera.update();
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		stage.draw();
		spriteBatch.begin();
		font.draw(spriteBatch, "点赞： " + MainActivity.player.getUp() ,0,HEIGHT);
		if(!MainActivity.worldPlayers.isEmpty()){
			for(PlayerDto dto : MainActivity.worldPlayers ){
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
	public boolean touchDown(float x, float y, int pointer, int button) {
//		initialScale = zoom;
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
		float xx = camera.position.x - deltaX;
		float yy = camera.position.y + deltaY;
//		if (xx < WIDTH/2 || xx > WIDTH + 200 || yy < HEIGHT/2 - 60  || yy > HEIGHT/2+60) { //超过边界不再移动
//			 return false;
//		}
		camera.position.set(xx, yy, 0);
		for(Image location : locations){
			float newX = location.getX() + deltaX;
			float newY = location.getY() - deltaY;
			location.setPosition(newX, newY);
		}
		
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
		
		for(Image location : locations){
			location.setScale(zoom);
		}
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

}
