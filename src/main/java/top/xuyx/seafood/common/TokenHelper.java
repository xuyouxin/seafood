package top.xuyx.seafood.common;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Component
public class TokenHelper {

    @Value("${jjwt.key}")
    private String tokenKey;

    /**
     * 创建token
     *
     */
    public String createToken(String userName) {
        String token = Jwts.builder()
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + 30L * 24 * 3600 * 1000))
                .signWith(SignatureAlgorithm.HS512, generalKey())
                .compact();
        return token;
    }

    /**
     * 由字符串生成加密key
     *
     * @return
     */
    private SecretKey generalKey() {
        // 本地的密码解码
        byte[] encodedKey = Base64.decodeBase64(tokenKey);
        // 根据给定的字节数组使用AES加密算法构造一个密钥
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 获取主题，即userId
     * @param token
     * @return
     */
    public String getSubject(String token) {
        return Jwts.parser()
                .setSigningKey(generalKey()) //设置签名
                .parseClaimsJws(token).getBody().getSubject();
    }
}
