package org.salina.android.rest;

import java.util.HashMap;
import java.util.Map;

/**
 * RestClient를 이용해서 전송할 클래스로써<br/>
 * 전송 정보와 데이터를 포함한 클래스
 * @author nnoco
 *
 */
public class TransferData {
	public static final String WRAPPED_DATA = "wrapped_data";
	
	private Map<String, Object> dataMap;
	
	public TransferData(){
		this(new HashMap<String, Object>());
	}
	
	public TransferData(Map<String, Object> dataMap){
		this.dataMap = dataMap;
	}
	
	/**
	 * 전송할 데이터맵에 name-data 쌍으로 데이터를 추가함
	 * @param name 추가할 데이터 이름
	 * @param data 추가할 데이터 객체
	 */
	public void addData(String name, Object data){
		dataMap.put(name, data);
	}
	
	/**
	 * 전송할 데이터맵에서 데이터 삭제
	 * @param name 삭제할 데이터 이름
	 */
	public void removeData(String name){
		dataMap.remove(name);
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}
	
	
}
