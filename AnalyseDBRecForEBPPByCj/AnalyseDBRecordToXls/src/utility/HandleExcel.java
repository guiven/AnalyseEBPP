package utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class HandleExcel {
	private HashMap<String, String> hashMapBef;
	private HashMap<String, String> hashMapAft;
	private Properties properties;
	
	/**
	 * load进内存上月和本月统计数据以及properties文件
	 * @param pathBef
	 * @param pathAft
	 */
	public void loadPath(String pathBef, String pathAft){
		properties = new Properties();
		try {
			hashMapBef = readTxt(pathBef);
			hashMapAft = readTxt(pathAft);
//			properties.load(new FileReader("CIAMessage_zh_CN.properties"));
			properties.load(new FileReader("file\\CIAMessage_zh_CN.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成Excel处理主方法
	 * 
	 * @author <a href="mailto:chenjie2@unionpay.com">ChenJie</a>
	 */
	public void process(){
//		loadPath("D:\\db2Record\\0708\\0708.txt","D:\\db2Record\\0809\\0809.txt");
//		loadPath("D:\\db2Record\\0708\\0708.txt",Calendar.getInstance().get(Calendar.MONDAY)+1+"_output.txt");
		//参数path2的括号不能丢 因为第一个“+”表示int相加，第二个“+”表示字符串连接
		loadPath("outputTemp\\"+Calendar.getInstance().get(Calendar.YEAR)+"-"+Calendar.getInstance().get(Calendar.MONDAY)+"_output.txt",
				"outputTemp\\"+Calendar.getInstance().get(Calendar.YEAR)+"-"+(Calendar.getInstance().get(Calendar.MONDAY)+1)+"_output.txt");
		/**
		 * 构建Excel
		 */
		try {
			List<?> list = MergeTwoDbRec.getSystemError();
			// 打开文件
			WritableWorkbook book = Workbook
					.createWorkbook(new File("bool.xls")); // 生成的bool名称。
			// 生成名为“第一页”的工作表，参数0表示这是第一页
			WritableSheet sheet = book.createSheet("应答码统计", 0); // 生成的sheet名称

			//加粗格式（用于表示标题）
			jxl.write.WritableFont wf = new jxl.write.WritableFont(
					WritableFont.TIMES, 11, WritableFont.BOLD, false,
					jxl.format.UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.DEFAULT_BACKGROUND);
			jxl.write.WritableCellFormat wcfF = new jxl.write.WritableCellFormat(
					wf);
			// 在Label对象的构造子中指名单元格位置是第一列第一行(0,0)
			Label label1 = new Label(0, 0, "响应码", wcfF);
			Label label2 = new Label(1, 0, "条数", wcfF);
			Label label3 = new Label(2, 0, "响应信息", wcfF);
			Label label4 = new Label(3, 0, "上月错误条数", wcfF);
			Label label5 = new Label(4, 0, "与上月错误条数环比值", wcfF);

			// 将定义好的单元格添加到工作表中
			sheet.addCell(label1);
			sheet.addCell(label2);
			sheet.addCell(label3);
			sheet.addCell(label4);
			sheet.addCell(label5);

			int row = 1;
//			double sumBef = 0;
//			double sumAft = 0;
			NumberFormat num = NumberFormat.getPercentInstance();
			num.setMaximumFractionDigits(2); // 小数点后面最多显示几位

			//标红格式(系统错误)
			jxl.write.WritableFont wfc = new jxl.write.WritableFont(
					WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false,
					jxl.format.UnderlineStyle.NO_UNDERLINE,
					jxl.format.Colour.RED);
			jxl.write.WritableCellFormat wcfFC = new jxl.write.WritableCellFormat(
					wfc);
			for (String key : hashMapAft.keySet()) {
				//响应码
				Label labelResp = new Label(0, row, key);
				//数量
				Label labelNum = new Label(1, row, hashMapAft.get(key));
//				sumAft += (Double.parseDouble(hashMapAft.get(key)));
				Label labelInfo = new Label(2, row, properties.getProperty(key));
				Label LabelVar1;
				Label LabelVar2;
				if (hashMapBef.containsKey(key)) {
					double varience = Double.parseDouble(hashMapAft.get(key))
							/ Double.parseDouble(hashMapBef.get(key));
//					if (varience >= 2) {
					if(list.contains(key)){
						labelResp = new Label(0, row, key, wcfFC);
						labelNum = new Label(1, row, hashMapAft.get(key), wcfFC);
						labelInfo = new Label(2, row,
								properties.getProperty(key), wcfFC);
						LabelVar1 = new Label(3, row, hashMapBef.get(key), wcfFC);
						LabelVar2 = new Label(4, row, " [" + num.format(varience) + "]", wcfFC);
					} else{
						LabelVar1 = new Label(3, row, hashMapBef.get(key));
						LabelVar2 = new Label(4, row,  " ["	+ num.format(varience) + "]");
					}
//					sumBef += (Double.parseDouble(hashMapBef.get(key)));
				} else{
					LabelVar1 = new Label(3, row, "当月新返回码");
					LabelVar2 = new Label(4, row, "");
				}
				sheet.addCell(labelResp);
				sheet.addCell(labelNum);
				sheet.addCell(labelInfo);
				sheet.addCell(LabelVar1);
				sheet.addCell(LabelVar2);
				row++;
			}
			// jxl.write.Number number = new jxl.write.Number(0,1,789.123);
			// sheet.addCell(number);
			// 写入数据并关闭文件
			book.write();
			book.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/**
	 * 读取数据库的统计数据文本文档
	 * @param path
	 * @return 当月的返回码数据（key=响应码，value=数量）
	 * @throws FileNotFoundException
	 * @author <a href="mailto:chenjie2@unionpay.com">ChenJie</a>
	 */
	public HashMap<String, String> readTxt(String path) throws FileNotFoundException{
		File fileRead = new File(path);
		Scanner scanner = new Scanner(fileRead,"utf-8");
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
		while(scanner.hasNext()){
			String tmp = scanner.nextLine();
			if(tmp.trim()==null||tmp.trim().equalsIgnoreCase(""))
				continue;
			String[] header = tmp.split("[\\s]+");//按空格分隔
			if(header.length>2)
				break;
			if(header[0].equalsIgnoreCase("RSP_CD")){
				scanner.nextLine();
				continue;
			}
			hashMap.put(header[0], header[1]);
		}
		return hashMap;
	}
}
