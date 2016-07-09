package com.trance.tranceview.screens;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.model.Request;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.player.handler.PlayerCmd;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.constant.ControlType;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.stages.GameStage;
import com.trance.tranceview.utils.AssetsManager;
import com.trance.tranceview.utils.FontUtil;

public class GameScreen implements Screen{
	
	private TranceGame tranceGame;
	public static int width;
	public static int height;
	private GameStage stage;
	private Image toWorld;
	private Window window;
	private ImageButton btn_up;
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private Music music;
	/**
	 * 一局所用总时间
	 */
	private final static int TOTAL_TIME = 2 * 60;
	
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
		stage = new GameStage(width, height, true);
		
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
		stage.init();
		stage.addActor(toWorld);
		InputMultiplexer inputMultiplexer = new InputMultiplexer(); 
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		
		clock();
		if(MapData.win || MapData.over){
			stage.addActor(window);
		}
		spriteBatch.begin();
		font.draw(spriteBatch,"倒计时:" + currTime,0,height);
		spriteBatch.end();
		stage.act(delta);
		stage.draw();
	}
	
	private long time = 0;
	private void clock(){
		long now = System.currentTimeMillis();
		if( (now - time) > 1000){
			time = now;
			currTime--;
			if(currTime <= 0){
				MapData.over = true;
			}
		}
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
	}
	
}
