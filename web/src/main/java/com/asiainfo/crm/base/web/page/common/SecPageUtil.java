package com.asiainfo.crm.base.web.page.common;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.bits.web.framework.Page;
import com.asiainfo.bits.web.framework.util.PageUtil;

/**
 * describe:
 *
 * @author xiecx2
 * @date 2019/03/06
 */
public class SecPageUtil extends PageUtil {
    private Page page = null;

    public SecPageUtil(Page page) {
        super(page);
    }

    public IDataset getStaticList(String type_id) {
        IDataset result = new DatasetList();
        if ("SEC_FUNC_PARENT_TYPE".equals(type_id)) {
            IData tempData = new DataMap();
            tempData.put("DATA_ID", "1");
            tempData.put("DATA_NAME", "员工管理");
            result.add(tempData);
            tempData.put("DATA_ID", "2");
            tempData.put("DATA_NAME", "员工授权");
            result.add(tempData);
        }
        return result;
    }
}
