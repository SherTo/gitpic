package com.example.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by xh on 2017/4/8.
 * 图片爬虫
 */
@Component
public class Pic {

    final String url = "http://www.87g.com/index.php?m=content&c=content_ajax&a=picture_page&siteid=1&catid=35&page=";
    final String savePath = "D:\\downloadPhoto\\";

    public void download(String urlString, String filename, String savePath) throws Exception {

        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        //设置请求超时为5s
        con.setConnectTimeout(5 * 1000);
        // 输入流
        InputStream is = con.getInputStream();

        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        File sf = new File(savePath);
        if (!sf.exists()) {
            sf.mkdirs();
        }
        OutputStream os = new FileOutputStream(sf.getPath() + "\\" + filename);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }

    public void runCrawler() {
        //返回值
        int result = 1;
        //访问页码
        Integer page = 1;
        //启动爬虫
        System.out.println("爬虫开始工作！");
        while (result == 1) {
            result = geturl(page);
            page += 1;
            if (result == 0) {
                System.out.println("爬虫运行结束！！");
            }
        }
    }

    public int geturl(int page) {
        int result = 1;
        try {
            URL url = new URL(this.url + page);
//            URL url = new URL("http://www.87g.com/index.php?m=content&c=content_ajax&a=picture_page&siteid=1&catid=35&page=121");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String content = "";
            String line = null;
            if (!ObjectUtils.isEmpty(line = in.readLine())) {
                content += line;
                in.close();
                if (content.equals("[]")) {
                    result = 0;
                    return result;
                }
                JSONObject jsonObject = JSONObject.parseObject(content);
                if (ObjectUtils.isEmpty(jsonObject)) {
                    result = 0;
                    return result;
                }
                Collection<Object> values = jsonObject.values();
                for (Object obj : values) {
                    String thumb = (String) JSON.parseObject(obj.toString()).get("thumb");
                    System.out.println(thumb);
                    download(thumb, String.valueOf(System.currentTimeMillis() + ".jpg"), savePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
