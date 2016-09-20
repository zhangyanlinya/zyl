package com.trance.tranceview.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.trance.tranceview.utils.AssetsManager;
import com.trance.tranceview.utils.FontUtil;

/**
 * <b>类名称：</b>ExitDialog
 * <b>类描述：</b>退出游戏对话框
 * <b>创建人：</b>wanglong
 * <b>修改人：</b>wanglong
 * <b>修改时间：</b>2014-12-14 上午3:54:18
 * <b>修改备注：</b>类初始化
 * @version 1.0.0<br/>
 */
public class ExitDialog extends Dialog{

	public ExitDialog(String title, WindowStyle windowStyle) {
		super(title, windowStyle);
		TextureRegionDrawable textureRegionDrawable = (TextureRegionDrawable) windowStyle.background;
		float regionWidth = textureRegionDrawable.getRegion().getRegionWidth();
		float regionHeight = textureRegionDrawable.getRegion().getRegionHeight();
		setColor(1, 1, 1, 0);
		setSize(regionWidth, regionHeight);
		setOrigin(getWidth()/2, getHeight()/2);
		setPosition(100, 100);
		
//		//标题
//		FreeTypeFontActor titleActor = new FreeTypeFontActor(title, 24);
//		titleActor.setColor(Color.YELLOW);
//		titleActor.setPosition((getWidth() - titleActor.getWidth())/2, getHeight() - 4);
//		addActor(titleActor);
		
		getContentTable().debug();
		LabelStyle labelStyle = new LabelStyle(FontUtil.getInstance().getFont(25, Color.WHITE), Color.YELLOW);
		getContentTable().add(new Label("确定退出游戏吗?", labelStyle));
		
		getButtonTable().debug();
		
		//取消按钮
		Group cancel = new Group();
		final ImageButton cancelButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(
				AssetsManager.getInstance().get("world/tips.png",Texture.class))));
		cancel.addActor(cancelButton);
		labelStyle = new LabelStyle(FontUtil.getInstance().getFont(25, Color.WHITE), Color.BLACK);
		Label cancelLabel = new Label("取 消", labelStyle);
		cancelLabel.setPosition((cancelButton.getWidth() - cancelLabel.getWidth())/2, 2.5f + (cancelButton.getHeight() - cancelLabel.getHeight())/2);
		cancel.addActor(cancelLabel);
		cancel.setSize(cancelButton.getWidth(), cancelButton.getHeight());
		cancel.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				ExitDialog.this.hide();
			}
		});
		getButtonTable().add(cancel).padRight(40);
		
		//确定按钮
		Group confirm = new Group();
		final ImageButton confirmButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(
				AssetsManager.getInstance().get("world/tips.png",Texture.class))));
		confirm.addActor(confirmButton);
		labelStyle = new LabelStyle(FontUtil.getInstance().getFont(25, Color.WHITE), Color.WHITE);
		Label confirmLabel = new Label("确 定", labelStyle);
		confirmLabel.setPosition((confirmButton.getWidth() - confirmLabel.getWidth())/2, 2.5f + (confirmButton.getHeight() - confirmLabel.getHeight())/2);
		confirm.addActor(confirmLabel);
		confirm.setSize(confirmButton.getWidth(), confirmButton.getHeight());
		confirm.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
//				Gdx.app.exit();
			}
		});
		getButtonTable().add(confirm).padLeft(40);
		
		//内容表格单元
		getCells().get(0).padTop(40);
		//按钮表格单元
		getCells().get(1).padBottom(40);
	}

}