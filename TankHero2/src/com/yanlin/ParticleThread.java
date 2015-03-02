package com.yanlin;

import java.util.ArrayList;

public class ParticleThread extends Thread {
	boolean isRunning;
	Explode e;
	int sleepSpan = 80;
	double time = 0;
	double span = 0.15;
	

	public ParticleThread(Explode e) {
		this.isRunning = true;
		this.e = e;
	}

	@Override
	public void run() {
		long startRunTime = System.currentTimeMillis();
		while (isRunning) {
			e.ps.addParticle(5, time,e);

			ArrayList<Particle> tempSet = e.ps.particleSet;
			int count = tempSet.size();
			if(count>0){
			for (int i = 0; i < count; i++) {
				
				Particle particle = tempSet.get(i);
				double timeSpan = time - particle.startTime;
				// 计算X坐标
				int tempx = (int) (particle.startX + particle.hor_v * timeSpan);
				// 计算Y坐标
				int tempy = (int) (particle.startY + particle.ver_v * timeSpan + 4.9
						* timeSpan * timeSpan);
				// 超过屏幕下边
				if (tempy > particle.startY + 5
						|| tempx > particle.startX + 5) {
					tempSet.remove(particle);
					count = tempSet.size();
				}
				particle.x = tempx;
				particle.y = tempy;
			}
			}
			time += span;
			try {
				Thread.sleep(sleepSpan);
			} catch (Exception ex) {
				ex.printStackTrace();

			}
			long endRunTime  = System.currentTimeMillis();
			if((endRunTime-startRunTime)>3000){
				isRunning =false;
//				e.gameView.pts.remove(this);
				e.live =false;
				
			}

		}
		super.run();
	}

}
