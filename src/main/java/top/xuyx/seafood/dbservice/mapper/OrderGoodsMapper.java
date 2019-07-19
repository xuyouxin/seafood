package top.xuyx.seafood.dbservice.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.xuyx.seafood.dbservice.entity.OrderGoodsDo;
import top.xuyx.seafood.model.out.OrderVo;

import java.util.List;

@Mapper
public interface OrderGoodsMapper extends BaseMapper<OrderGoodsDo> {

    List<OrderVo.OrderGoodsVo> findOrderGoodsByOrderId(String orderId);

    List<OrderVo.OrderGoodsVo> findOrderGoodsByOrderIds(@Param("orderIds") List<String> orderIds);
}
