package utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import common.Logger;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class MergeTwoDbRec {
	
	
	static Logger logger = Logger.getLogger(MergeTwoDbRec.class);

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
/*		HashMap hashMapBef = readTxt("D:\\db2Record\\0809\\08.txt");
		HashMap hashMapAft = readTxt("D:\\db2Record\\0809\\09.txt");
//		HashMap<String,Double> outMap = outPutFile(hashMapBef,hashMapAft);
		HashMap<String,Integer> outMap = outPutFile(hashMapBef,hashMapAft);
//		HashMap<String,Integer> outMap = readTxtInt("D:\\db2Record\\0708\\0708.txt");
		//计算系统成功率
		Double sumSuc =0.0;
		Double sumAll = 0.0;
		List<String> list = null;
		try {
			list = (List<String>) getSystemError();
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String key:outMap.keySet()){
			if(list.indexOf(key)==-1){
				sumSuc+=outMap.get(key);
			}else{
				System.out.println("sysErr:"+key+" num:"+outMap.get(key));
			}
			sumAll+=outMap.get(key);
		}
		NumberFormat num = NumberFormat.getPercentInstance(); 
		num.setMaximumFractionDigits(2); //小数点后面最多显示几位
		System.out.println(num.format(sumSuc/sumAll));
		System.out.printf("成功的交易共：%15f 即"+sumSuc+"笔", sumSuc);
		System.out.println();
		System.out.printf("总共的交易共：%15f 即"+sumAll+"笔", sumAll);
		System.out.println();*/
	}
	
	/**
	 * 
	 * @param pathBef 上半月数据库统计响应码原始数据路径
	 * @param pathAft 当月数据库统计响应码原始数据路径	
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static void loadAndCalc(String pathBef, String pathAft) throws FileNotFoundException{
		HashMap<String,String> hashMapBef = readTxt(pathBef);
		HashMap<String,String> hashMapAft = readTxt(pathAft);
		HashMap<String,Integer> outMap = outPutFile(hashMapBef,hashMapAft);
		Double sumSuc =0.0;
		Double sumAll = 0.0;
		List<String> list = null;
		try {
			list = (List<String>) getSystemError();
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String key:outMap.keySet()){
			if(list.indexOf(key)==-1){
				sumSuc+=outMap.get(key);
			}else{
//				System.out.println("sysErr:"+key+" num:"+outMap.get(key));
				logger.debug("sysErr:"+key+" num:"+outMap.get(key));
			}
			sumAll+=outMap.get(key);
		}
		//打印系统成功率（百分数保留两位小数）
		NumberFormat num = NumberFormat.getPercentInstance(); 
		num.setMaximumFractionDigits(2); //小数点后面最多显示几位
//		System.out.println(num.format(sumSuc/sumAll));
		logger.info("系统成功率"+num.format(sumSuc/sumAll));
		
//		System.out.printf("成功的交易共：%15f 即"+sumSuc+"笔", sumSuc);
//		System.out.println();
//		System.out.printf("总共的交易共：%15f 即"+sumAll+"笔", sumAll);
//		System.out.println();
		logger.info("成功的交易共："+sumSuc+"笔");
		logger.info("总共的交易共："+sumAll+"笔");
	}
	
	/**
	 * 读取数据库的统计数据文本文档
	 * @param path
	 * @return 当月的返回码数据（key=响应码，value=数量）
	 * @throws FileNotFoundException
	 * @author <a href="mailto:chenjie2@unionpay.com">ChenJie</a>
	 */
	public static HashMap<String, String> readTxt(String path) throws FileNotFoundException{
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
	
	public static HashMap<String, Integer> readTxtInt(String path) throws FileNotFoundException{
		File fileRead = new File(path);
		Scanner scanner = new Scanner(fileRead,"utf-8");
		LinkedHashMap<String, Integer> hashMap = new LinkedHashMap<String, Integer>();
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
			hashMap.put(header[0], Integer.parseInt(header[1]));
		}
		return hashMap;
	}
	
	
	/**
	 * 合并上月21日至月底与当月1日至20日的响应码数据
	 * @param hashMapBef
	 * @param hashMapAft
	 * @return 输出map的同时会产生合并的txt（名字格式为“月份_output.txt”）
	 */
	public static HashMap<String,Integer> outPutFile(HashMap<String,String> hashMapBef, HashMap<String,String> hashMapAft){
//		File outFile = new File(System.currentTimeMillis()+"_output.txt");
		newFolder("outputTemp");
		File outFile = new File("outputTemp\\"+Calendar.getInstance().get(Calendar.YEAR)+"-"+(Calendar.getInstance().get(Calendar.MONDAY)+1)+"_output.txt");
		FileWriter fw = null;
//		HashMap<String, Double> mergedMap = new HashMap<String, Double>();
		HashMap<String, Integer> mergedMap = new HashMap<String, Integer>();
		try {
			fw = new FileWriter(outFile);
			for(String key:hashMapBef.keySet()){
				if(hashMapAft.containsKey(key)){
//					Double value = Double.parseDouble(hashMapBef.get(key))+Double.parseDouble(hashMapAft.get(key));
					Integer value = Integer.parseInt(hashMapBef.get(key))+Integer.parseInt(hashMapAft.get(key));
//					System.out.println(key+"   "+value);
					fw.append(key+"   "+value);
					fw.append("\n");
					mergedMap.put(key, value);
					hashMapAft.remove(key);
				}
//				hashMapBef.remove(key);
			}
			if(hashMapAft.size()>0){
				for(String key:hashMapAft.keySet()){
//					System.out.println(key+"   "+hashMapAft.get(key));
					fw.append(key+"   "+hashMapAft.get(key));
//					mergedMap.put(key, Double.parseDouble(hashMapAft.get(key)));
					mergedMap.put(key, Integer.parseInt(hashMapAft.get(key)));
					fw.append("\n");
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				fw.flush();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return mergedMap;
	}
	
	
	/**
	 * 需求组定义的系统错误列表
	 * @return
	 * @throws IOException
	 * @throws BiffException
	 * 
	 * @author <a href="mailto:chenjie2@unionpay.com">ChenJie</a>
	 */
	public static List<?> getSystemError() throws IOException, BiffException{
		//打开文件
		Workbook book=Workbook.getWorkbook(new File("file\\EBPP_Res_SysErr.xls")); 
//		Workbook book=Workbook.getWorkbook(new File("EBPP_Res_SysErr.xls")); 
        // 3. 获取所有workSheets
        Sheet[] allSheet = book.getSheets();
        // 4.遍历组织工作薄
        Sheet orgSheet = allSheet[0];
        
        LinkedList<String> list = new LinkedList<String>();
        for(int i=1;i<orgSheet.getRows();i++){
        	String key = orgSheet.getCell(0, i).getContents();
        	if(orgSheet.getCell(2, i).getContents().equalsIgnoreCase("Y"))
        		list.add(key);
        }
        return list;
	}
	
	//新建一个文件夹 
	public static void newFolder(String folderPath) { 
	    try { 
	      String filePath = folderPath; 
	      File myFilePath = new File(filePath); 
	      if (!myFilePath.exists()) { 
	        myFilePath.mkdir(); 
	      } 
	    } catch (Exception e) { 
	      System.out.println("新建文件夹操作出错"); 
	      e.printStackTrace(); 
	    } 
	  } 

}
