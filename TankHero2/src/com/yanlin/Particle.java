package com.yanlin;
/**
 * ����
 * @author ZhangYanlin
 *
 */
public class Particle {
	public int color;          //������ɫ   
	public int r;              //���Ӱ뾶   
    public double ver_v;       //��ֱ�ٶ�   
    public double hor_v;       //ˮƽ�ٶ�   
    public int startX;         //��ʼX���   
    public int startY;         //��ʼY���   
    public int x;              //ʵʱX���   
    public int y;              //ʵʱY���   
    public double startTime;   //��ʼʱ��   
      
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
