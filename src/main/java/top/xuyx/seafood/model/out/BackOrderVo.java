package top.xuyx.seafood.model.out;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BackOrderVo {
    private String id;
    private String orderCode;
    private String userName;
    private String name;
    private String mobile;
    private String addressArea;
    private String addressDetail;
    private BigDecimal goodsTotal;
    private int status;
    private String statusDesc;
    private String orderNote;
    private LocalDateTime createTime;

    private List<OrderVo.OrderGoodsVo> orderGoods;
}
