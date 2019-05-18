package com.cn.tianxia.api.game;

import com.cn.tianxia.api.utils.SpringContextUtils;

/**
 * @Auther: zed
 * @Date: 2019/3/8 10:37
 * @Description: 游戏接口代理工厂类
 */
public class GameProxyFactory {

    public static GameInterfaceService productGameService(String type) throws Exception{
        return (GameInterfaceService) SpringContextUtils.getBeanById(type);
    }

}
