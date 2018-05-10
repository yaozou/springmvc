package org.springframework.web.servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class DispatcherServlet extends HttpServlet {
    private Properties p = new Properties();
    private List<String> classNames= new ArrayList<String>();
    private Map<String,Object> ioc = new TreeMap<String, Object>();
    private Map<String,Method> handlerMapping = new TreeMap<String, Method>();
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
        if (ioc.isEmpty()) return;
        for (Map.Entry<String,Object> entry: ioc.entrySet()) {
            Class clazz = entry.getValue().getClass();
            StringBuffer url = new StringBuffer();
            if (clazz.isAnnotationPresent(Controller.class)){
                Controller controller = (Controller) clazz.getAnnotation(Controller.class);
                url.append(controller.value());
            }

            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                    
                }
            }
        }
    }

    private void doAutowried() {
        if (ioc.isEmpty()) return;
        for (Map.Entry<String,Object> entry: ioc.entrySet()) {
            // 1、获得所有字段
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field f : fields) {
                if(!f.isAnnotationPresent(Autowired.class)) continue;
                Autowired autowired = f.getAnnotation(Autowired.class);
                String beanName = f.getType().getName();

                f.setAccessible(true);
                try {
                    f.set(entry.getValue(),ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    private void doInstance() {
        if (classNames.isEmpty()) return;
        try{
            for (String clazzName: classNames) {
                Class clazz = Class.forName(clazzName);
                //使用了注解的才进行实例化
                // IoC容器规则： key默认使用类名首字母小写  用户自定义名字则用户优先选择 接口时可以使用接口的类型作为key
                if (clazz.isAnnotationPresent(Controller.class)){
                    String beanName = lowerFirstCase(clazz.getSimpleName());
                    ioc.put(beanName,clazz.newInstance());
                }
                else if(clazz.isAnnotationPresent(Service.class)){
                    Object instance = clazz.newInstance();

                    // 1、默认使用类名首字母小写
                    // 2、用户自定义名字则用户优先选择
                    Service service = (Service) clazz.getAnnotation(Service.class);
                    String beanName = service.value().trim();
                    if ("".equals(beanName)) beanName = lowerFirstCase(clazz.getSimpleName());
                    ioc.put(beanName,instance);
                    // 3、接口时可以使用接口的类型作为key
                    Class[] interfaces =  clazz.getInterfaces();
                    for (Class clazz1: interfaces) {
                        ioc.put(lowerFirstCase(clazz1.getSimpleName()),instance);
                    }
                }
                else if(clazz.isAnnotationPresent(Repository.class)){
                    Object instance = clazz.newInstance();
                    Service service = (Service) clazz.getAnnotation(Service.class);
                    String beanName = service.value().trim();
                    if ("".equals(beanName)) beanName = lowerFirstCase(clazz.getSimpleName());
                    ioc.put(beanName,instance);
                }
                else
                    continue;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doScanner(String packageName) {
        //递归扫描
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("//.","/"));
        File classDir = new File(url.getFile());
        for (File file:classDir.listFiles()) {
            if (file.isDirectory()) doScanner(packageName+"."+file.getName());
            else{
                String clazzName = packageName+"."+file.getName().replace("class","");
                classNames.add(clazzName);
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

    /**
     * 首字母小写
     * @param name
     * @return
     */
    private String lowerFirstCase(String name){
        char[] chars = name.toCharArray();
        chars[0] += 32;
        return chars.toString();
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
