package utility;

import java.io.FileNotFoundException;

import common.Logger;


public class ReadMain {
	
	static Logger logger = Logger.getLogger(ReadMain.class);

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		String path1;
		String path2;
		if(args.length==2){
			path1 = args[0];
			path2 = args[1];
			logger.info("使用自定义的月统计文件对比路径："+path1+"与"+path2);
			
		}else{
			path1= "D:\\db2Record\\0809\\08.txt";
			path2= "D:\\db2Record\\0809\\09.txt";
			logger.info("用户未定义的月统计文件对比路径，使用默认路径"+path1+"与"+path2);
		}
		MergeTwoDbRec.loadAndCalc(path1,path2);
		new HandleExcel().process();
	}

}
