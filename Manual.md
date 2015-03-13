# 介绍 #

详细使用手册还在整理中。


# 详情 #

1.在eclipse安装目录的dropins目录下面建立一个quickwebframework目录

2.将相关的压缩包中的bundles目录中的所有jar文件放到上面的dropins/quickwebframework目录中。

3.重新启动eclipse

4.在eclipse项目中新建Dynamic Web Project项目，参考example中qwf.test.web.war中的配置进行配置。

5.在新建的WEB项目的WEB-INF目录下建立目录plugins，将要用到的QuickWebFramework的功能模块复制到此目录。(比如用到了velocity，就要把qwf-vr-velocity模块及依赖的模块的jar文件复制到此文件)

6.新建Plug-in Project项目，Target Platform选择an OSGi framework->standard。

7.打开新建的OSGi模块项目的META-INF/MANIFEST.MF文件，设置Dependencies，在Required Plug-ins中添加qwf-core及其他要用到的组件。

8.根据你实际项目的情况选择视图框架，视图渲染器等进行开发

9 下面说运行调试部分了，在eclipse中配置tomcat(7以上版本)，将web项目添加到tomcat中，启动tomcat，启动完成后应该可以看到QuickWebFramework的默认页面和插件管理页面了。

10.将开发的WEB功能模块导出为Deployable plug-ins and fragments，目录选择为此Web项目在tomcat部署路径的WEB-INF目录，在Options中勾选Use class files compiled in the workspace，即可以自动安装到OSGi环境中。