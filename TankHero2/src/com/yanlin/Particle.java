package com.yanlin;
/**
 * 粒子
 * @author ZhangYanlin
 *
 */
public class Particle {
	int color;          //粒子颜色   
    int r;              //粒子半径   
    double ver_v;       //垂直速度   
    double hor_v;       //水平速度   
    int startX;         //初始X坐标   
    int startY;         //初始Y坐标   
    int x;              //实时X坐标   
    int y;              //实时Y坐标   
    double startTime;   //起始时间   
      
    public Particle(int color, int r, double ver_v, double hor_v, int x, int y, double startTime) {  
        super();  
        this.color = color;  
        this.r = r;  
        this.ver_v = ver_v;  
        this.hor_v = hor_v;  
        this.startX = x;  
        this.startY = y;  
        this.x = x;  
        this.y = y;  
        this.startTime = startTime;  
    }  

}
