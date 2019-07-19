package top.xuyx.seafood.dbservice.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("wx_cache")
public class CacheDo implements Serializable {
    private static final long serialVersionUID = -2931900511400352598L;

    @TableId
    private int id;

    private String keyName;

    private String value;

    private LocalDateTime createTime;

    private int timeout;
}
