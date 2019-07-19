package top.xuyx.seafood.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xuyx.seafood.common.IdGenerator;
import top.xuyx.seafood.common.JsonUtil;
import top.xuyx.seafood.common.Response;
import top.xuyx.seafood.common.enums.StatusEnum;
import top.xuyx.seafood.dbservice.entity.UserDo;
import top.xuyx.seafood.dbservice.mapper.UserMapper;
import top.xuyx.seafood.model.in.RequestCommonModel;
import top.xuyx.seafood.weixin.util.Constant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static top.xuyx.seafood.controller.BaseController.getCookieValue;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/getUserId")
    public Response add(@RequestBody String json) {
        RequestCommonModel rcm = JsonUtil.jsonToObject(json, RequestCommonModel.class);
        if(rcm == null) {
            return Response.fail(StatusEnum.code_202);
        }
        String openId = rcm.getOpenId();
        if (StringUtils.isBlank(openId)) {
            return Response.fail(StatusEnum.code_204);
        }
        List<UserDo> users = userMapper.selectByMap(ImmutableMap.of("openid", openId));
        UserDo user;
        if (users.size() > 0) {
            user = users.get(0);
        } else {
            user = new UserDo();
            user.setId(IdGenerator.generateId());
            user.setOpenid(openId);
            userMapper.insert(user);
        }
        return Response.ok(ImmutableMap.of("userId", user.getId()));
    }

    @RequestMapping(value = "/getOpenId")
    public Response getOpenId(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        String openid = getCookieValue(request, Constant.WEIXIN_LOGIN_COOKIE_NAME);
        if (openid != null){
            json.put("openId", openid);
        } else {
            json.put("openId", "");
        }
        return Response.ok(json);
    }
}
