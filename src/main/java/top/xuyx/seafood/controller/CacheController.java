package top.xuyx.seafood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xuyx.seafood.common.Response;
import top.xuyx.seafood.weixin.util.MyCache;

@RestController
@RequestMapping("/cache")
public class CacheController {

    @Autowired
    private MyCache cache;

    @RequestMapping(value = "/set")
    public Response set(String key, String value, int timeout) {
        cache.set(key, value, timeout);
        return Response.ok();
    }

    @RequestMapping(value = "/get")
    public Response get(String key) {
        return Response.ok(cache.get(key));
    }
}
