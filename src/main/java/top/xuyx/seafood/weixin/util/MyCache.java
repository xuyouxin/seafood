package top.xuyx.seafood.weixin.util;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.xuyx.seafood.dbservice.entity.CacheDo;
import top.xuyx.seafood.dbservice.mapper.CacheMapper;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MyCache {

    @Autowired
    private CacheMapper cacheMapper;

    public void set(String key, String value, int timeout) {
        List<CacheDo> list = cacheMapper.selectByMap(ImmutableMap.of("key_name", key));
        if (list.size() > 0) {
            CacheDo cacheDo = list.get(0);
            cacheDo.setKeyName(key);
            cacheDo.setValue(value);
            cacheDo.setTimeout(timeout);
            cacheDo.setCreateTime(LocalDateTime.now());
            cacheMapper.updateById(cacheDo);
        } else {
            CacheDo cacheDo = new CacheDo();
            cacheDo.setKeyName(key);
            cacheDo.setValue(value);
            cacheDo.setTimeout(timeout);
            cacheDo.setCreateTime(LocalDateTime.now());
            cacheMapper.insert(cacheDo);
        }
    }

    public String get(String key) {
        List<CacheDo> list = cacheMapper.selectByMap(ImmutableMap.of("key_name", key));
        if (list.size() > 0) {
            CacheDo cacheDo = list.get(0);
            int timeout = cacheDo.getTimeout();
            LocalDateTime createTime = cacheDo.getCreateTime();
            if (createTime.plusSeconds(timeout).isAfter(LocalDateTime.now())) {
                return cacheDo.getValue();
            }
        }
        return null;
    }
}
