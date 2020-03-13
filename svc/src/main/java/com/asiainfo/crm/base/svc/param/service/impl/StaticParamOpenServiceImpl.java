package com.asiainfo.crm.base.svc.param.service.impl;

import com.asiainfo.bits.core.data.param.StaticParamDTO;
import com.asiainfo.crm.base.api.param.service.IStaticParamOpenService;
import com.asiainfo.crm.base.svc.cell.param.service.IStaticParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 参数翻译服务
 * <p>
 * liaosheng@asiainfo.com
 * 2020/3/11 3:40 下午
 * @author liaosheng
 */
@Service
@Slf4j
public class StaticParamOpenServiceImpl implements IStaticParamOpenService {

    @Autowired
    private IStaticParamService service;

    @Override
    public List<StaticParamDTO> queryParams(StaticParamDTO request) {
        log.debug("request {}", request);
        return service.queryParams(request);
    }
}
