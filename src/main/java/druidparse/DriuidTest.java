package druidparse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleExportParameterVisitor;

public class DriuidTest {
	
	static String sql="INSERT INTO EDC_OGG.USER_PRODUCT (HOME_CITY,PRODUCT_TYPE,SUBSCRIPTION_ID,USER_ID,PRODUCT_ID,STATUS,INURE_TIME,EXPIRE_TIME,OPERATION_ID,HISTORY_ID,CREATE_TIME,CREATE_ID,MODIFY_TIME,MODIFY_ID,REQUEST_SOURCE,TEST_FLAG,REC_UPDATE_TIME,change_inure_date,change_expire_date,load_time ) VALUES(595.0,1002.0,2.03385187981E11,5.95500170897995E14,1.20100092948E11,4.0,to_date('2015-10-18:14:05:27','yyyy-mm-dd hh24:mi:ss'),to_date(to_char(sysdate+1/24,'yyyy-mm-dd HH24'),'yyyy-mm-dd HH24:mi:ss '),2.19739107418E11,1.62745525267E11,null,null,to_date('2017-07-08:06:14:20','yyyy-mm-dd hh24:mi:ss'),5009998.0,201010.0,0.0,to_date('2017-07-08:06:14:21','yyyy-mm-dd hh24:mi:ss'),to_date('2017-07-08 06:14:28','yyyy-mm-dd hh24:mi:ss'),to_date(20991231,'yyyymmdd'),sysdate )";
	 
	static String update="update EDC_OGG.USER_PRODUCT set change_expire_date=sysdate  where HOME_CITY=595.0 and PRODUCT_TYPE=1002.0 and SUBSCRIPTION_ID=2.03385187981E11 and  change_inure_date between to_date(to_char(sysdate,'yyyy-mm-dd HH24'),'yyyy-mm-dd HH24:mi:ss') and to_date(to_char(sysdate+1/24,'yyyy-mm-dd HH24'),'yyyy-mm-dd HH24:mi:ss ') and change_expire_date>sysdate";
	  

	public static class PrepareData{
		
		public PrepareData(String sql,List<Object> params) {
			this.sql=sql;
			this.params=params;
		}
		private String sql;
		
		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

		private List<Object> getParams() {
			return params;
		}

		public void setParams(List<Object> params) {
			this.params = params;
		}

		public List<Object> params;
		
	}
	 
	 public static void main(String[] args) throws Exception {
		 List<String> sqls = new ArrayList<String>();
		 for (int i = 0; i < 1; i++) {
			 sqls.add("insert into TEST(ID,NAME,AGE) values("+i+",'name_"+i+"',"+((i%100)+5)+")");
		 }
		
		 Class.forName("oracle.jdbc.driver.OracleDriver");
		 Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@10.1.8.83:1521/RECENGXDB","receng_dev","receng_dev");
		 connection.setAutoCommit(false);
		 long b = System.currentTimeMillis();
		 Statement st = connection.createStatement();
		 for (String sql:sqls) {
			PrepareData data= buildSql(sql);
			PreparedStatement ps= connection.prepareStatement(data.sql);
			for(int p=0;p<data.params.size();p++){
				ps.setObject(p+1, data.params.get(p));
			}
			ps.execute();
			ps.close();
			
			// st.addBatch(sql);
			
//			 /connection.createStatement().executeUpdate(sql);
		}
		 st.executeBatch();
		 st.close();
		 connection.commit();
		long e = System.currentTimeMillis();
		System.out.println((e-b)+"ms");
        
	}
	 
	 public static PrepareData buildSql(String sqlx){
		String sql="insert into test (id,name,age) values (1115,'abc',2*3*4)";
		StringBuilder out = new StringBuilder();
		List<Object> params = new ArrayList<>();
		OracleExportParameterVisitor visitor = new OracleExportParameterVisitor(params,out,true);
		OracleStatementParser oracleStatementParser = new OracleStatementParser(sql);
		List<SQLStatement> statementList = oracleStatementParser.parseStatementList();
        for (SQLStatement statement : statementList) {
        	statement.accept(visitor);
        }
        System.out.println(out.toString());
        System.out.println(params);
	      return new PrepareData(out.toString(), params);
	 }
}
