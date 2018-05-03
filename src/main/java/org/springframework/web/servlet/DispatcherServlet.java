package org.springframework.web.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class DispatcherServlet extends HttpServlet {
    private Properties p = new Properties();
    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //2、根据配置文件扫描所以的相关的类
        doScanner(p.getProperty("scanPackage"));
        //3、初始化所有的相关类的实例，并且将其放入到Ioc容器之中，也就是map之中
        doInstance();
        //4、自动实现依赖注入
        doAutowried();
        //5、初始化HandleMapping
        initHandleMapping();
    }

    private void initHandleMapping() {
    }

    private void doAutowried() {
    }

    private void doInstance() {
    }

    private void doScanner(String packageName) {
        //递归扫描
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("//.","/"));
        File classDir = new File(url.getFile());
        for (File file:classDir.listFiles()) {
            if (file.isDirectory()) doScanner(packageName+"."+file.getName());
            else{

            }
        }
    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream is =  this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            p.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (is != null)
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
