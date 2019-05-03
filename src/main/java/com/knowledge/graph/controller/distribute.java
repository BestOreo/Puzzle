package com.knowledge.graph.controller;

import com.alibaba.fastjson.JSONArray;
import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by geshuaiqi on 2019/4/5.
 */
@CrossOrigin
@RestController
public class distribute {

    // 获取CPU占用率
    public static double getProcessCpuLoad() throws Exception {
        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        double value = (int)(bean.getSystemCpuLoad()*100);
        return value;
    }


    private static String getRAMLoad()
    {
        final long RAM_TOTAL = Runtime.getRuntime().totalMemory();
        final long RAM_FREE = Runtime.getRuntime().freeMemory();
        final long RAM_USED = RAM_TOTAL - RAM_FREE;
        final double RAM_USED_PERCENTAGE = ((RAM_USED * 1.0) / RAM_TOTAL) * 100;
        return (int)(RAM_USED_PERCENTAGE) + "";
    }


    @GetMapping("/cpu")
    public static String monitor(HttpServletRequest request,Model model) {
        try {
            return String.format("cpu:%s,ram:%s", getProcessCpuLoad(), getRAMLoad());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/load/{id}")
    public static JSONArray getload(HttpServletRequest request,Model model) {
        StringBuffer res = new StringBuffer("[");
        try {
            for(String id: InitailConfig.serverNode.keySet()){
                String status = searchAPI.crawl(new URL("http://"+InitailConfig.serverNode.get(id)+"/cpu"));
                if(res.length() > 1){
                    res.append(",");
                }
                res.append(String.format("{id:\"%s\",ip:\"%s\",%s}",
                        id, InitailConfig.serverNode.get(id),status));
            }
            res.append("]");
            return JSONArray.parseArray(res.toString());
        }catch (Exception e){
            System.out.println(res.toString());
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/heartbeat")
    public static JSONArray heartbeat(HttpServletRequest request,Model model) {

        return null;
    }

}
