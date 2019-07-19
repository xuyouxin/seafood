package top.xuyx.seafood.weixin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.xuyx.seafood.common.Response;
import top.xuyx.seafood.common.enums.OrderStatusEnum;
import top.xuyx.seafood.common.enums.StatusEnum;
import top.xuyx.seafood.dbservice.entity.OrderDo;
import top.xuyx.seafood.dbservice.entity.OrderPayDo;
import top.xuyx.seafood.dbservice.mapper.OrderMapper;
import top.xuyx.seafood.dbservice.mapper.OrderPayMapper;
import top.xuyx.seafood.weixin.util.Constant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static top.xuyx.seafood.common.IdGenerator.generateId;

@Slf4j
@Service
public class WeiXinService {
	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderPayMapper orderPayMapper;

	public Response orderPay(String title, String openId, String ip, OrderDo order) {
		String linkNo = generateLinkNo();
		log.error(String.format("生成订单>> title:%s, openId:%s, ip:%s, linkNo:%s, money:%s", title, openId, ip,  linkNo, order.getGoodsTotal().toString()));
		String xml = WeiXinSDK.WXOrderJSAPI(title, order.getGoodsTotal(), openId, linkNo, ip);
		Map<String, Object> retMap = WeiXinSDK.combineH5PayObject(xml);
		if(retMap.size() > 0 ) {
			//保存支付记录
			OrderPayDo op = new OrderPayDo();
			op.setId(generateId());
			op.setOrderId(order.getId());
			op.setOrderCode(linkNo);
			op.setOpenid(openId);
			op.setIp(ip);
			op.setMoney(order.getGoodsTotal());
			op.setPayStatus(Constant.PayStatus.NO);
			orderPayMapper.insert(op);
			
			log.error("生成支付订单成功>>" + retMap);
			return Response.ok(retMap);
		} else {
			log.error("生成支付订单失败");
			return Response.fail(StatusEnum.code_216);
		}
	}
	
	/**
	 * 生成支付流水号，格式："wx" + 年月日时分秒+毫秒
	 * @return
	 */
	private String generateLinkNo() {
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
		return "wx"+orderId+suffix;
	}
	
	/**
	 * 更新支付结果
	 * @param isOk
	 * @param linkNo
	 */
	public void updateOrderPay(boolean isOk, String linkNo) {
		int payStatus;
		if(isOk) {
			payStatus = Constant.PayStatus.YES;
			orderMapper.updateOrderStatus(linkNo, OrderStatusEnum.WAIT_SEND.getCode(), OrderStatusEnum.WAIT_SEND.getMessage());
		} else {
			payStatus = Constant.PayStatus.FAIL;
		}
		orderMapper.updateOrderPay(linkNo, payStatus);
	}
}
