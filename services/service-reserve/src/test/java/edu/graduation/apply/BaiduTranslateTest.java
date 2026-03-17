package edu.graduation.reserve;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.graduation.reserve.config.MD5Util;
import edu.graduation.reserve.feign.BaiduTranslateClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest
public class BaiduTranslateTest {

    private final BaiduTranslateClient baiduTranslateClient;
    private final ObjectMapper objectMapper;

    //    @Value("${baidu.translate.appid}")
//    private String appid;
//    @Value("${baidu.translate.secret}")
//    private String secretKey;
    @Autowired
    public BaiduTranslateTest(BaiduTranslateClient baiduTranslateClient, ObjectMapper objectMapper) {
        this.baiduTranslateClient = baiduTranslateClient;
        //美化json
        this.objectMapper = objectMapper;
    }

    @Test
    public void testTranslatePost() throws Exception{
        String q = "'一个feign测试';一个'feign'测试;一个Feign测试;一个'Feign'测试";
        String from = "zh";
        String to = "en";
        String appid="20251229002529659";
        String salt = String.valueOf(new Random().nextInt(100000));
        String sign = MD5Util.md5(appid + q + salt + "c8a1XTrGSPrqlVb9CLDj");

        // 用 POST 调用百度翻译 API
//        String result = baiduTranslateClient.translate(q, from, to, appid, salt, sign);
//        System.out.println("翻译结果: " + result);
        String jsonResult = baiduTranslateClient.translate(q, from, to, appid, salt, sign);

        // 美化 JSON 输出
        Object json = objectMapper.readValue(jsonResult, Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        System.out.println("翻译结果:\n" + prettyJson);
    }
}