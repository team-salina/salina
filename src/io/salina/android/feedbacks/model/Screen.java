package io.salina.android.feedbacks.model;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class Screen {
	@Attribute(name="name")
	private String name;
	
	@Attribute(name="class")
	private String clazz;
	
	@ElementList
	private List<String> functions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public List<String> getFunctions() {
		return functions;
	}

	public void setFunctions(List<String> functions) {
		this.functions = functions;
	}
}
