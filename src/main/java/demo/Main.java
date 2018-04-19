package demo;

public class Main {
	
	public static void main(String[] args) {
		try(Closeable1 closeable1=new Closeable1();Closeable3 closeable3 = new Closeable3()){
			
		}catch (Exception e) {
		}
	}

}
