package jsqlparse;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Types;

public class DbBind {
//	spring.druid.url=jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=10.1.8.99)(PORT=1521))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME = RECENG_DEV)))
//			spring.druid.username=receng_dev
//			spring.druid.password=receng_dev
//			spring.druid.driver-class-name=oracle.jdbc.driver.OracleDriver
	public static void main(String[] args) throws Exception{
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(
				"jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=10.1.8.99)(PORT=1521))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME = RECENG_DEV)))"
				,"receng_dev","receng_dev");
		
		String sql ="insert into receng_cfg_custormergroup(ID,APP_ID,NAME,CREATE_TIME) "
				+ "values(?,?,?,to_date(?, ?))";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setLong(1, 11114);
		ps.setString(2, "app_dd");
		ps.setString(3,"aaaaa");
		ps.setString(4, "2015-10-18");
		ps.setString(5, "yyyy-mm-dd");
		ps.addBatch();
	//	ps.setString(4,"to_date('2015-10-18:14:05:27', 'yyyy-mm-dd hh24:mi:ss')");
		ps.execute();
	}
}
