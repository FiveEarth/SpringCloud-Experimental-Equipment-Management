package edu.graduation.reserve.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "baidu-translate", url = "https://api.fanyi.baidu.com")
public interface BaiduTranslateClient {

    /**
     * 调用百度翻译 API
     *
     * @param q     要翻译的文本
     * @param from  源语言（例如 zh）
     * @param to    目标语言（例如 en）
     * @param appid 百度翻译 APPID
     * @param salt  随机数
     * @param sign  签名
     * @return JSON 字符串，包含翻译结果
     */
    @PostMapping(value = "/api/trans/vip/translate", consumes = "application/x-www-form-urlencoded")
    String translate(
            @RequestParam("q") String q,
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("appid") String appid,
            @RequestParam("salt") String salt,
            @RequestParam("sign") String sign
    );
}