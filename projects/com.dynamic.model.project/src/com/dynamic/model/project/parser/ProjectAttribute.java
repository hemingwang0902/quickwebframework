package com.dynamic.model.project.parser;

import java.lang.reflect.Field;

/***
 * ��Ŀ������Ϣ
 * @author Binary
 *
 */
public class  ProjectAttribute{
	/***
	 * Դ��Ŀ¼
	 */
	private String src;
	/***
	 * ��Ŀ����
	 */
	private String alias;
	/***
	 * classĿ¼
	 */
	private String output;
	/**
	 * �Ƿ��Զ�����
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