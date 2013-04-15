package com.dynamic.model.project.deploy;

import java.util.ArrayList;
import java.util.List;


public class DeployModel {
	
	List<String> modulesList;
	
	public List<String> getModulesList() {
		return modulesList;
	}
	public void setModulesList(List<String> modulesList) {
		this.modulesList = modulesList;
	}
	public void addModule(String module) {
		if(this.modulesList==null){
			this.modulesList=new ArrayList<String>();
		}
		this.modulesList.add(module);
	}
}
