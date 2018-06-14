package calcite;

import java.util.concurrent.atomic.AtomicLongArray;


public class CalciteTest {
	
	public static void main(String[] args) {
		AtomicLongArray a = new AtomicLongArray(2);
		for (int i = 0; i < 18; i++) {
			System.out.println(i+":"+a.get(i));
		}
	}

}
