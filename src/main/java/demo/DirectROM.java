package demo;

import java.nio.ByteBuffer;

public class DirectROM {
	
	public static void main(String[] args) {
		DirectROM directROM = new DirectROM();
		directROM.setAge(111);
		directROM.setName("abdds");
		ByteBuffer bb =ByteBuffer.allocateDirect(10000);
	}
	
	private String name;
	
	private int age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
