package com.yanlin;
/**
 * ����
 * @author ZhangYanlin
 *
 */
public class Particle {
	int color;          //������ɫ   
    int r;              //���Ӱ뾶   
    double ver_v;       //��ֱ�ٶ�   
    double hor_v;       //ˮƽ�ٶ�   
    int startX;         //��ʼX����   
    int startY;         //��ʼY����   
    int x;              //ʵʱX����   
    int y;              //ʵʱY����   
    double startTime;   //��ʼʱ��   
      
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
