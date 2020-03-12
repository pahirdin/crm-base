package com.asiainfo.crm.base.svc.param.cell.service.impl;

import com.asiainfo.bits.core.util.ArrayUtils;
import com.asiainfo.bits.skeleton.database.DataSourceKey;
import com.asiainfo.bits.skeleton.database.annotation.DataSource;
import com.asiainfo.crm.base.api.param.dto.StaticParamDTO;
import com.asiainfo.crm.base.svc.param.cell.entity.po.StaticParam;
import com.asiainfo.crm.base.svc.param.cell.mapper.StaticParamMapper;
import com.asiainfo.crm.base.svc.param.cell.service.IStaticParamService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 静态参数
 *
 * @author liaosheng
 * @version 1.0.0
 * @date 2020-02-25 18:31:09
 */
@Service
@DataSource(DataSourceKey.BASE)
public class StaticParamServiceImpl extends ServiceImpl<StaticParamMapper, StaticParam> implements IStaticParamService {

    @Autowired
    private StaticParamMapper staticParamMapper;


    @Override
    public List<StaticParamDTO> queryParams(StaticParamDTO staticDataDTO) {
        return ArrayUtils.copyPropertiesList(list(Wrappers.<StaticParam>lambdaQuery()
                .eq(StaticParam::getTypeId, staticDataDTO.getTypeId())
                .eq(StaticParam::getValidFlag, "1")
                .eq(StringUtils.isNotBlank(staticDataDTO.getDataId()), StaticParam::getDataId, staticDataDTO.getDataId())
                .eq(StringUtils.isNotBlank(staticDataDTO.getPDataId()), StaticParam::getPDataId, staticDataDTO.getPDataId())
                .eq(StringUtils.isNotBlank(staticDataDTO.getSubsysCode()), StaticParam::getSubsysCode, staticDataDTO.getSubsysCode())
                .eq(StringUtils.isNotBlank(staticDataDTO.getEparchyCode()), StaticParam::getEparchyCode, staticDataDTO.getEparchyCode()))
                , StaticParamDTO.class);
    }
}