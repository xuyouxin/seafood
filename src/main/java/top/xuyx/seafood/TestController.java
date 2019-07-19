package top.xuyx.seafood;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/t")
@RestController
public class TestController {

    public TestController() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(60000);
        requestFactory.setConnectTimeout(60000);
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(requestFactory);
    }

    RestTemplate restTemplate;

    @RequestMapping("/test")
    public String get() {
        return "test";
    }

    @GetMapping("testIndex1")
    public long testIndex1() {
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i=0; i<1; i++){
            restTemplate.getForObject("http://cdwc.top/index", String.class);
        }
        sw.stop();
        return sw.getTotalTimeMillis();
    }

    @GetMapping("testIndex100")
    public long testIndex100() {
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i=0; i<100; i++){
            restTemplate.getForObject("http://cdwc.top/index", String.class);
        }
        sw.stop();
        return sw.getTotalTimeMillis();
    }

    @GetMapping("testRecommend1")
    public long testRecommend1() {
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i=0; i<1; i++){
            restTemplate.getForObject("http://cdwc.top/goods/recommend", String.class);
        }
        sw.stop();
        return sw.getTotalTimeMillis();
    }

    @GetMapping("testRecommend100")
    public long testRecommend100() {
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i=0; i<100; i++){
            restTemplate.getForObject("http://cdwc.top/goods/recommend", String.class);
        }
        sw.stop();
        return sw.getTotalTimeMillis();
    }


    @GetMapping("testAliImage1")
    public long testAliImage1() {
        StopWatch sw = new StopWatch();
        sw.start();

        for (int i=0; i<1; i++){
            restTemplate.getForObject("http://oss.cdwc.top/goods/B1535621483288F.jpg", String.class);
        }
        sw.stop();
        return sw.getTotalTimeMillis();
    }

    @GetMapping("testAliImage100")
    public long testAliImage100() {
        StopWatch sw = new StopWatch();
        sw.start();

        for (int i=0; i<100; i++){
            restTemplate.getForObject("http://oss.cdwc.top/goods/B1535621483288F.jpg", String.class);
            //原图：https://cdwc-image.oss-cn-hangzhou.aliyuncs.com/goods/B1535621483288F.jpg
            // 压缩的图：https://cdwc-image.oss-cn-hangzhou.aliyuncs.com/goods/1112.jpg
        }
        sw.stop();
        return sw.getTotalTimeMillis();
    }

    @GetMapping("testHWImage1")
    public long testHWImage1() {
        StopWatch sw = new StopWatch();
        sw.start();

        for (int i=0; i<1; i++){
            restTemplate.getForObject("https://seafood.obs.cn-east-2.myhuaweicloud.com/1112.jpg", String.class);
        }
        sw.stop();
        return sw.getTotalTimeMillis();
    }

    @GetMapping("testHWImage100")
    public long testHWImage100() {
        StopWatch sw = new StopWatch();
        sw.start();

        for (int i=0; i<100; i++){
            restTemplate.getForObject("https://seafood.obs.cn-east-2.myhuaweicloud.com/1112.jpg", String.class);
        }
        sw.stop();
        return sw.getTotalTimeMillis();
    }

    @GetMapping("testAliVideo")
    public long testAliVideo() {
        StopWatch sw = new StopWatch();
        sw.start();

        for (int i=0; i<1; i++){
            restTemplate.getForObject("https://qvbian-web.oss-cn-shanghai.aliyuncs.com/content/banner/F1544782663980z.mp4", String.class);
        }
        sw.stop();
        return sw.getTotalTimeMillis();
    }

}
