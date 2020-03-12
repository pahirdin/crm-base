package com.asiainfo.crm.base.api.param.dto;

import com.asiainfo.bits.core.data.AbstractDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 静态参数传输对象
 *
 * @date 2020/3/12 2:25 下午
 * @author liaosheng
 */

@Data
@EqualsAndHashCode(callSuper=false)
public class StaticParamDTO extends AbstractDTO {

    private String typeId;
    private String dataId;
    private String pDataId;
    private String dataName;
    private String subsysCode;
    private String eparchyCode;

    @JsonProperty(defaultValue = "1")
    private String validData;

}
