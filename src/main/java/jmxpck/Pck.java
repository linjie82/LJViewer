package jmxpck;

public class Pck implements PckMBean {
	
	private String name;

	@Override
	public void sayName() {
		System.out.println("say:"+name);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
