package demo;

public class Closeable1 implements AutoCloseable {

	@Override
	public void close() throws Exception {
		System.out.println("cloase11");
	}

}
