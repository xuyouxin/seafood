package top.xuyx.seafood.model.in;

import lombok.Data;
import top.xuyx.seafood.dbservice.entity.GoodsDo;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderModel {
	private String userId;
	private List<OrderGoodsModel> goodsList;
	private BigDecimal goodsTotal;
	private String addressId;
	private String orderNote;

	@Data
	public static class OrderGoodsModel {
		private String goodsId;
		private Integer goodsNum;
		private GoodsDo goods; //由程序根据goodsId获得
	}
}
