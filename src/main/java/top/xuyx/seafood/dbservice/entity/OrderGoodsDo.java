package top.xuyx.seafood.dbservice.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wx_order_goods")
public class OrderGoodsDo implements Serializable {
	private static final long serialVersionUID = -2931900511400352598L;
	
	@TableId
	private String id;

	private String userId;

	private String orderId;


	private String goodsId;

    private String goodsTitle;

    private String goodsPhoto;

    private BigDecimal goodsWholesalePrice;

    private BigDecimal goodsRetailPrice;

    private Integer goodsBuyNum;

    private String goodsAddress;


    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
