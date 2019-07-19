package top.xuyx.seafood.dbservice.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.xuyx.seafood.dbservice.entity.OrderDo;
import top.xuyx.seafood.model.in.SearchOrderModel;
import top.xuyx.seafood.model.out.BackOrderVo;
import top.xuyx.seafood.model.out.CountVo;
import top.xuyx.seafood.model.out.OrderVo;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<OrderDo> {

    List<OrderVo> findOrdersByUserId(@Param("userId") String userId, @Param("status") Integer status);

    /**
     * 更新支付状态
     * @param linkNo
     * @param payStatus
     */
    void updateOrderPay(@Param("linkNo") String linkNo, @Param("payStatus") int payStatus);

    /**
     * 更新订单状态
     * @param linkNo
     * @param status
     * @param statusDesc
     */
    void updateOrderStatus(@Param("linkNo") String linkNo, @Param("status") int status, @Param("statusDesc") String statusDesc);

    List<BackOrderVo> findAllOrders(SearchOrderModel som);

    int findAllOrdersCount(SearchOrderModel som);
}
