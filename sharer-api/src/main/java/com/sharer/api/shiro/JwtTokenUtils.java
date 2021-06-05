package com.sharer.api.shiro;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sharer.api.shiro.vo.UserTokenVo;
import com.sharer.common.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 功能描述
 *
 * @author: zyu
 * @description: APP登录Token的生成和解析
 * @date: 2019/3/30 15:14
 */
public class JwtTokenUtils {

    private final static Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);
    /**
     * token秘钥，请勿泄露，请勿随便修改 backups:JKKLJOoadsafa
     */
    public static final String SECRET = "JKKLJOoadsafa";

    /**
     * token 过期时间: 10天
     */
    public static final int calendarField = Calendar.DATE;
    public static final int calendarInterval = 10;

    /**
     * JWT生成Token.<br/>
     * <p>
     * JWT构成: header, payload, signature
     *
     * @param userTokenVo 登录成功后用户的基本信息
     */
    public static String createToken(UserTokenVo userTokenVo) throws Exception {
        Date iatDate = new Date();
        // expire time
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(calendarField, calendarInterval);
        Date expiresDate = nowTime.getTime();

        // header Map
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        // build token
        // param backups {iss:Service, aud:APP}
        String token = JWT.create().withHeader(map) // header
                //.withClaim("iss", "Service") // payload
                //.withClaim("aud", "APP")
                .withClaim("userTokenVo", JsonUtils.toJSONString(userTokenVo))
                .withIssuedAt(iatDate) // sign time
                .withExpiresAt(expiresDate) // expire time
                .sign(Algorithm.HMAC256(SECRET)); // signature

        return token;
    }


    /**
     * 解密Token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static Map<String, Claim> verifyToken(String token) {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            // e.printStackTrace();
            // token 校验失败, 抛出Token验证非法异常
            return null;
        }
        return jwt.getClaims();
    }


    /**
     * 根据Token获取userVo
     *
     * @param token
     * @return user_id
     */
    public static UserTokenVo userVo(String token) throws Exception {
        Map<String, Claim> claims = verifyToken(token);
        Claim userTOkenVo = claims.get("userTokenVo");
        if (StringUtils.isEmpty(userTOkenVo) || StringUtils.isEmpty(userTOkenVo)) {
            // token 校验失败, 抛出Token验证非法异常
            throw new Exception("token参数异常!");
        }
//        logger.error(userTOkenVo.asString());
        return JsonUtils.json2Object(userTOkenVo.asString(), UserTokenVo.class);
    }

    public static void main(String[] args) throws Exception {

    }

}