package top.xuyx.seafood.model.out;

import lombok.Builder;
import lombok.Data;
import top.xuyx.seafood.dbservice.entity.GoodsDo;

import java.util.List;

/**
 * 首页的接口vo
 */
@Builder
@Data
public class IndexPageVo {

    private List<GoodsDo> banners;

    private List<GoodsDo> goodsList;
}
