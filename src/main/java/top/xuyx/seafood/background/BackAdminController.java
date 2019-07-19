package top.xuyx.seafood.background;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xuyx.seafood.common.Response;
import top.xuyx.seafood.common.TokenHelper;
import top.xuyx.seafood.model.in.RequestCommonModel;

@RestController
@RequestMapping("/back")
public class BackAdminController {

    @Autowired
    private TokenHelper tokenHelper;

    @PostMapping("/login")
    public Response login(@RequestBody RequestCommonModel rcm) {
        String name = rcm.getName();
        String password = rcm.getPassword();
        if (Strings.isBlank(name) || Strings.isBlank(password)) {
            return Response.fail("请输入用户名和密码!");
        }

        if ("admin".equals(name) && "Zzh931226".equals(password)) {
            String token = tokenHelper.createToken(name);
            return Response.ok(token);
        } else {
            return Response.fail("用户名或密码错误!");
        }
    }
}
