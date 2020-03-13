package com.asiainfo.crm.base.svc.cell.param.entity.po;

import com.asiainfo.bits.core.data.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 静态参数表
 *
 * @author liaosheng
 * @version 1.0.0
 * @date 2020-02-27 14:08:59
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("TD_S_STATIC")
public class StaticParam extends BaseEntity {

    private static final long serialVersionUID = 1L;
    
    /** 参数类型 */
    @TableId(value = "TYPE_ID", type = IdType.INPUT)
    private String typeId;

    /** 数据ID */
    @TableId(value = "DATA_ID", type = IdType.INPUT)
    private String dataId;

    /** 数据名称 */
    @TableField(value = "DATA_NAME")
    private String dataName;

    /** 父数据ID */
    @TableField(value = "PDATA_ID")
    private String pDataId;

    /** 子系统编码 */
    @TableField(value = "SUBSYS_CODE")
    private String subsysCode;

    /** 地州编码 */
    @TableField(value = "EPARCHY_CODE")
    private String eparchyCode;

    /** 有效标识 */
    @TableField(value = "VALID_FLAG")
    private String validFlag;

}