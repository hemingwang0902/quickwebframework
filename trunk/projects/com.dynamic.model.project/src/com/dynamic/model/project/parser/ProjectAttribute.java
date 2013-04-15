package com.dynamic.model.project.parser;

import java.lang.reflect.Field;

/***
 * 项目属性信息
 * @author Binary
 *
 */
public class  ProjectAttribute{
	/***
	 * 源码目录
	 */
	private String src;
	/***
	 * 项目别名
	 */
	private String alias;
	/***
	 * class目录
	 */
	private String output;
	/**
	 * 是否自动部署
	 */
	private String autoDeploy;
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getAutoDeploy() {
		return autoDeploy;
	}
	public void setAutoDeploy(String autoDeploy) {
		this.autoDeploy = autoDeploy;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		Field[] fields = getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			try {
				Object value = field.get(this);
				if (value == null) {
					continue;
				} else {
					builder.append(field.getName()+":"+value+",");
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		builder.deleteCharAt(builder.lastIndexOf(","));
		builder.append("}");
		return builder.toString();
	}
}