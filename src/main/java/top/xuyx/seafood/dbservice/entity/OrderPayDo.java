package top.xuyx.seafood.dbservice.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

@Data
@TableName("wx_order_pay")
public class OrderPayDo implements Serializable {
	private static final long serialVersionUID = -2931900511400352598L;
	
	@TableId
	private String id;

	private String orderId;

	private String orderCode;

	private String openid;

	private String channelNo;

	private String ip;

	private BigDecimal money;

	private Integer payStatus;

	private LocalDateTime createTime;

	private LocalDateTime updateTime;
}
