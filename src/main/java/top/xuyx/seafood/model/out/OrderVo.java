package top.xuyx.seafood.model.out;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderVo {
	private String orderId;
	private String orderCode;
	private Integer status;
	private String statusDesc;
	private List<OrderGoodsVo> goodsInfo;
	private BigDecimal goodsTotal;
	private String orderNote;

	@Data
	public static class OrderGoodsVo {
		private String orderId;
		private String goodsId;
		private String title;
		private String photo;
		private BigDecimal goodsWholesalePrice;
		private BigDecimal goodsRetailPrice;
		private Integer goodsBuyNum;
		private Integer unit;
	}
}
