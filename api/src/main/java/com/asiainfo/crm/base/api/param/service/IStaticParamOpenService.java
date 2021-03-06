package com.asiainfo.crm.base.api.param.service;


import com.asiainfo.bits.core.data.param.StaticParamDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 由业务定制的静态参数翻译服务
 * liaosheng@asiainfo.com
 * 2020/2/29 12:57 下午
 * @author liaosheng
 */
@Service
public interface IStaticParamOpenService {

    /**
     * 查询静态参数数据
     * @param request
     * @return
     */
    List<StaticParamDTO> queryParams(StaticParamDTO request);
}
