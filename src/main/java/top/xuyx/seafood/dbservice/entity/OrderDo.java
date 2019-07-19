package top.xuyx.seafood.dbservice.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wx_order")
public class OrderDo implements Serializable {
	private static final long serialVersionUID = -2931900511400352598L;
	
	@TableId
	private String id;

	private String userId;

	private String name;

	private String mobile;

	private String addressArea;

	private String addressDetail;

	/**
	 * 订单编号
	 */
	private String orderCode;

	/**
	 * 商品总价
	 */
	private BigDecimal goodsTotal;

	private String orderNote;

	private Integer status;

	private String statusDesc;

	private LocalDateTime createTime;

	private LocalDateTime updateTime;

}
