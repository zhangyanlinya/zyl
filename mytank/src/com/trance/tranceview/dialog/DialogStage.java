package com.trance.tranceview.dialog;


import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.trance.tranceview.TranceGame;
import com.trance.tranceview.constant.UiType;
import com.trance.tranceview.dialog.base.BaseStage;
import com.trance.tranceview.utils.ResUtil;

/**
 *
 */
public class DialogStage extends BaseStage {

    private Image bgImage;

    public DialogStage(TranceGame tranceGame) {
        super(tranceGame);
        init();
    }

    private void init() {
        bgImage = new Image(ResUtil.getInstance().getUi(UiType.BLANK));
        bgImage.getColor().a = 0.9f;
        bgImage.setWidth(getWidth() * 0.8f);
        bgImage.setHeight(getHeight() * 0.8f);
        bgImage.setPosition(getWidth()/2 - bgImage.getWidth()/2,  getHeight()/2 - bgImage.getHeight()/2);
        addActor(bgImage);
        
        Image close = new Image(ResUtil.getInstance().getUi(UiType.CLOSE));
        close.setPosition(getWidth()/2 + bgImage.getWidth()/2,  getHeight()/2 + bgImage.getHeight()/2);
        close.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				getTranceGame().mapScreen.setShowDailogStage(false);
			}
        });
        addActor(close);
    }
}



















