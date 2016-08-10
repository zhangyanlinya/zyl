package com.trance.tranceview.utils;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;

public class FontUtil {
	
	private static FontUtil fontUtil;
	private FreeTypeFontGenerator generator;
	private Set<String> set = new HashSet<String>();
	private static String[] Surname= {"赵","钱","孙","李","周","吴","郑","王","冯","陈","褚","卫","蒋","沈","韩","杨","朱","秦","尤","许",
			  "何","吕","施","张","孔","曹","严","华","金","魏","陶","姜","戚","谢","邹","柏","水","窦","章","云","苏","潘","葛","奚","范","彭",
			  "鲁","韦","昌","马","苗","凤","花","方","俞","任","袁","柳","酆","鲍","史","唐","费","廉","岑","薛","雷","贺","倪","汤","滕","殷",
			  "罗","毕","郝","邬","安","常","乐","于","时","傅","皮","卞","齐","康","伍","余","元","卜","顾","孟","平","黄","和",
			  "穆","萧","尹","姚","邵","湛","汪","祁","毛","禹","狄","米","贝","明","臧","计","伏","成","戴","谈","宋","茅","庞","熊","纪","舒",
			  "屈","项","祝","董","梁","杜","阮","蓝","闵","席","季","麻","强","贾","路","娄","危","江","童","颜","郭","梅","盛","林","刁","钟",
			  "徐","邱","骆","高","夏","蔡","田","樊","胡","凌","霍","虞","万","支","柯","昝","管","卢","莫","经","房","裘","缪","干","解","应",
			  "宗","丁","宣","贲","邓","郁","单","杭","洪","包","诸","左","石","崔","吉","钮","龚","程","嵇","邢","滑","裴","陆","荣","翁","荀",
			  "谷","车","侯","宓","蓬","全","郗","班","仰","秋","仲","伊","宫","宁","仇","栾","暴","甘","钭","厉","戎","祖","武","符","刘","景",
			  "詹","束","龙","叶","幸","司","韶","郜","黎","蓟","溥","印","宿","白","怀","蒲","邰","从","鄂","索","咸","籍","赖","卓","蔺","屠",
			  "蒙","池","乔","阴","郁","胥","能","苍","双","闻","莘","党","翟","谭","贡","劳","逄","姬","申","扶","堵","冉","宰","郦","雍","却",
			  "璩","桑","桂","濮","牛","寿","通","边","扈","燕","冀","浦","尚","农","温","别","庄","晏","柴","瞿","阎","充","慕","连","茹","习",
			  "宦","艾","鱼","容","向","古","易","慎","戈","廖","庾","终","暨","居","衡","步","都","耿","满","弘","匡","国","文","寇","广","禄",
			  "闾","辜","纵","侴","万俟","司马","上官","欧阳","夏侯","诸葛","闻人","东方","赫连","皇甫","羊舌","尉迟","公羊","澹台","公冶","宗正",
			  "濮阳","淳于","单于","太叔","申屠","公孙","仲孙","轩辕","令狐","钟离","宇文","长孙","慕容","鲜于","闾丘","司徒","司空","兀官","司寇"};
	
	public static FontUtil getInstance(){
		if(fontUtil == null){
			fontUtil = new FontUtil();
		}
		return fontUtil;
	}
	
	/**
	 * get BitmapFont from config
	 * 
	 * @param size    font size
	 * @param append  追加String 
	 * @param color   font color
	 * @return
	 */
	public BitmapFont getFont(int size, String append, Color color){
		set.clear();
		for(int i = 0; i < append.length(); i++){
			char c = append.charAt(i);
			if(CharUtil.isChinese(c)){
				set.add(String.valueOf(c));
			}
		}
		StringBuilder sb = new StringBuilder(FreeTypeFontGenerator.DEFAULT_CHARS);
		for(String s : set){
			sb.append(s);
		}
		generator = new FreeTypeFontGenerator(
	               Gdx.files.internal("font/font.ttf"));
		FreeTypeBitmapFontData fontData = generator.generateData(size,
	              sb.toString(), false);
		generator.dispose();
		BitmapFont font = new BitmapFont(fontData, fontData.getTextureRegions(), false);
		font.setColor(color);
		return font;
	}
	
	public static String randomChineseName(){
		int index = RandomUtil.nextInt(Surname.length - 1);		
		String name = Surname[index]; //
		/* 从常用字中选取一个或两个字作为名 */
		if(RandomUtil.nextBoolean()){
			name += getChinese() + getChinese();
		}else {
			name += getChinese();
		}
		return name;
	}
	
	private static String getChinese() {
		String str = null;
		int highPos = (176 + RandomUtil.nextInt(39));// 区码，0xA0打头，从第16区开始，即0xB0=11*16=176,16~55一级汉字，56~87二级汉字
		int lowPos = 161 + RandomUtil.nextInt(94);// 位码，0xA0打头，范围第1~94列
		
		byte[] bArr = new byte[2];
		bArr[0] = Integer.valueOf(highPos).byteValue();;
		bArr[1] = Integer.valueOf(lowPos).byteValue();;
		try {
			str = new String(bArr, "GB2312"); // 区位码组合成汉字
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
}
