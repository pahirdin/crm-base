package com.asiainfo.crm.base.web.component.common;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.web.BaseComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

public abstract class ImportList extends BaseComponent {

    public abstract String getImportNameStr();

    public abstract String getImportCodeStr();

    public abstract String getPathNameStr();

    public abstract String getConfigPathStr();

    public abstract void setImportList(IDataset importList);

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle)
            throws Exception {
        super.renderComponent(informalParametersBuilder, writer, cycle);
        includeResourceFile(writer, cycle);
        String[] importNameArray = getImportNameStr().split(",");
        String[] importCodeArray = getImportCodeStr().split(",");
        String[] pathNameArray = getPathNameStr().split(",");
        String[] configPathArray = getConfigPathStr().split(",");
        IDataset importList = new DatasetList();
        for (int i = 0; isaBoolean(i, importNameArray, importCodeArray, pathNameArray, configPathArray); i++) {
            IData importInfo = new DataMap();
            importInfo.put("DATA_NAME", importNameArray[i]);
            importInfo.put("DATA_CODE", importCodeArray[i]);
            importInfo.put("TEMPLATE_NAME", pathNameArray[i]);
            importInfo.put("CONFIG_PATH", configPathArray[i]);
            importList.add(importInfo);
        }
        setImportList(importList);
    }

    private boolean isaBoolean(int i, String[]... array) {
        for (String[] strArray : array) {
            if (i < strArray.length) {
                return true;
            }
        }
        return false;
    }

    /**
     * 加载js
     */
    private void includeResourceFile(IMarkupWriter writer, IRequestCycle cycle) {
        boolean isAjaxRequest = isAjaxServiceReuqest(cycle);
        if (isAjaxRequest) {
            includeScript(writer, "scripts/sec/popup/importList.js");
        } else {
            addResourceFile(writer, "scripts/sec/popup/importList.js");
        }
    }
}
