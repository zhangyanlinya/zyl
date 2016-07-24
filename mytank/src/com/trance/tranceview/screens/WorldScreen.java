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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
import com.trance.tranceview.utils.SocketUtil;

public class WorldScreen implements Screen, GestureListener, InputProcessor {

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
	private Dialog loading;
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
		spriteBatch = new SpriteBatch();
		
		StringBuilder sb = new StringBuilder("点赞");
		if(!MainActivity.worldPlayers.isEmpty()){
			for(PlayerDto dto : MainActivity.worldPlayers.values() ){
				String name = dto.getPlayerName();
				sb.append(name);
			}
		}
		font = FontUtil.getInstance().getFont(35, sb.toString(), Color.WHITE);;
		
		//提示框
		TextureRegionDrawable tips = new TextureRegionDrawable( new TextureRegion(
				AssetsManager.getInstance().get("world/tips.png",Texture.class)));
		Drawable background = new TextureRegionDrawable(tips);
		WindowStyle style = new WindowStyle(font, Color.MAGENTA, background);
		loading = new Dialog("点赞",style);
		loading.setPosition(WIDTH/2 - loading.getWidth()/2, HEIGHT/2 - loading.getHeight()/2);
		

		camera = new OrthographicCamera(WIDTH, HEIGHT);
		int sw = 480 * 20;
		int sh = 800 * 20;
		stage = new Stage(sw, sh);
		camera.setToOrtho(false, WIDTH * 2, HEIGHT * 2);
		stage.setCamera(camera);
		
		for(int x = 0; x < 20; x ++){
			for(int y = 0 ; y < 20; y ++){
				final PlayerDto dto = MainActivity.getWorldPlayerDto(x, y);
				WorldImage location = new WorldImage(AssetsManager.getInstance().get("world/me.png", Texture.class), font, dto);
				location.setPosition(x * 480 , y * 800);
//				location.setColor(x, y, x, 1);
				String key = new StringBuilder().append(x).append("_").append(y).toString();
				worldImages.put(key, location);
				stage.addActor(location);
				final int ox = x;
				final int oy = y;
				location.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
//						loading.show(stage);
						if(dto != null){
							MapData.playerId = dto.getId();
							HashMap<String,Object> params = new HashMap<String,Object>();
							params.put("targetId", dto.getId());
							Response response = SocketUtil.send(Request.valueOf(Module.WORLD, WorldCmd.QUERY_PLAYER, params),true);
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
							SocketUtil.sendAsync(Request.valueOf(Module.WORLD, WorldCmd.ALLOCATION, params));
						}
//						loading.hide();
					}
				});
			}

		}
	
		//Home
		home = new Image(AssetsManager.getInstance().getControlTextureRegion(ControlType.HOME));
		home.setBounds(10, 10, home.getWidth() + home.getWidth()/2, home.getHeight() + home.getHeight()/2);
		
		bg = AssetsManager.getInstance().get("ui/loginbg.png",Texture.class);
	}
	
	private Texture bg;
	
	@Override
	public void pause() {

	}

	@Override
	public void render(float delatime) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		
		stage.draw();
		spriteBatch.begin();
//		spriteBatch.draw(bg,0,0,WIDTH,HEIGHT);
		if(MainActivity.player != null){
			font.draw(spriteBatch, "点赞： " + MainActivity.player.getUp() ,0,HEIGHT);
		}
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

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
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
			MapData.map = MapData.myMap;
			MapData.other = false;
			tranceGame.setScreen(tranceGame.mapScreen);
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
