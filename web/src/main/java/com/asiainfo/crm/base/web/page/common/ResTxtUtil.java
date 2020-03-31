package com.asiainfo.crm.base.web.page.common;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.bits.web.framework.Visit;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;

public class ResTxtUtil{
    private static int c = 0x186a0;

    public static String createErrorDataTxtURL(IDataset error,  Visit visit,
                                               String ftpSite, String excelName,String businessType) throws Exception{
        String fileName = "errorFile[" + businessType + "].txt";
        List<?> excelCfgData = ExcelConfig.getSheets(excelName);
        File errorFile = writeTxtData(error, "/", null, excelCfgData);
        //将导入失败的文件上传
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(visit);
        String errorFileId = ImpExpUtil.getImpExpManager().getFileAction().upload(new FileInputStream(errorFile), ftpSite, "upload/import", fileName);
        String errorUrl = ImpExpUtil.getDownloadPath(errorFileId, fileName);
        errorFile.delete();
        return errorUrl;
    }
    public static File writeTxtData(IDataset dataset, String filePath,
                                    String excelModel, List<?> excelCfgData) throws Exception {
        BufferedWriter bw = null;
        File f = null;
        FileWriter fileWriter = null;
        try {
            if (c > 0xf3e58)
                c = 0x186a0;
            c++;
            f = new File((new StringBuilder()).append("WADE_TXT_EXPORT")
                    .append("_")
                    .append(String.valueOf(System.currentTimeMillis()))
                    .append(String.valueOf(c)).toString());
            fileWriter = new FileWriter(f);
            bw = new BufferedWriter(fileWriter);
            String[] cellNames = getCellName(excelCfgData);
            int col = cellNames.length;
            int line = dataset.size();//行
                for (int i = 0; i < line; i++) {
                    IData errorData = dataset.getData(i);//
                    StringBuffer s = new StringBuffer();
                    s.append(formatInfo(errorData.getString(cellNames[0])));
                    for (int j = 1; j < col; j++) {
                        s.append("   " + errorData.getString(cellNames[j]));
                    }
                    bw.write(s.toString());
                    bw.newLine();
                    bw.flush();
            }
        } catch (Exception e) {
            if(fileWriter != null){
                fileWriter.close();
            }

            if (bw != null){
                bw.close();
            }
        }finally{
            if(fileWriter != null){
                fileWriter.close();
            }
            if (bw != null){
                bw.close();
            }
        }

        return f;
    }

    @SuppressWarnings("rawtypes")
    private static String[] getCellName(List<?> excelCfgData) {

        Element sheet = (Element) excelCfgData.get(0);
        Element header = sheet.element("header");
        List cells = header.elements();
        String[] cellNames = new String[cells.size()];
        for (int i = 0; i < cells.size(); i++) {
            Element cell = (Element) cells.get(i);
            cellNames[i] = cell.attributeValue("name");
        }
        return cellNames;
    }
    private static String formatInfo(String s) {

        if(StringUtils.isEmpty(s)){
            return "  ";
        }
        return s;
    }

}