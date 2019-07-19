package top.xuyx.seafood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xuyx.seafood.common.Response;
import top.xuyx.seafood.dbservice.entity.GoodsDo;
import top.xuyx.seafood.dbservice.mapper.GoodsMapper;
import top.xuyx.seafood.model.out.IndexPageVo;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    GoodsMapper goodsMapper;

    @RequestMapping("/list")
    public Response list() {
        IndexPageVo vo = IndexPageVo.builder()
                .banners(goodsMapper.selectBanners())
                .goodsList(goodsMapper.selectGoodsList()).build();
        Response response = Response.ok(vo);
        return response;
    }

    @RequestMapping("/detail")
    public Response detail(String id) {
        GoodsDo goodsDo = goodsMapper.selectById(id);
        return Response.ok(goodsDo);
    }
}
