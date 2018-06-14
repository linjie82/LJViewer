import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ClearCvs {
	
	public static void main(String[] args) {
//		String base = "E:/WorkSpaces/bigdata2spaces/edc-bigdata-receng/edc-bigdata-receng-core/receng_backend";
//		scanAndDel(new File(base));]
		List<Integer> ls = new ArrayList<>();
		long b = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			ls.add(i);
			if((i+1)%1000000==0){
				System.out.println(i);
			}
		}
		long e = System.currentTimeMillis();
		System.err.println((e-b)+"ms");
	}
	
	public static BigDecimal calE(int n){
		BigDecimal bigDecimal =new BigDecimal(1).divide(new BigDecimal(n),15,RoundingMode.CEILING).add(new BigDecimal(1)).pow(n);
		return bigDecimal;
	}
	
	public static void scanAndDel(File file){
		if(file.isDirectory()){
			if(file.getName().equals("CVS")){
				System.out.println("delelt:"+file.getAbsolutePath());
				deleteIt(file);
			}else{
				for (File f : file.listFiles()) {
					if(f.isDirectory()){
						scanAndDel(f);
					}
				} 
			}
		}
	}
	
	public static void deleteIt(File file){
		if(file.isDirectory()){
			for (File f : file.listFiles()) {
				deleteIt(f);
			} 
		}
		file.delete();
	}

}
