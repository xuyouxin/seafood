package top.xuyx.seafood.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xuyx.seafood.common.JsonUtil;
import top.xuyx.seafood.common.Response;
import top.xuyx.seafood.common.enums.StatusEnum;
import top.xuyx.seafood.dbservice.entity.AddressDo;
import top.xuyx.seafood.dbservice.entity.GoodsDo;
import top.xuyx.seafood.dbservice.mapper.*;
import top.xuyx.seafood.dbservice.service.OrderService;
import top.xuyx.seafood.model.in.OrderModel;
import top.xuyx.seafood.model.out.OrderVo;
import top.xuyx.seafood.model.in.RequestCommonModel;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private AddressMapper addressMapper;
	@Autowired
	private GoodsMapper goodsMapper;
	
	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderGoodsMapper orderGoodsMapper;

	@PostMapping("/add")
	public Response add(@RequestBody String json) {
		OrderModel om = JsonUtil.jsonToObject(json, OrderModel.class);
		if(om == null) {
			return Response.fail(StatusEnum.code_202);
		}
		if(StringUtils.isAnyBlank(om.getUserId(), om.getAddressId()) 
				|| om.getGoodsList()==null || om.getGoodsList().isEmpty()
				|| om.getGoodsTotal()==null ) {
			return Response.fail(StatusEnum.code_204);
		}
		if(userMapper.selectById(om.getUserId())==null) {
			return Response.fail(StatusEnum.code_205);
		}
		AddressDo address = addressMapper.selectById(om.getAddressId());
		if(address == null) {
			return Response.fail(StatusEnum.code_206);
		}

		//判断商品id，以及库存
		for(OrderModel.OrderGoodsModel ogm : om.getGoodsList()) {
			GoodsDo goods = goodsMapper.selectById(ogm.getGoodsId());
			if(goods == null) {
				return Response.fail(StatusEnum.code_203);
			}
			if(goods.getNum() < ogm.getGoodsNum()) {
				return Response.fail(StatusEnum.code_208); //库存不足
			}
			ogm.setGoods(goods);
		}

		//核对金额
		BigDecimal goodsTotal = computeGoodsTotal(om);
		if(goodsTotal.compareTo(om.getGoodsTotal())!=0 ) {
			/*
			 * 注意，BigDecimal比较不能用equals。equals比较时，1与1.0认为两者不相等。
			 */
			return Response.fail(StatusEnum.code_207); //金额不对
		}

		//交易
		String orderId = orderService.charge(om, address);
		JSONObject result = new JSONObject();
		result.put("orderId", orderId);
		return Response.ok(result);
	}

	/**
	 * 注意：如果一种商品的购买数量大于30斤，则该商品按批发价；否则，按零售价
	 * @return
	 */
	private BigDecimal computeGoodsTotal(OrderModel om) {
		BigDecimal goodsTotal = new BigDecimal(0);
		for(OrderModel.OrderGoodsModel ogm : om.getGoodsList()) {
			if (ogm.getGoodsNum() >= 30) {
				goodsTotal = goodsTotal.add(ogm.getGoods().getWholesalePrice().multiply(new BigDecimal(ogm.getGoodsNum())));
			} else {
				goodsTotal = goodsTotal.add(ogm.getGoods().getRetailPrice().multiply(new BigDecimal(ogm.getGoodsNum())));
			}
		}
		return goodsTotal;
	}
	
	@PostMapping("/select")
	public Response select(@RequestBody String json) {
		RequestCommonModel rcm = JsonUtil.jsonToObject(json, RequestCommonModel.class);
		if(rcm == null) {
			return Response.fail(StatusEnum.code_202);
		}
		if(StringUtils.isBlank(rcm.getUserId())) {
			return Response.fail(StatusEnum.code_204);
		}
		List<OrderVo> orderList = orderMapper.findOrdersByUserId(rcm.getUserId(), rcm.getStatus());
		for(OrderVo ov : orderList) {
			ov.setGoodsInfo(orderGoodsMapper.findOrderGoodsByOrderId(ov.getOrderId()));
		}
		JSONObject result = new JSONObject();
		result.put("orderList", orderList);
		return Response.ok(result);
	}

}
