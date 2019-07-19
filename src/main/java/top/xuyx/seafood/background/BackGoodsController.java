package top.xuyx.seafood.background;

import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xuyx.seafood.common.IdGenerator;
import top.xuyx.seafood.common.Response;
import top.xuyx.seafood.common.enums.StatusEnum;
import top.xuyx.seafood.dbservice.entity.GoodsDo;
import top.xuyx.seafood.dbservice.mapper.GoodsMapper;
import top.xuyx.seafood.dbservice.service.GoodsService;
import top.xuyx.seafood.model.in.RequestCommonModel;
import top.xuyx.seafood.model.out.PageVo;

import java.util.List;

@RestController
@RequestMapping("/back/goods")
public class BackGoodsController {

    @Autowired
    GoodsMapper goodsMapper;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/list")
    public Response list(RequestCommonModel rcm) {
        if (rcm.getCurrent() == null || rcm.getSize() == null) {
            return Response.fail(StatusEnum.code_204);
        }
        int x = (rcm.getCurrent() - 1) > 0 ? (rcm.getCurrent() - 1) : 0;
        rcm.setStart(x * rcm.getSize());
        List<GoodsDo> list = goodsMapper.findGoods(rcm);

        int count = goodsMapper.findGoodsCount();

        return Response.ok(ImmutableMap.of("list", list, "count", count));
    }

    @PostMapping("/save")
    public Response save(@RequestBody GoodsDo goodsDo) {
        if (goodsDo.getId() == null) {
            goodsDo.setId(IdGenerator.generateId());
        }
        boolean success = goodsService.insertOrUpdate(goodsDo);
        if (success) {
            return Response.ok();
        } else {
            return Response.fail(StatusEnum.code_201);
        }
    }

    @PostMapping("/delete")
    public Response delete(@RequestBody GoodsDo goodsDo) {
        boolean success = goodsService.deleteById(goodsDo.getId());
        if (success) {
            return Response.ok();
        } else {
            return Response.fail(StatusEnum.code_201);
        }
    }
}
