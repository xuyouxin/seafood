package top.xuyx.seafood.dbservice.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.xuyx.seafood.dbservice.entity.GoodsDo;
import top.xuyx.seafood.model.in.RequestCommonModel;

import java.util.List;

@Mapper
public interface GoodsMapper extends BaseMapper<GoodsDo> {

    List<GoodsDo> selectBanners();

    List<GoodsDo> selectGoodsList();

    List<GoodsDo> findGoods(RequestCommonModel rcm);

    int findGoodsCount();
}
