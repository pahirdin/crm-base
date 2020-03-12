package com.asiainfo.crm.sec.web.privchecker.data;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class ParamMap extends TreeMap<String, String> implements Serializable, Comparable<Map<String, String>>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4534667742485195787L;
	
	public ParamMap(){
		super(new ParamMapComparator());
	}
	
	public static class ParamMapComparator implements Serializable, Comparator<String>{

		/**
		 * 
		 */
		private static final long serialVersionUID = -5242374953667680677L;

		/**
		 * 顺序排列
		 */
		@Override
		public int compare(String str1, String str2) {
			return str1.compareTo(str2);
		}
		
	}
	
	
	/**
	 * 顺序比较
	 */
	@Override
	public int compareTo(Map<String, String> m) {
		int size1 = this.size();
		int size2 = m.size();
		
		String[] arr1 = new String[size1];
		String[] arr2 = new String[size2];
		
		this.keySet().toArray(arr1);
		m.keySet().toArray(arr2);
		
		Arrays.sort(arr1);
		Arrays.sort(arr2);
		
		String key1 =  StringUtils.join(arr1);
		String key2 =  StringUtils.join(arr2);
		
		int val = key1.compareTo(key2);
		if(val == 0){
			this.values().toArray(arr1);
			m.values().toArray(arr2);
			
			String val1 = StringUtils.join(arr1);
			String val2 = StringUtils.join(arr2);
			
			return val1.compareTo(val2);
		}
		return val;
	}

}