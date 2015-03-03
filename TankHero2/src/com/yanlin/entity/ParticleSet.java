package com.yanlin.entity;

import java.util.ArrayList;

import android.graphics.Color;

public class ParticleSet {
	public ArrayList<Particle> particleSet;  
    public ParticleSet(){  
        particleSet = new ArrayList<Particle>();  
    }  

	public void addParticle(int count,double startTime,Explode e){  
        for(int i=0;i<count;i++){  
            int color = this.getColor(i);  
            int r = 1;  
            double ver_v = -30 + 10*(Math.random());  
            double hor_v = 10 - 20*(Math.random());  
            //int x = 160;  
            int y = (int)(e.y - 10*(Math.random()));  
            Particle particle = new Particle(color, r, ver_v, hor_v, e.x, y, startTime);  
            particleSet.add(particle);  
        }  
    }  
    public int getColor(int i){  
        int color = Color.RED;  
        switch(i%4){  
            case 0:  
                color = Color.RED;  
                break;  
            case 1:  
                color = Color.BLUE;  
                break;  
            case 2:  
                color = Color.YELLOW;  
                break;  
            case 3:  
                color = Color.GRAY;  
                break;  
        }  
        return color;  
    }  

}
