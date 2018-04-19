package demo;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimerDemo {
	
	static Timer timer = new Timer();
	static ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
	static ExecutorService executorService = Executors.newFixedThreadPool(6);
	
	
	
	public static void sched(final String name,long time){
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("invoke run   " +name);
				if(map.putIfAbsent(name, new Object())==null){
					executorService.submit(new Runnable() {
						@Override
						public void run() {
							try {
								System.out.println(name+"     in");
								Thread.sleep(6000);
							} catch (Exception e) {
								e.printStackTrace();
							}finally{
								System.out.println("remove   "+name);
								map.remove(name);
							}							
						}
					});
				}else{
					System.out.println("has key");
				}
			}
		}, new Date(), time);
	}
	
	public static void main(String[] args) {
		sched("a",2000);
		sched("b",1000);
	}

}
