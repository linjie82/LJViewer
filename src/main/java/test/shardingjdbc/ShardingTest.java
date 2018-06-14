package test.shardingjdbc;

import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.api.config.TableRuleConfiguration;
import io.shardingjdbc.core.api.config.strategy.InlineShardingStrategyConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class ShardingTest {
	
	public static void main(String[] args) throws SQLException {
		// 配置真实数据源
	    Map<String, DataSource> dataSourceMap = new HashMap<>();
	    // 配置第一个数据源
	    BasicDataSource dataSource1 = new BasicDataSource();
	    dataSource1.setDriverClassName("oracle.jdbc.driver.OracleDriver");
	    dataSource1.setUrl("jdbc:oracle:thin:@10.1.8.83:1521/RECENGXDB");
	    dataSource1.setUsername("receng_dev");
	    dataSource1.setPassword("receng_dev");
	    dataSourceMap.put("ds_0", dataSource1);
	    
	    // 配置第二个数据源
	    BasicDataSource dataSource2 = new BasicDataSource();
	    dataSource2.setDriverClassName("oracle.jdbc.driver.OracleDriver");
	    dataSource2.setUrl("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=10.46.158.126)(PORT=1621)))(CONNECT_DATA=(SERVICE_NAME=tsbass)))");
	    dataSource2.setUsername("devmart");
	    dataSource2.setPassword("e4Sr_a4M");
	    dataSourceMap.put("ds_1", dataSource2);
	    
	    // 配置Order表规则
	    TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
	    orderTableRuleConfig.setLogicTable("t");
	    orderTableRuleConfig.setActualDataNodes("ds_${0..1}.t_${0..1}");
	    
	    // 配置分库 + 分表策略
	    orderTableRuleConfig.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("id", "ds_${id % 2}"));
	    orderTableRuleConfig.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("age", "t_${age % 2}"));
	    
	    // 配置分片规则
	    ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
	    shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);
	    
	    // 省略配置order_item表规则...
	    // ...
	    
	    // 获取数据源对象
	    DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new ConcurrentHashMap(), new Properties());
	
	    try(Connection connection = dataSource.getConnection()){
	    	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
	    	String sql= null;
				 sql= bufferedReader.readLine();
	    	while(!"exit".equalsIgnoreCase(sql)){
	    		try {
	    			ResultSet rs = connection.createStatement().executeQuery(sql);
	    	    	ResultSetMetaData rsmd = rs.getMetaData();
	    	    	while(rs.next()){
	    	    		for(int i=0;i<rsmd.getColumnCount();i++){
	    	    			System.out.print(rsmd.getColumnName(i+1)+":"+ rs.getObject(i+1) +"\t");
	    	    		}
	    	    		System.out.println();
	    	    	}
				} catch (Exception e) {
					e.printStackTrace();
				}
	    		sql= bufferedReader.readLine();
	    	}
	    	
//	    	connection.createStatement().execute("delete from t");
//	    	Random random = new Random();
//	    	PreparedStatement ps = connection.prepareStatement("insert into t (id,name,age,memo) VALUES (?,?,?,?)");
//	    	for(int i=1;i<100;i++){
//	    		ps.setInt(1, i);
//	    		ps.setString(2, "name_"+i);
//	    		ps.setInt(3, Math.abs(random.nextInt()%100));
//	    		ps.setString(4, "memo____"+i);
//	    		ps.addBatch();
//	    	}
//	    	ps.executeBatch();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
