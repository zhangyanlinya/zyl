package com.trance.tranceview.screens;

import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.model.Request;
import com.trance.common.util.CryptUtil;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.player.handler.PlayerCmd;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.utils.AssetsManager;

public class LoginScreen implements Screen{
	
	private Image start;
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private FreeTypeFontGenerator generator;
	private FreeTypeBitmapFontData fontData;
	private Stage stage;
	private boolean init;
	public static boolean login;
	private boolean connect = true;
	
	public LoginScreen(TranceGame tranceGame) {

	}
	
	public void init(){
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		spriteBatch = new SpriteBatch();
		generator = new FreeTypeFontGenerator(
	               Gdx.files.internal("font/haibao.ttf"));
		//注意：里面的字符串一定不能重复 否则会报错
		fontData = generator.generateData(45, FreeTypeFontGenerator.DEFAULT_CHARS + "点击图片开始游戏网络连接失败", false);
		
		font = new BitmapFont(fontData, fontData.getTextureRegions(), false);

		font.setColor(Color.RED);
		generator.dispose();//别忘记释放
		
		//GO
		TextureRegionDrawable startDrawable = new TextureRegionDrawable( new TextureRegion(
				AssetsManager.getInstance().get("ui/loginbg.png", Texture.class)));
		start = new Image(startDrawable);
		start.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(login){
					return;
				}
				login = true;
				new Thread(){
					public void run(){
						login();
					}
				}.start();
			}
		});
		
		start.setWidth(start.getWidth() * 5);
		start.setHeight(start.getHeight() * 5);
		int x = (int) (Gdx.graphics.getWidth()/2 - start.getWidth()/2);
		int y = (int) (Gdx.graphics.getHeight()/2 - start.getHeight()/2);
		start.setX(x);
		start.setY(y);
		stage.addActor(start);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer(); 
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void show() {
		if(!init){
			init();
			init = true;
		}
	}
	
	protected synchronized void login() {
		String src = MainActivity.userName + MainActivity.loginKey;
		String loginMD5 = null;
		try {
			loginMD5 = CryptUtil.md5(src);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", MainActivity.userName);
		params.put("loginKey", loginMD5);
		params.put("server", "1");
		params.put("loginWay", "0");
		int module = Module.PLAYER;
		int cmd = PlayerCmd.LOGIN;
		connect = SimpleSocketClient.socket.sendAsync(Request.valueOf(module, cmd, params));
		if(!connect){
			login = false;
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		stage.draw();
		spriteBatch.begin();
		font.draw(spriteBatch,"[点击图片开始游戏]",350,240);
		if(!connect){
			font.draw(spriteBatch,"[网络连接失败]",350,200);
		}
		spriteBatch.end();
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
		stage.dispose();
		spriteBatch.dispose();
		font.dispose();
		login = false;
	}
	
}
