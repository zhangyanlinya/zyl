package com.trance.tranceview.screens;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.microedition.khronos.opengles.GL10;

import com.alibaba.fastjson.JSON;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.trance.common.socket.model.Request;
import com.trance.common.socket.model.Response;
import com.trance.common.socket.model.ResponseStatus;
import com.trance.common.util.CryptUtil;
import com.trance.trancetank.config.Module;
import com.trance.trancetank.model.Result;
import com.trance.trancetank.modules.building.model.PlayerBuildingDto;
import com.trance.trancetank.modules.coolqueue.model.CoolQueueDto;
import com.trance.trancetank.modules.player.handler.PlayerCmd;
import com.trance.trancetank.modules.player.model.ArmyDto;
import com.trance.trancetank.modules.player.model.PlayerDto;
import com.trance.tranceview.MainActivity;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.mapdata.MapData;
import com.trance.tranceview.utils.AssetsManager;
import com.trance.tranceview.utils.FontUtil;
import com.trance.tranceview.utils.SocketUtil;
import com.trance.tranceview.utils.TimeUtil;

public class LoginScreen implements Screen{
	
	private Texture background;
	private Image start;
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private Stage stage;
	private boolean init;
	private TranceGame tranceGame;
	private AssetsManager assetsManager;
	//画笔
  	public ShapeRenderer renderer;
	
	public LoginScreen(TranceGame tranceGame) {
		this.tranceGame = tranceGame;
	}
	
	public void init(){
		renderer = new ShapeRenderer(); 
		assetsManager = AssetsManager.getInstance();
		assetsManager.init();
		
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		spriteBatch = new SpriteBatch();
		String code = "我养了一条鱼，死了[快哭了]，悲伤不已[快哭了]，我不想土葬，我想给它火葬，把鱼灰撒回海洋……" +
				" 谁知道那玩意越烤越香……后来我就买了瓶啤酒……太感人了！[流泪][流泪][流泪]"
				+ "论技术怎么给策划解释概率不稳 是 因为 电压不稳 造成的？游戏服务器是跑在linux上吧，linux的随机数是硬件随机吧，所以电压不稳，造成概率失控";
		font = FontUtil.getInstance().getFont(45, "点击图片开始游戏", Color.RED);
		
		//GO
		background = new Texture(Gdx.files.internal("ui/loginbg.png"));
		TextureRegionDrawable startDrawable = new TextureRegionDrawable(new TextureRegion(
				background));
		start = new Image(startDrawable);
		start.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				   if(!finish){
					   return;
				   }
				   login();
			}
		});
		
		start.setWidth(start.getWidth() * 5);
		start.setHeight(start.getHeight() * 5);
		float x = Gdx.graphics.getWidth()/2 - start.getWidth()/2;
		float y = Gdx.graphics.getHeight()/2 - start.getHeight()/2;
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
	
	@SuppressWarnings("unchecked")
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
		int module = Module.PLAYER;
		int cmd = PlayerCmd.LOGIN;
		Response response = SocketUtil.send(Request.valueOf(module, cmd, params),true);
		if(response == null){
			return;
		}
		
		ResponseStatus status = response.getStatus();
		if (status == ResponseStatus.SUCCESS) {
			byte[] bytes = response.getValueBytes();
			String text = new String(bytes);
			Result<PlayerDto> result = JSON.parseObject(text, Result.class);
			if (result == null) {
				return;
			}
			Object pobj = result.get("content");
			if (pobj == null) {
				return;
			}
			PlayerDto playerDto = JSON.parseObject(pobj.toString(),
					PlayerDto.class);
			playerDto.setMyself(true);
			
			int[][] map = null;
			Object mobj = result.get("mapdata");
			if (mobj == null) {
				map = MapData.baseMap;
			}else{
				map = JSON.parseObject(mobj.toString(), int[][].class);
			}
			playerDto.setMap(map);

			Object wobj = result.get("worldPlayers");
			if (wobj != null) {
				Map<String, Object> players = (Map<String, Object>) wobj;
				for (Entry<String, Object> e : players.entrySet()) {
					String dto = JSON.toJSONString(e.getValue());
					PlayerDto value = JSON.parseObject(dto, PlayerDto.class);
					MainActivity.worldPlayers.put(e.getKey(), value);
				}
			}
			
			Object aobj = result.get("armys");
			if(aobj != null){
				List<ArmyDto> armys = JSON.parseArray(aobj.toString(), ArmyDto.class);
				for(ArmyDto dto : armys){
					playerDto.addAmry(dto);
				}
			}
			
			Object cobj = result.get("coolQueues");
			if(cobj != null){
				List<CoolQueueDto> coolQueues = JSON.parseArray(cobj.toString(), CoolQueueDto.class);
				for(CoolQueueDto dto : coolQueues){
					playerDto.addCoolQueue(dto);
				}
			}
			
			Object bobj = result.get("buildings");
			if(bobj != null){
				List<PlayerBuildingDto> buildings = JSON.parseArray(bobj.toString(), PlayerBuildingDto.class);
				for(PlayerBuildingDto dto : buildings){
					playerDto.addBuilding(dto);
				}
			}
			
			Long serverTime = (Long) result.get("serverTime");
			TimeUtil.init(serverTime);
			
			MainActivity.player = playerDto;
			
			Gdx.app.postRunnable(new Runnable() {
				
				@Override
				public void run() {
					tranceGame.startGame();
				}
			});
		}
	}

	private boolean finish; 
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		stage.draw();
		if(assetsManager.update()){
			spriteBatch.begin();
			font.setColor(Color.GREEN);
			font.draw(spriteBatch,"[点击图片开始游戏]",350,240);
			spriteBatch.end();
			finish = true;
		}
		
		//draw progress
		float percent = assetsManager.getProgress(); 
		renderer.setColor(Color.RED);
		renderer.begin(ShapeType.Line);
		renderer.rect(Gdx.graphics.getWidth() / 4 , 100, Gdx.graphics.getWidth() / 2, 40);
		renderer.end();
		if(percent < 0.2){
			renderer.setColor(Color.RED);
		}else if(percent < 0.5){
			renderer.setColor(Color.YELLOW);
		}else{
			renderer.setColor(Color.GREEN);
		}
		renderer.begin(ShapeType.Filled);
		renderer.rect(Gdx.graphics.getWidth() / 4 + 2, 104, percent * Gdx.graphics.getWidth()/2 - 6, 34);
		renderer.end();
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
		background.dispose();
		stage.dispose();
		spriteBatch.dispose();
		font.dispose();
		assetsManager.dispose();
		renderer.dispose();
	}
	
}
