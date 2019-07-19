package top.xuyx.seafood.dbservice.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.xuyx.seafood.common.IdGenerator;
import top.xuyx.seafood.common.enums.OrderStatusEnum;
import top.xuyx.seafood.dbservice.entity.AddressDo;
import top.xuyx.seafood.dbservice.entity.GoodsDo;
import top.xuyx.seafood.dbservice.entity.OrderDo;
import top.xuyx.seafood.dbservice.entity.OrderGoodsDo;
import top.xuyx.seafood.dbservice.mapper.GoodsMapper;
import top.xuyx.seafood.dbservice.mapper.OrderGoodsMapper;
import top.xuyx.seafood.dbservice.mapper.OrderMapper;
import top.xuyx.seafood.model.in.OrderModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderService extends ServiceImpl<OrderMapper, OrderDo> {
	@Autowired
	private GoodsMapper goodsMapper;

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderGoodsMapper orderGoodsMapper;
	
	@Transactional
	public String charge(OrderModel om, AddressDo address) {

		/*
		 * 插入订单数据
		 */
		OrderDo order =  new OrderDo();
		String orderId = IdGenerator.generateId();
		order.setId(orderId);
		order.setUserId(om.getUserId());
		//地址信息
		order.setName(address.getName());
		order.setMobile(address.getMobile());
		order.setAddressArea(address.getAddressArea());
		order.setAddressDetail(address.getAddressDetail());
		//金额
		order.setGoodsTotal(om.getGoodsTotal());
		//订单编号和备注
		order.setOrderCode(generateOrderCode());
		order.setOrderNote(om.getOrderNote());
		//状态：待支付
		order.setStatus(OrderStatusEnum.WAIT_PAY.getCode());
		order.setStatusDesc(OrderStatusEnum.WAIT_PAY.getMessage());
		orderMapper.insert(order);
		
		/*
		 * 插入订单商品数据
		 */
		for(OrderModel.OrderGoodsModel ogm : om.getGoodsList()) {
			GoodsDo goods = ogm.getGoods();
			OrderGoodsDo og = new OrderGoodsDo();
			og.setId(IdGenerator.generateId());
			og.setUserId(om.getUserId());
			og.setOrderId(orderId);
			og.setGoodsId(ogm.getGoodsId());
			og.setGoodsTitle(goods.getTitle());
			og.setGoodsPhoto(goods.getPhoto());
			og.setGoodsBuyNum(ogm.getGoodsNum());
			og.setGoodsWholesalePrice(goods.getWholesalePrice());
			og.setGoodsRetailPrice(goods.getRetailPrice());
			og.setGoodsAddress(goods.getAddress());
			orderGoodsMapper.insert(og);
		}
		
		/*
		 * 商品库存减少
		 */
		for(OrderModel.OrderGoodsModel ogm : om.getGoodsList()) {
			GoodsDo goods = ogm.getGoods();
			int num = goods.getNum() - ogm.getGoodsNum();

			GoodsDo goodsX = new GoodsDo();
			goodsX.setId(goods.getId());
			goodsX.setNum(num);
			goodsMapper.updateById(goodsX);
		}
		return orderId;
	}
	
	/**
	 * 生成订单号，格式：年月日时分秒+毫秒
	 * @return
	 */
	private String generateOrderCode() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String orderId = now.format(formatter);
		long x = System.currentTimeMillis()%1000;
		String suffix;
		if(x < 10) {
			suffix = "00" + x;
		} else if(x < 100) {
			suffix = "0" + x;
		} else {
			suffix = "" + x;
		}
		return orderId+suffix;
	}
}
