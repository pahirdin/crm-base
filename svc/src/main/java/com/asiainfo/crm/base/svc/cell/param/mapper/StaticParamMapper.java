package com.asiainfo.crm.base.svc.cell.param.mapper;

import com.asiainfo.bits.skeleton.database.DataSourceKey;
import com.asiainfo.bits.skeleton.database.annotation.DataSource;
import com.asiainfo.bits.skeleton.database.annotation.Storage;
import com.asiainfo.crm.base.svc.cell.param.entity.po.StaticParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 静态参数表
 *
 * @author liaosheng
 * @version 1.0.0
 * @date 2020-02-27 14:09:06
 */
@Storage
@DataSource(DataSourceKey.BASE)
public interface StaticParamMapper extends BaseMapper<StaticParam> {

}