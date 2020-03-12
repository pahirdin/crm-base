package com.asiainfo.crm.sec.web.component.common;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.web.BaseComponent;
import com.ailk.web.util.BaseUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.*;

public abstract class CommonPopup extends BaseComponent {
    public abstract String getParams(); 
    public abstract void setParams(String params);

    /**
     * @param cycle
     */
    protected void cleanupAfterRender(IRequestCycle cycle) {
        super.cleanupAfterRender(cycle);
    }

    /**
     * @return
     */
    private IData getParamByAttr() {
        IData source = new DataMap();
        String title = this.getTitle();

        if (title == null || "".equals(title.trim())) {
            setTitle("通用选择窗口");
        } else {
            setTitle(title);
        }

        this.setSource(source);
        return source;
    }

    public abstract String getResTypeCode();

    public abstract String getRootStockId();


    private void renderPopupClick(IMarkupWriter writer, String parameters, String name, String cond) {
        StringBuilder clickStr = new StringBuilder();
        String beforeAction = getBeforeAction();
        if (StringUtils.isNotBlank(beforeAction)) {
            if (beforeAction.contains("()")) {
                beforeAction = beforeAction + "()";
            }
            clickStr.append("if(" + beforeAction + "==false){return;};");
        }
        String popupPage = getPopupPage();
        String title = getTitle();
        String listener = getListener();
        String inParams = getParams();

        String params = BaseUtil.getParameters(getSource(), getColumns()) + "&VALUECODE=" + getName() + "&VALUENAME=POP_" + getName() + "&AFTER_ACTION=" + getAfterAction();
/*        clickStr.append("popupPage('" + title + "'");
        clickStr.append(",'" + popupPage + "'");
        clickStr.append(",'" + listener + "'");
        clickStr.append(",'" + params + inParams + "'");
        clickStr.append(",null");
        clickStr.append(",'1000px','520px',true");
        clickStr.append(")");*/
     clickStr.append("popupPage('" + title + "'");
        clickStr.append(",'" + popupPage + "'");
        clickStr.append(",'" + listener + "'");
        clickStr.append(",'" + params + inParams + "'");
        clickStr.append(",null");
        clickStr.append(", 'c_popup c_popup-half c_popup-half-hasBg'");
        clickStr.append(")");

        writer.attributeRaw("ontap", clickStr.toString());
    }


    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle) {
        getParamByAttr();
        String popupPage = "";
        String listener = "init";
        if (StringUtils.isEmpty(getPopupPage())) {
            this.setPopupPage(popupPage);
        }
        if (StringUtils.isEmpty(getListener())) {
            this.setListener(listener);
        }
        String name = getName();
        if (name == null) {
            throw new ApplicationRuntimeException(Tapestry.format("invalid-field-name", "Popup"), this, null, null);
        }

        String textName = getTextName();
        if (textName == null) {
            textName = "POP_" + name;
        }
        String value = getValue();
        String text = getText();
        String parameters = "&NAME=" + getName();
        if (getSource() != null) {
            parameters = parameters + BaseUtil.getParameters(getSource(), getColumns());
        }

        if (isShowButton()) {
            writer.begin("span");
            writer.attributeRaw("class", "e_group");

        }
        if (isShowButton()) {
            writer.begin("span");
            writer.attributeRaw("class", "e_groupRight");
            writer.begin("span");
            writer.attributeRaw("class", "e_ico-check" + (isDisabled() ? " e_dis" : ""));
            writer.attributeRaw("id", "POP_BTN_" + name);
            if (isDisabled()) {
                writer.attributeRaw("disabled", "true");
            }
            writer.attributeRaw("type", "button");
            renderPopupClick(writer, parameters, name, getCond());
            renderInformalParameters(writer, cycle, new String[]{"id", "textName", "name", "nullable", "type"});
            writer.begin("i");
            writer.attributeRaw("class", "e_button-right");
            writer.end();
            writer.begin("span");
            writer.end();
            writer.end();
            writer.end();
        }
        String id = null;
        IBinding idBinding = getBinding("id");
        if ((idBinding != null) && (!Tapestry.isBlank(idBinding.getString()))) {
            id = idBinding.getString();
        }
        writer.begin("span");
        writer.attributeRaw("class", "e_groupMain");
        writer.begin("input");
        writer.attributeRaw("type", "text");
        writer.attributeRaw("id", id != null ? "POP_" + id : textName);
        writer.attributeRaw("name", textName);
        writer.attributeRaw("fieldName", name);
        writer.attributeRaw("value", text == null ? "" : text);

        renderInformalParameters(writer, cycle);
        if (isDisabled()) {
            writer.attributeRaw("disabled", "true");
        }
        if (isReadOnly()) {
            writer.attributeRaw("readOnly", "readOnly");
        }

        if ((!isReadOnly()) && (getEnterAction() != null)) {
            writer.attributeRaw("onkeypress", "var e=$.event.fix(event);if (e.which == 13) {e.stop();return " + getEnterAction() + ";}");
        }
        writer.end("input");

        writer.begin("input");
        writer.attributeRaw("type", "hidden");
        writer.attributeRaw("id", id != null ? id : name);
        writer.attributeRaw("name", name);
        writer.attributeRaw("textName", id != null ? "POP_" + id : textName);
        writer.attributeRaw("value", value == null ? "" : value);
        writer.attributeRaw("afterAction", getAfterAction());
        writer.attributeRaw("parameters", parameters);
        if (getSubsys() != null) {
            writer.attributeRaw("subsys", getSubsys());
        }
        writer.end("input");
        writer.end();
        writer.end();
    }

    public abstract String getName();

    public abstract String getText();

    public abstract String getValue();

    public abstract String getTextName();

    public abstract Object getSource();

    public abstract Object getColumns();

    public abstract String getCond();

    public abstract String getAfterAction();

    public abstract boolean isDisabled();

    public abstract boolean isReadOnly();

    public abstract String getSubsys();

    public abstract String getEnterAction();

    public abstract String getBeforeAction();

    public abstract boolean isShowButton();

    public abstract String getPopupPage();

    public abstract String getListener();

    public abstract String getTitle();

    public abstract String getWidth();

    public abstract String getHeight();

    public abstract String getFrameName();

    public abstract boolean isShowFlush();

    public abstract boolean isShowClose();

    public abstract boolean isShowMinMax();

    public abstract String getShowType();

    public abstract void setSource(Object o);

    public abstract void setPopupPage(String page);

    public abstract void setListener(String listener);

    public abstract void setDisabled(boolean disabled);

    public abstract void setTitle(String title);

    public abstract void setAfterAction(String afterAction);

    private String text;

    public abstract String getDepartId();

    public abstract boolean isBlank();

}
