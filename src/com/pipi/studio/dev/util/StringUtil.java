package com.pipi.studio.dev.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.location.Location;



/**
* @ClassName: StringUtil
* @Description: TODO(字符串工具类)
* @author liuwei
* @date 2012-9-11 下午01:48:36
* FIXME HardcodedText!!!
*/
public class StringUtil {

	public static boolean isEmpty( String input ) 
	{
		if ( input == null || "".equals( input ) || "null".equals(input) )
			return true;
		
		for ( int i = 0; i < input.length(); i++ ) 
		{
			char c = input.charAt( i );
			if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean isNotEmpty(String s) {
		return (s != null && s.trim().length() > 0);
	}
	
	public static String readAssetsCity(Context context,String fileName) {
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		if(fileName == null || fileName.equals("")){
			return null;
		}
		try {
			is = context.getAssets().open(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8")); 
			
			sb.append(br.readLine());
		
			String line ;
			while((line = br.readLine())!=null){
//				temp+=line;
				sb.append(line);
			}
		} catch (Exception e) {
			return null;
		}
		return sb.toString();
	}
	
	public static String readFromFile(Context context,String fileName) {
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		
//		String temp = null;
		File file;
		if(fileName == null || fileName.equals("")){
			return null;
		}
		try {
			file = context.getFileStreamPath(fileName);
			is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is,"UTF-8");
			int c;
			char[] charStr = new char[1024];
			while ((c = isr.read(charStr)) != -1) {
				sb.append(charStr,0,c);
			}
			
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return  sb.toString();
		
	}
	
	public static void writeCityToFile(Context context,String fileName) {
		InputStream is = null;
		if(fileName == null || fileName.equals("")){
			return ;
		}
		try {
//			ew
			is = context.getAssets().open(fileName);
			File file = context.getFileStreamPath(fileName);
			FileOutputStream output = new FileOutputStream(file);
	         	byte[] buffer = new byte[4096];
	             int n = 0;
	             while (-1 != (n = is.read(buffer))) {
	                 output.write(buffer, 0, n);
	             }
	       output.close();
	       is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeNewCityToFile(Context context,String fileName,String str){
		File file = context.getFileStreamPath(fileName);
		FileOutputStream fos=null;
		try {
			fos = new FileOutputStream(file);
			OutputStreamWriter osw=new OutputStreamWriter(fos);
			osw.write(str);
			osw.close();
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int getChineseLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 1;
            }
        }
        return valueLength;
    }
	
	public static int getAirDegree(double ax, double ay, double bx, double by) {
		double degree = Math.atan(Math.abs((by - ay) / (bx - ax))) * 180 / Math.PI;
		double dLo = by - ay;
		double dLa = bx - ax;
		if (dLo > 0 && dLa <= 0) {
			degree = (90 - degree) + 90;
		} else if (dLo <= 0 && dLa < 0) {
			degree = degree + 180;
		} else if (dLo < 0 && dLa >= 0) {
			degree = (90 - degree) + 270;
		}
		return (int) degree;
	}
	
	public static String selectTime(String planTime, String realTime){
		if(!"-".equals(realTime.trim())){
			return realTime;
		}
		return planTime;
	}
	
	public static String getStringValue(Object str, String def){
		String s = (String)str;
		if(null == s || isEmpty(s) || "false".equals(s)){
			s = def;
		}
		return s;
	}
	
	public static String format(int x) {
		String s = "" + x;
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}
	
	//计算两坐标点之间的距离
	public static double getDistance(double lat1, double lon1, double lat2, double lon2){
		float[] results=new float[1];  
		Location.distanceBetween(lat1, lon1, lat2, lon2, results);
		return results[0];
	}
}
