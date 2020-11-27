package com.app2.engine.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthorizeUtil implements Serializable {
    public static final String USER_NAME_KEY = "SESSION-USERNAME";
    public static final String USER_ROLE_KEY = "SESSION-ROLE";
    private Map<String, List<String>> mapUserDetails = new HashMap<String, List<String>>();
    private Map<String, Object> mapKey = new HashMap<String, Object>();

    public AuthorizeUtil() {
        mapKey.put(USER_NAME_KEY, USER_NAME_KEY);
        mapKey.put(USER_ROLE_KEY, USER_ROLE_KEY);
    }

    public Map<String, List<String>> getMapUserDetails() {
        return mapUserDetails;
    }

    public String getUsername() {
        List<String> listUsername = mapUserDetails.get(USER_NAME_KEY);
        if (AppUtil.isNotNull(listUsername) && listUsername.size() > 0) {
            return listUsername.get(0);
        } else {
            return null;
        }
    }

    public String getRole() {
        List<String> list = mapUserDetails.get(USER_ROLE_KEY);
        if (AppUtil.isNotNull(list) && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void setMapUserDetails(HttpServletRequest request) {
        for (String key : mapKey.keySet()) {
            List<String> listValueMap = new ArrayList<>();
            String value = request.getHeader(key);
            if (value != null) {
                listValueMap.add(value);
                mapUserDetails.put(key, listValueMap);
            }
        }
    }

}
