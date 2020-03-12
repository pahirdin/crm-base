package com.asiainfo.crm.sec.web.privchecker.data;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.*;

public class ParamRightMap extends TreeMap<Map<String, String>, Set<String>> implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3996203916695663219L;

	public ParamRightMap(){
		super(new ParamRightMapComparator());
	}
	
	public static class ParamRightMapComparator implements Serializable, Comparator<Map<String, String>>{
		/**
		 * 
		 */
		private static final long serialVersionUID = -1987134796598507498L;

		/**
		 * 根据参数key和value倒序排列
		 */
		@Override
		public int compare(Map<String, String> m1, Map<String, String> m2) {
			int size1 = m1.size();
			int size2 = m2.size();
			
			if(size1 != size2){
				return size2 - size1;
			}
			
			String[] arr1 = new String[size1];
			String[] arr2 = new String[size2];
			
			m1.keySet().toArray(arr1);
			m2.keySet().toArray(arr2);	
			
			Arrays.sort(arr1);
			Arrays.sort(arr2);
			
			String key1 =  StringUtils.join(arr1);
			String key2 =  StringUtils.join(arr2);
			
			int val = key2.compareTo(key1);
			if(val == 0){
				m1.values().toArray(arr1);
				m2.values().toArray(arr2);
				
				String val1 = StringUtils.join(arr1);
				String val2 = StringUtils.join(arr2);
				
				return val2.compareTo(val1);
			}
			return val;
		}
	}
}