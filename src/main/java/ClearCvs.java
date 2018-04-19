import java.io.File;


public class ClearCvs {
	
	public static void main(String[] args) {
		String base = "E:/WorkSpaces/bigdata2spaces/edc-bigdata-receng/edc-bigdata-receng-core/receng_backend";
		scanAndDel(new File(base));
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
