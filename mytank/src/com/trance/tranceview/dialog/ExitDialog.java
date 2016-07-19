package com.trance.tranceview.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.trance.tranceview.utils.FontUtil;

/**
 * <b>类名称：</b>ExitDialog
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
		setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		
		LabelStyle labelStyle = new LabelStyle(FontUtil.getInstance().getFont(35, "网络连接中...", Color.YELLOW), Color.YELLOW);
		getContentTable().add(new Label("网络连接中...", labelStyle));
		
		getButtonTable().debug();
		
	}

}