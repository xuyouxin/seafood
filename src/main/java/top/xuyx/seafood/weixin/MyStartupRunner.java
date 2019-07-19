package top.xuyx.seafood.weixin;

import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;  
import org.springframework.core.annotation.Order;  
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j; 

@Slf4j
@Component  
@Order(value=1)
public class MyStartupRunner implements CommandLineRunner  
{  
	@Autowired
	private WeiXinSDK weixinSdk;
	
    @Override  
    public void run(String... args) throws Exception  
    {  
    	log.info("test>>定时器启动");
    	Timer timer = new Timer();
		timer.schedule(new MyTask(), 1000 * 60 * 60);// 在1秒后执行此任务,每次间隔3600秒执行一次
    }  
    
    public class MyTask extends java.util.TimerTask {
		public synchronized void run() {
			try {
				log.info("WeiXin Token 更新:Begin...");

				long old = System.currentTimeMillis();

				{// 同时获取
					String accessToken = weixinSdk.doGetAccessToken();
					String jsApiTicket = weixinSdk.doGetTicket();
					log.debug(String.format("accessToken=%s ;j sApiTicket=%s", accessToken, jsApiTicket));
				}
				long now = System.currentTimeMillis();

				log.info("WeiXin Token 更新: 耗时:" + (now - old) + "毫秒 End...");

			} catch (Exception e) {
				log.error("", e);
			}

		}
	}
}  
