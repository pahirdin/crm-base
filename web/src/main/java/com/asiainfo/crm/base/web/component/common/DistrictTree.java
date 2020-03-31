package com.asiainfo.crm.base.web.component.common;

import com.ailk.web.BaseComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.Tapestry;

public abstract class DistrictTree extends BaseComponent {

    public abstract String getName();

    public abstract String getTextName();

    public abstract void setTextName(String textName);

    public abstract String getText();

    public abstract String getValue();

    public abstract String getBeforeAction();

    public abstract String getPopupPage();

    public abstract void setPopupPage(String popupPage);

    public abstract String getTitle();

    public abstract void setTitle(String title);

    public abstract String getDesc();

    public abstract String getParams();

    public abstract void setParams(String params);

    public abstract String getRootDistrict();

    public abstract String getAfterAction();

    public abstract String getPrivCode();

    public abstract String getRegionId();

    public abstract String getIsSelRoot();

    public abstract String getOperateType();

    public abstract String getPopMode();

    public abstract String getReadOnly();

    public abstract String getNullable();

    public abstract String getListener();

    public abstract void setListener(String listener);

    /**
     * 作用：初始化参数
     *
     */
    private void setInitValue() {
        StringBuilder bf = new StringBuilder();
        String popupPage = "popup.DistrictTree";
        String title = "地域树";
        String listener = "init";
        String name = getName();
        String textName = getTextName();
        String privCode = getPrivCode();
        String regionId = getRegionId();
        String params = null;
        String popMode = getPopMode();
        if (textName == null) {
            textName = "POP_" + name;
            setTextName(textName);
        }

        String rootDistrict = getRootDistrict();

        popMode = StringUtils.isNotBlank(popMode) ? popMode.toUpperCase() : "";
        String afterAction = getAfterAction();
        String isSelRoot = getIsSelRoot();

        if (StringUtils.isNotBlank(name)) bf.append("&VALUECODE=").append(name);
        if (StringUtils.isNotBlank(getTextName())) bf.append("&VALUENAME=").append(getTextName());
        if (StringUtils.isNotBlank(popMode)) bf.append("&POP_MODE=").append(popMode);
        if (StringUtils.isNotBlank(rootDistrict)) bf.append("&ROOT_DISTRICT=").append(rootDistrict);
        if (StringUtils.isNotBlank(afterAction)) bf.append("&AFTER_ACTION=").append(afterAction);
        if (StringUtils.isNotBlank(privCode)) bf.append("&PRIV_CODE=").append(privCode);
        if (StringUtils.isNotBlank(isSelRoot)) bf.append("&IS_SEL_ROOT=").append(isSelRoot);
        if (StringUtils.isNotBlank(regionId)) bf.append("&REGION_ID=").append(regionId);

        params = bf.toString();
        setPopupPage(popupPage);
        setTitle(title);
        setListener(listener);
        setParams(params);
    }

    /**
     * 作用：组织树
     */
    public void renderComponent(IMarkupWriter writer, IRequestCycle cycle) {
        try {
            super.renderComponent(writer, cycle);
            setInitValue();
            String name = getName();
            String textName = getTextName();
            String text = getText();
            String value = getValue();

            if (name == null)
                throw new ApplicationRuntimeException(Tapestry.format("invalid-field-name", "Popup"), this, null, null);

            writer.begin("span");
            writer.attributeRaw("class", "e_mix");

            writer.begin("input");
            writer.attributeRaw("type", "text");
            writer.attributeRaw("id", textName);
            writer.attributeRaw("name", textName);
            writer.attributeRaw("readOnly", "true");
            writer.attributeRaw("nullable", getNullable());
            writer.attributeRaw("desc", getDesc());

            writer.attributeRaw("fieldName", name);
            writer.attributeRaw("value", text != null ? text : "");
            writer.end("input");

            writer.begin("input");
            writer.attributeRaw("type", "hidden");
            writer.attributeRaw("id", name);
            writer.attributeRaw("name", name);
            writer.attributeRaw("textName", name);
            writer.attributeRaw("readOnly", "true");
            writer.attributeRaw("nullable", getNullable());
            writer.attributeRaw("desc", getDesc());
            writer.attributeRaw("value", value != null ? value : "");
            writer.end("input");

            writer.begin("span");
            writer.attributeRaw("class", "e_ico-check");
            writer.attributeRaw("id", "POP_BTN_" + name);
            writer.attributeRaw("name", "POP_BTN_" + name);

            writer.end("span");
            writer.end("span");
        } catch (Exception e) {

        }
    }


}
