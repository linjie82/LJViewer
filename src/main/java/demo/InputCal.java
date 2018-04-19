package demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InputCal {
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader("E:/WorkSpaces/bigdata2spaces/redisdemo/src/main/java/demo/file"));
		int cnt = Integer.valueOf( bufferedReader.readLine());
		int[][] ary = new int[cnt][cnt];
		for(int i=0;i<cnt;i++){
			String line = bufferedReader.readLine();
			String[] cs = line.split(",");
			for(int j=0;j<cs.length;j++){
				ary[i][j]=Integer.valueOf(cs[j]);
			}
		}
		System.out.println(cnt);
		for(int i=0;i<cnt;i++){
			for(int j=0;j<=i;j++){
				System.out.print(ary[i][j]+" ");
			}
			System.out.println();
		}
		maxSum = new int[cnt][cnt];
		
//		System.out.println(max(0,0,ary));
//		System.out.println("cal:"+c);
		
		System.out.println(max2(ary));
	}
	static int[][] maxSum ;
	static int c=0;
	public static int max(int i,int j,int[][] ary){
		c++;
		if(i==ary.length-1){
			return ary[i][j];
		}
		if(maxSum[i][j]==0){
			maxSum[i][j]= Math.max(max(i+1, j, ary), max(i+1, j+1, ary))+ary[i][j];
		}
		return maxSum[i][j];
	}

	
	public static int max2(int[][] ary){
		int maxlen = ary.length;
		for(int i=maxlen-2;i>=0;i--){
			for(int j=0;j<=i;j++){
				ary[i][j] = Math.max(ary[i][j]+ary[i+1][j], ary[i][j]+ary[i+1][j+1]);
			}
		}
		return ary[0][0];
	}
}
