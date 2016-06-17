package com.trance.tranceview.screens;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.trance.common.socket.model.Request;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.player.handler.PlayerCmd;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.constant.ControlType;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.stages.GameStage;
import com.trance.tranceview.utils.AssetsManager;

public class GameScreen implements Screen{
	TranceGame tranceGame;
	public static int width;
	public static int height;
	
	Stage stage;
	Image toWorld;
	
	private Window window;
	private ImageButton btn_up;
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private Music beginMusic;
	private FreeTypeFontGenerator generator;
	private FreeTypeBitmapFontData fontData;
	
	/**
	 * 一局所用总时间
	 */
	private final static int TOTAL_TIME = 1 * 60;
	
	/**
	 * 当前时间
	 */
	private int currTime = TOTAL_TIME;
	private Timer timer;
	
	public GameScreen(TranceGame tranceGame) {
		this.tranceGame = tranceGame;
	}

	@Override
	public void show() {
		currTime = TOTAL_TIME;//初始化时间 
		spriteBatch = new SpriteBatch();
		generator = new FreeTypeFontGenerator(
	               Gdx.files.internal("font/haibao.ttf"));
		//注意：里面的字符串一定不能重复 否则会报错
		fontData = generator.generateData(35, FreeTypeFontGenerator.DEFAULT_CHARS
	               + "点赞倒计时：", false);
		
		font = new BitmapFont(fontData, fontData.getTextureRegions(), false);

		font.setColor(Color.RED);
		generator.dispose();//别忘记释放
		
		beginMusic = AssetsManager.assetManager.get("audio/begin.mp3");
		beginMusic.play();
		width = Gdx.graphics.getWidth(); // 720
		height = Gdx.graphics.getHeight(); // 1200
		
		MapData.win = false;
		MapData.over = false;
		stage = new GameStage(width, height, true);
		
		//返回家
		toWorld = new Image(AssetsManager.getControlTextureRegion(ControlType.WORLD));
		toWorld.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				tranceGame.setScreen(tranceGame.worldScreen);
				dispose();
			}
		});
		stage.addActor(toWorld);
		
		//提示框
		TextureRegionDrawable tips = new TextureRegionDrawable( new TextureRegion(
				AssetsManager.assetManager.get("world/tips.png",Texture.class)));
		Drawable background = new TextureRegionDrawable(tips);
		WindowStyle style = new WindowStyle(font, Color.MAGENTA, background);
		window = new Window("点赞",style);
		window.setPosition(width/2 - window.getWidth()/2, height/2 - window.getHeight()/2);
		
		//点赞
		TextureRegionDrawable up = new TextureRegionDrawable( new TextureRegion(
				AssetsManager.assetManager.get("ui/up.png",Texture.class)));
		btn_up = new ImageButton(up);
		btn_up.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(MapData.playerId > 0L && MainActivity.player != null && MapData.playerId != MainActivity.player.getId()){
					Map<String,Object> params = new HashMap<String,Object>();
					params.put("targetId", MapData.playerId);
					MainActivity.socket.sendAsync(Request.valueOf(Module.PLAYER, PlayerCmd.UP, params));
					Music music = AssetsManager.assetManager.get("audio/get_bomber.mp3");
					music.play();
				}
			}
		});
		window.addActor(btn_up);
		
		timer = new Timer();
		timer.schedule(new MyTask() , 1000 ,1000);//表示多少时间后结束
				
	}
	
	class MyTask extends TimerTask {

		@Override
		public void run() {
			currTime --;
			if(currTime <= 0){//表示到时了
				MapData.win = true;
				Music music = AssetsManager.assetManager.get("audio/game_over.mp3");
				music.play();
				timer.cancel();//取消定时器
				timer = null;
			}
		}
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		
		if(MapData.win || MapData.over){
			stage.addActor(window);
		}
		spriteBatch.begin();
		font.draw(spriteBatch,"倒计时:" + currTime,0,height);
		spriteBatch.end();
		stage.act(delta);
		stage.draw();
		
	}
	
	/**
	 * 碰撞检测
	 */
//	private void collisionDetection() {
//		
//		for(int i = 0; i < blocks.size ;i ++ ){//与墙相关的
//			Block block = blocks.get(i);
//			for(int j = 0 ; j < tanks.size ; j++){//车撞墙
//				Block tank = tanks.get(j);
//				if(block.type == BlockType.GRASS){
//					continue;
//				}
//				if(block.rectangle.overlaps(tank.rectangle)){
//					tank.stay();
//				}
//			}
//			for(int k = 0 ; k < bullets.size ; k++){//子弹撞墙
//				Bullet bullet = bullets.get(k);
//				if(block.type == BlockType.WATER){
//					continue;
//				}
//				if(block.type == BlockType.GRASS){
//					continue;
//				}
//				if(block.rectangle.overlaps(bullet.rectangle)){
//					if(block.type == BlockType.STEEL){
//						bullet.removeFromStage();
//						bullets.removeIndex(k);
//						continue;
//					}
//					if(block.byAttack(bullet)){
//						block.remove();
//						blocks.removeIndex(i);
//						MapScreen.blockPool.free(block);
//					}
//					bullet.removeFromStage();
//					bullets.removeIndex(k);
//				}
//			}
//		}
//		
//		for(int i = 0 ;i <tanks.size; i ++){//与车相关
//			Block tank = tanks.get(i);
//			for(int j = 0 ; j < bullets.size; j++){//被子弹击中
//				Bullet bullet = bullets.get(j);
//				if(tank.rectangle.overlaps(bullet.rectangle)){
//					if(bullet.block.type != tank.type){
//						if(tank.byAttack(bullet)){//over!
//							tank.remove();
//							tanks.removeIndex(i);
//							MapScreen.blockPool.free(tank);
//						}
//						bullet.removeFromStage();
//						bullets.removeIndex(j);
//					}
//				}
//			}
//			for(int j = 0 ; j < tanks.size ; j++){//车撞车
//				Block t2 = tanks.get(j);
//				if(tank == t2){
//					continue;
//				}
//				if(tank.rectangle.overlaps(t2.rectangle)){
//					tank.stay();
//					t2.stay();
//				}
//			}
//		}
//	}


	
	
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

		if (stage != null){
			stage.dispose();
		}
		
		if(spriteBatch != null){
			spriteBatch.dispose();
		}
		
		if(timer != null){
			timer.cancel();
			timer = null;
		}
		
		if(beginMusic!= null && beginMusic.isPlaying()){
			beginMusic.stop();
		}
		
		if(font != null){
			font.dispose();
		}
	}
	
}