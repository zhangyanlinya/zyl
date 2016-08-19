package com.trance.tranceview.controller;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.MathUtils;

public class GestureController extends GestureAdapter implements GestureListener {
	
	private OrthographicCamera camera;
	public float zoom = 1.0f;
	public float initialScale = 1.0f;
	private float leftX;
	private float rightX;
	private float donwY;
	private float upY;
	private boolean canUpdate = true;

	public GestureController(OrthographicCamera camera, float leftX, float rightX, float donwY,float upY) {
		this.camera = camera;
		this.leftX = leftX;
		this.rightX = rightX;
		this.donwY = donwY;
		this.upY = upY;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		initialScale = zoom;
		return false;
	}
	
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		if(!canUpdate){
			return false;
		}
		float cx = camera.position.x ;
		float cy = camera.position.y;
		if(cx < leftX ){
			camera.position.x = leftX;
			return true;
		}
		
		if(cy < donwY ){
			camera.position.y = donwY;
			return true;
		}
		
		if(cx > rightX){
			camera.position.x = rightX;
			return true;
		}
		
		if(cy > upY){
			camera.position.y = upY;
			return true;
		}
		
		camera.translate(-deltaX , deltaY);
		return true;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		if(!canUpdate){
			return false;
		}
		// 与pinch对应，也是是一个多点触摸的手势，并且两个手指做出放大的动作
		// Calculate pinch to zoom
		float ratio = initialDistance / distance;

		// Clamp range and set zoom
		zoom = MathUtils.clamp(initialScale * ratio, 0.5f, 2.0f);
		camera.zoom = zoom;
		return false;
	}

	public boolean isCanUpdate() {
		return canUpdate;
	}

	public void setCanUpdate(boolean canUpdate) {
		this.canUpdate = canUpdate;
	}
	
}
