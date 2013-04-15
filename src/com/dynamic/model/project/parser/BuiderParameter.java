package com.dynamic.model.project.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.dynamic.model.project.Constants;
import com.dynamic.model.project.builder.DynamicWebNature;
import com.dynamic.model.project.util.Console;
import com.dynamic.model.project.util.ProjectUtil;

public class BuiderParameter{
	//property
	private static final String SRC="src";
	private static final String OUTPUT="output";
	private static final String DEPLOY="deploy";
	private static final String MODULE="module";
	private static final String ALIAS = "alias";
	private static final String AUTODEPLOY = "autoDeploy";
	private static final String MODULEREPOSITORY="moduleRepository";
	/****
	 * 项目属性信息
	 */
	private static Map<String, ProjectAttribute> projectMap=new HashMap<String,ProjectAttribute>();
	/***
	 * 初始化项目参数
	 * @param project
	 */
	public  void initialize(IProject project){
		if(project.exists()){
			parserDeploy(project);
			parserProjectAttribute();
		}
	}
	/***
	 * 返回依赖项
	 * @return
	 */
	public static Set<String> getDependModel(){
		return projectMap.keySet();
	}
	/***
	 * 获得项目Output属性信息
	 * @param projectName
	 * @return
	 */
	public static String getProjectOutput(IProject project){
		ProjectAttribute attribute=projectMap.get(project.getName());
		if(attribute==null){
			return OUTPUT;
		}
		return attribute.getOutput();
	}
	/***
	 * 获得项目Src属性信息
	 * @param projectName
	 * @return
	 */
	public static String getProjectSrc(IProject project){
		ProjectAttribute attribute=projectMap.get(project.getName());
		if(attribute==null){
			return SRC;
		}
		return attribute.getSrc();
	}
	/***
	 * 获得项目Alias属性信息
	 * @param projectName
	 * @return
	 */
	public static String getProjectAlias(IProject project){
		ProjectAttribute attribute=projectMap.get(project.getName());
		if(attribute==null){
			return ALIAS;
		}
		return attribute.getAlias();
	}
	/***
	 * 获得项目autoDeploy属性信息
	 * @param projectName
	 * @return
	 */
	public static boolean getProjectAutoDeploy(IProject project){
		ProjectAttribute attribute=projectMap.get(project.getName());
		if(attribute==null||attribute.getAutoDeploy()==null){
			return false;
		}else if(attribute.getAutoDeploy().equalsIgnoreCase("true")){
			return true;
		}
		return false;
	}
	/***
	 * 
	 * @param projectName
	 * @return
	 */
	public static boolean contains(String projectName){
		return	projectMap.containsKey(projectName);
	}
	/***
	 * 解析部署信息
	 * @param deploy
	 * @return
	 */
	private void parserDeploy(IProject project){
		projectMap.clear();
		String deployFile=BuilderProperty.getDestination(DynamicWebNature.NATURE_ID,DEPLOY);
		IFile deploy = project.getFile(deployFile);
		Console.println("Parser Deploy File:"+deploy.getLocation().toString());
		if (!deploy.exists()){
			Console.println("Deploy File:"+deploy.getLocation().toString()+" Not find , Program Return.");
			return;
		}
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(deploy.getContents());
			Element projecModules = document.getRootElement();
			Element repository = projecModules.element(MODULEREPOSITORY);
			List<Element> moduleList = repository.elements(MODULE);
			for (Element module : moduleList) {
				String value=module.getStringValue();
				if(value==null||value.equals("")){
					continue;
				}
				projectMap.put(value.trim(),null);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	/***
	 * 解析项目信息
	 * @param project
	 */
	private void parserProjectAttribute(){
		Set<String> projectSet = projectMap.keySet();
		IWorkspaceRoot spaceRoot=ResourcesPlugin.getWorkspace().getRoot();
		for(String item : projectSet){
			//解析模块项目配置信息
			IProject resource = spaceRoot.getProject(item);
			if(!ProjectUtil.isPluginProject(resource)){
				projectMap.remove(resource.getName());
				Console.println("Project "+resource.getName()+" Is Not Correct Dynamic Plugin Project.");
				return;
			}
			IFile modelXml=resource.getFile(Constants.MODEL_XML);
			Console.println("Parser Attribute File:"+modelXml.getLocation().toString());
			if(!modelXml.exists()){
				Console.println("Attribute File:"+modelXml.getLocation().toString()+" Not find , Program Return.");
				return ;
			}
			ProjectAttribute attribute=new ProjectAttribute();
			SAXReader saxReader = new SAXReader();
			try {
				Document document = saxReader.read(modelXml.getContents());
				Element dynamicModel = document.getRootElement();
				Element tagElement =null;
				//ALIAS
				tagElement = dynamicModel.element(ALIAS);
				attribute.setAlias(tagElement.getStringValue());
				//AUTODEPLOY
				tagElement = dynamicModel.element(AUTODEPLOY);
				attribute.setAutoDeploy(tagElement.getStringValue());
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
			//解析项输出信息
			IFile dotClasspath=resource.getFile(Constants.DOT_CLASSPATH);
			if(!dotClasspath.exists()){
				return;
			}
			Console.println("Parser Attribute File:"+dotClasspath.getLocation().toString());
			try {
				Document document = saxReader.read(dotClasspath.getContents());
				Element classpath = document.getRootElement();
				List<Element> classpathentry = classpath.elements("classpathentry");
				for (Element property : classpathentry) {
					Attribute attr = property.attribute("kind");
					String value=property.attribute("path").getValue();
					if (attr.getValue().equals(SRC)){
						attribute.setSrc(value);
					}else if(attr.getValue().equals(OUTPUT)){
						attribute.setOutput(value);
					}
				}
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
			Console.println("Project "+resource.getName()+" Information:"+attribute.toString());
			projectMap.put(item,attribute);
		}
	}
	/****
	 * web上下文
	 * @param module
	 * @return eg：dynamic.web
	 */
	public String getWebContent(IProject project) {
		IFile componentFile = project.getFile(
				".settings/org.eclipse.wst.common.component");
		if(componentFile==null){
			return "WebContent";
		}
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(componentFile.getContents());
			Element projecModules = document.getRootElement();
			List<Element> webModules = projecModules.elements("wb-resource");
			for (Element element : webModules) {
				Attribute att = element.attribute("deploy-path");
				if (att.getValue().equals("/")){
					return element.attribute("source-path").getValue().substring(1);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return "WebContent";
	}
}
