package top.xuyx.seafood.background;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xuyx.seafood.common.Response;
import top.xuyx.seafood.common.StreamUtils;
import top.xuyx.seafood.common.enums.StatusEnum;
import top.xuyx.seafood.dbservice.mapper.OrderGoodsMapper;
import top.xuyx.seafood.dbservice.mapper.OrderMapper;
import top.xuyx.seafood.model.in.SearchOrderModel;
import top.xuyx.seafood.model.out.BackOrderVo;
import top.xuyx.seafood.model.out.OrderVo.OrderGoodsVo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/back/order")
public class BackOrderController {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderGoodsMapper orderGoodsMapper;

    @PostMapping("/list")
    public Response list(@RequestBody SearchOrderModel som) {
        if (som.getCurrent() == null || som.getSize() == null) {
            return Response.fail(StatusEnum.code_204);
        }
        int x = (som.getCurrent() - 1) > 0 ? (som.getCurrent() - 1) : 0;
        som.setStart(x * som.getSize());
        List<BackOrderVo> list = orderMapper.findAllOrders(som);

        List<String> ids = list.stream().map(BackOrderVo::getId).collect(Collectors.toList());
        if (!ids.isEmpty()) {
            List<OrderGoodsVo> orderGoods = orderGoodsMapper.findOrderGoodsByOrderIds(ids);
            Map<String, List<OrderGoodsVo>> orderGoodsMap = StreamUtils.groupingBy(orderGoods, OrderGoodsVo::getOrderId);
            list.forEach(vo -> vo.setOrderGoods(orderGoodsMap.get(vo.getId())));
        }

        int count = orderMapper.findAllOrdersCount(som);

        return Response.ok(ImmutableMap.of("list", list, "count", count));
    }
}
