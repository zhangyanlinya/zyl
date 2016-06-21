package com.trance.tranceview.screens;

import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.trance.common.socket.SimpleSocketClient;
import com.trance.common.socket.model.Request;
import com.trance.common.util.CryptUtil;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.modules.player.handler.PlayerCmd;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.constant.ControlType;
import com.trance.tranceview.utils.AssetsManager;

public class LoginScreen implements Screen{
	
	private Image toWorld;
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private FreeTypeFontGenerator generator;
	private FreeTypeBitmapFontData fontData;
	private Stage stage;
	
	public LoginScreen(TranceGame tranceGame) {
		
	}

	@Override
	public void show() {
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		spriteBatch = new SpriteBatch();
		generator = new FreeTypeFontGenerator(
	               Gdx.files.internal("font/haibao.ttf"));
		//注意：里面的字符串一定不能重复 否则会报错
		fontData = generator.generateData(35, FreeTypeFontGenerator.DEFAULT_CHARS
	               + "点赞倒计时：", false);
		
		font = new BitmapFont(fontData, fontData.getTextureRegions(), false);

		font.setColor(Color.RED);
		generator.dispose();//别忘记释放
		
		//GO
		toWorld = new Image(AssetsManager.getControlTextureRegion(ControlType.WORLD));
		toWorld.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				new Thread(){
					public void run(){
						login();
					}
				}.start();
			}
		});
		stage.addActor(toWorld);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer(); 
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	protected void login() {
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
		params.put("fcmStatus", 0);
		params.put("adultStatus", 2);
		int module = Module.PLAYER;
		int cmd = PlayerCmd.LOGIN;
		SimpleSocketClient.socket.sendAsync(Request.valueOf(module, cmd, params));
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		stage.draw();
		stage.act(delta);
		spriteBatch.begin();
		font.draw(spriteBatch,"start game..",400,240);
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
		spriteBatch.dispose();
		font.dispose();
		stage.dispose();
	}
	
}