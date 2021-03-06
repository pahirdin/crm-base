package com.asiainfo.crm.base.svc.cell.param.service;

import com.asiainfo.bits.core.data.param.StaticParamDTO;

import java.util.List;

/**
 * @program: crm
 * @description: 用户订单相关业务服务
 * @author: jinnian
 * @create: 2020-01-20 14:38
 **/
public interface IStaticParamService {

    List<StaticParamDTO> queryParams(StaticParamDTO request);
}
