package wos.server.rest.auth;

import com.jeesite.common.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import wos.server.rest.auth.entity.UserSessionRestEntity;

/**
 * 缓存
 */
public class Caches {

    /**
     * 登录令牌缓存
     */
    private static HashMap<String, UserSessionRestEntity> userSessionMap=new HashMap<>();

    /**
     * 缓存用户令牌
     * @param userSession
     */
    public static void putUserSession(UserSessionRestEntity userSession) {
        if (userSession==null) return;
        if (StringUtils.isEmpty(userSession.getSessionId())) return;
        if (StringUtils.isEmpty(userSession.getUserId())) return;
        userSessionMap.put(userSession.getSessionId(),userSession);
    }

    /**
     * 移除用户令牌缓存
     * @param sessionId
     */
    public static void removeUserSession(String sessionId) {
        if (StringUtils.isNotEmpty(sessionId) && userSessionMap.containsKey(sessionId)) userSessionMap.remove(sessionId);
    }

    /**
     * 获取用户登录令牌
     * @param sessionId
     * @return
     */
    public static UserSessionRestEntity getUserSession(String sessionId) {
        if (StringUtils.isEmpty(sessionId) && !userSessionMap.containsKey(sessionId)) return null;
        return userSessionMap.get(sessionId);
    }

    /**
     * 获取用户登录令牌
     * @param userCode
     * @return
     */
    public static UserSessionRestEntity getUserSessionByUserCode(String userCode) {
        if (StringUtils.isEmpty(userCode)) return null;
        return userSessionMap.values().stream()
                .filter(p->userCode.equals(p.getUserCode()))
                .findFirst().orElse(null);
    }

    /**
     * 检查令牌是否有效
     * @param sessionId
     * @return
     */
    public static boolean checkUserSession(String sessionId) {
        return StringUtils.isNotEmpty(sessionId) && userSessionMap.containsKey(sessionId);
    }

    /**
     * 根据用户名删除令牌
     * @param userCode
     * @return
     * @throws Exception
     */
    public static boolean removeUserSessionByUserCode(String userCode) throws Exception {
        if (StringUtils.isEmpty(userCode)) throw new Exception("用户登录名不允许为空");
        List<UserSessionRestEntity> userSessionList=userSessionMap.values().stream()
                .filter(p->userCode.equals(p.getUserCode())).collect(Collectors.toList());
        if (userSessionList==null || userSessionList.isEmpty()) return true;
        userSessionList.forEach(userSession -> {
            String sessionId=userSession.getSessionId();
            if (StringUtils.isEmpty(sessionId)) return;
            if (!userSessionMap.containsKey(sessionId)) return;
            userSessionMap.remove(sessionId);
        });
        return true;
    }

}
