package top.xuyx.seafood.dbservice.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wx_goods")
public class GoodsDo implements Serializable {
	private static final long serialVersionUID = -2931900511400352598L;
	
	@TableId
	private String id;

    private String title;

    private String photo;

    private BigDecimal wholesalePrice;

    private BigDecimal retailPrice;

    private Integer num;

    private Integer unit;

    private String address;

    private Boolean inBanner;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
