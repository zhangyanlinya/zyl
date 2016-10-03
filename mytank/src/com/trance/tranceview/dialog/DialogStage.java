package com.trance.tranceview.dialog;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.dialog.base.BaseStage;
import com.trance.tranceview.utils.ResUtil;

/**
 * 退出确认对话框的舞台, 包含一个对话框
 *
 * @author xietansheng
 */
public class DialogStage extends BaseStage {

    /** 舞台背景颜色, 60% 黑色 */
    private final Color bgColor = new Color(0, 0, 0, 0.6F);

    /** 背景 */
    private Image bgImage;
	
	/** 确认对话框 */
	private DialogGroup dialogGroup;

    public DialogStage(TranceGame tranceGame) {
        super(tranceGame);
        init();
    }

    private void init() {
        /*
         * 背景
         */
        // Res.AtlasNames.GAME_BLANK 是一张纯白色的小图片
        bgImage = new Image(ResUtil.getInstance().get("world/tips.png",Texture.class));
        bgImage.setColor(bgColor);
        bgImage.setOrigin(0, 0);
        // 缩放到铺满整个舞台
        bgImage.setScale(getWidth() / bgImage.getWidth(), getHeight() / bgImage.getHeight());
        addActor(bgImage);

        /*
         * 创建对话框
         */
    	dialogGroup = new DialogGroup("确定退出游戏 ?");
    	// 将对话框居中到舞台
    	dialogGroup.setPosition(
    			getWidth() / 2 - dialogGroup.getWidth() / 2,
    			getHeight() / 2 - dialogGroup.getHeight() / 2
    	);
    	
    	// 给对话框的确定按钮添加监听器
    	dialogGroup.getOkButton().addListener(new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		// 点击确定按钮, 退出应用
        		Gdx.app.exit();
        	}
        });
    	
    	// 给对话框的确定按钮添加监听器
    	dialogGroup.getCancelButton().addListener(new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		// 点击取消按钮, 隐藏对话框（隐藏退出确认舞台, 返回主游戏舞台）
        		getTranceGame().mapScreen.setShowDailogStage(false);
        	}
        });
    	
    	// 添加对话框到舞台
    	addActor(dialogGroup);
    	
    	/*
    	 * 添加舞台输入监听器
    	 */
//    	addListener(new InputListenerImpl());
    }
    
//    /**
//     * 输入事件监听器
//     */
//    private class InputListenerImpl extends InputListener {
//        @Override
//        public boolean keyUp(InputEvent event, int keycode) {
//            if (keycode == Input.Keys.BACK) {
//                // 按返回键, 隐藏退出确认舞台（返回主游戏舞台）
////                getTranceGame().getGameScreen().setShowExitConfirmStage(false);
//                return true;
//            }
//            return super.keyUp(event, keycode);
//        }
//    }

}



















