package com.asiainfo.crm.base.svc.param.cell.mapper;

import com.asiainfo.bits.skeleton.database.DataSourceKey;
import com.asiainfo.bits.skeleton.database.annotation.DataSource;
import com.asiainfo.bits.skeleton.database.annotation.Storage;
import com.asiainfo.crm.base.svc.param.cell.entity.po.StaticParam;
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