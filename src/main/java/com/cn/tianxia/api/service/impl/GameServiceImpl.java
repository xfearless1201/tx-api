/****************************************************************** 
 *
 *    Powered By tianxia-online. 
 *
 *    Copyright (c) 2018-2020 Digital Telemedia 天下网络 
 *    http://www.d-telemedia.com/
 *
 *    Package:     com.cn.tianxia.service.impl 
 *
 *    Filename:    GameServiceImpl.java 
 *
 *    Description: TODO(用一句话描述该文件做什么) 
 *
 *    Copyright:   Copyright (c) 2018-2020 
 *
 *    Company:     天下网络科技 
 *
 *    @author: Wilson
 *
 *    @version: 1.0.0
 *
 *    Create at:   2018年10月26日 10:09 
 *
 *    Revision: 
 *
 *    2018/10/26 10:09 
 *        - first revision 
 *
 *****************************************************************/
package com.cn.tianxia.api.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.cn.tianxia.api.base.annotation.DataSource;
import com.cn.tianxia.api.base.datashource.Database;
import org.springframework.stereotype.Service;

import com.cn.tianxia.api.domain.txdata.GameListDao;
import com.cn.tianxia.api.service.IGameService;

/**
 * @ClassName GameServiceImpl
 * @Description TODO(这里用一句话描述这个类的作用)
 * @Author Wilson
 * @Date 2018年10月26日 10:09
 * @Version 1.0.0
 **/
@Service("gameService")
public class GameServiceImpl implements IGameService {

    @Resource
    private GameListDao gameListDao;

    @Override
    @DataSource(Database.TXDATA_DB_SLAVE)
    public List<Map<String, String>> selectByGameType(Map<String,Object> params) {
        List<Map<String, String>> counts = gameListDao.selectByGameTypeCount(params);
        List<Map<String, String>> lists = gameListDao.selectByGameType(params);
        lists.add(0,counts.get(0));
        return lists;
    }

}