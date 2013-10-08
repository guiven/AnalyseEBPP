package utility;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class computeSysRate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
	public List<?> getSystemError() throws IOException, BiffException{
		//打开文件
		Workbook book=Workbook.getWorkbook(new File("bool.xls")); //生成的bool名称。
        // 3. 获取所有workSheets
        Sheet[] allSheet = book.getSheets();
        // 4.遍历组织工作薄
        Sheet orgSheet = allSheet[0];
        
        LinkedList<String> list = new LinkedList<String>();
        for(int i=1;i<orgSheet.getRows();i++){
        	String key = orgSheet.getCell(0, i).getContents();
        	if(orgSheet.getCell(3, i).getContents().equalsIgnoreCase("Y"))
        		list.add(key);
        }
        return list;
	}

}
