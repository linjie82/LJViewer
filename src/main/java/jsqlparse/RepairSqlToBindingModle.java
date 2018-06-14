package jsqlparse;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitorAdapter;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.update.Update;

public class RepairSqlToBindingModle {
   
   public static void main(String[] args) throws JSQLParserException {
	   CCJSqlParserManager parser = new CCJSqlParserManager();
	   String sql="INSERT INTO EDC_OGG.USER_PRODUCT (HOME_CITY,PRODUCT_TYPE,SUBSCRIPTION_ID,USER_ID,PRODUCT_ID,STATUS,INURE_TIME,EXPIRE_TIME,OPERATION_ID,HISTORY_ID,CREATE_TIME,CREATE_ID,MODIFY_TIME,MODIFY_ID,REQUEST_SOURCE,TEST_FLAG,REC_UPDATE_TIME,change_inure_date,change_expire_date,load_time ) VALUES(595.0,1002.0,2.03385187981E11,5.95500170897995E14,1.20100092948E11,4.0,to_date('2015-10-18:14:05:27','yyyy-mm-dd hh24:mi:ss'),to_date(to_char(sysdate+1/24,'yyyy-mm-dd HH24'),'yyyy-mm-dd HH24:mi:ss '),2.19739107418E11,1.62745525267E11,null,null,to_date('2017-07-08:06:14:20','yyyy-mm-dd hh24:mi:ss'),5009998.0,201010.0,0.0,to_date('2017-07-08:06:14:21','yyyy-mm-dd hh24:mi:ss'),to_date('2017-07-08 06:14:28','yyyy-mm-dd hh24:mi:ss'),to_date(20991231,'yyyymmdd'),sysdate )";
	 
	   String update="update EDC_OGG.USER_PRODUCT set change_expire_date=sysdate  where HOME_CITY=595.0 and PRODUCT_TYPE=1002.0 and SUBSCRIPTION_ID=2.03385187981E11 and  change_inure_date between to_date(to_char(sysdate,'yyyy-mm-dd HH24'),'yyyy-mm-dd HH24:mi:ss') and to_date(to_char(sysdate+1/24,'yyyy-mm-dd HH24'),'yyyy-mm-dd HH24:mi:ss ') and change_expire_date>sysdate";
	   long btim = System.currentTimeMillis();
	   for(int i=0;i<10000;i++){
		   Statement stmt = parser.parse(new StringReader(update));
		  // System.out.println(stmt.toString());
		   if(stmt instanceof Insert){
			   
			   ExpressionList il = (ExpressionList) ((Insert) stmt).getItemsList();
			   InsertItemsListVisitor insertItemsListVisitor =new InsertItemsListVisitor();
			   il.accept(insertItemsListVisitor);
			   //System.out.println(stmt.toString());
			   //System.out.println(insertItemsListVisitor.data);
		   }else if(stmt instanceof Update){
			   Expression where = ((Update) stmt).getWhere();
			   WhereVisitor visitor = new WhereVisitor();
			   where.accept(visitor);
		   }
	   }
	   long etim = System.currentTimeMillis();
	   System.out.println((etim-btim)+"ms");
	  
   }
   
   public static class InsertStatmentVisitor extends StatementVisitorAdapter{
	   @Override
	    public void visit(Insert insert) {
		   insert.getItemsList().accept(new ItemsListVisitor() {
			
			@Override
			public void visit(MultiExpressionList multiExprList) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(ExpressionList expressionList) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(SubSelect subSelect) {
				// TODO Auto-generated method stub
				
			}
		});
	    }
   }
   
   public static class Value{
	   private Object data;
	   private String type;
	   public Value(Object data,String type) {
		   this.data=data;
		   this.type =type;
	   }
	public Object getData() {
		return data;
	}
	public String getType() {
		return type;
	}
	public String toString(){
		return data.toString()+" <"+type+">";
	}
   }
   
   
   public static class InsertItemsListVisitor extends ItemsListVisitorAdapter{
	   List<Value> data = new ArrayList<>();
	   
	   public void visit(ExpressionList expressionList){
		   List<Expression> expressions = new ArrayList<>(); 
		   List<Expression> els = expressionList.getExpressions();
		   for (Expression expression : els) {
			   if(expression instanceof LongValue){
				   data.add(new Value(((LongValue) expression).getValue(), "long"));
				   expressions.add(new JdbcParameter());
			   }else if(expression instanceof StringValue){
				   data.add(new Value(((StringValue) expression).getValue(),"string"));
				   expressions.add(new JdbcParameter());
			   }else if(expression instanceof DoubleValue){
				   data.add(new Value(((DoubleValue) expression).getValue(), "double"));
				   expressions.add(new JdbcParameter());
			   }else{
				   if(expression instanceof Function){
					   visit(((Function) expression).getParameters());
				   }
				   expressions.add(expression);
			   }
		   }
		   expressionList.setExpressions(expressions);
	   }
   }
   
   public static  class WhereVisitor extends ExpressionVisitorAdapter{
	   List<Value> ndatas = new ArrayList<>();
	   public List<Object> data = new ArrayList<>();

	private boolean inner_visitExp(Expression expression){
		if(expression instanceof Function){
			expression.accept(this);
			return true;
		}else{
			if(expression instanceof LongValue){
				ndatas.add(new Value(((LongValue) expression).getValue(), "long"));
		   }else if(expression instanceof StringValue){
			   ndatas.add(new Value(((StringValue) expression).getValue(),"string"));
		   }else if(expression instanceof DoubleValue){
			   ndatas.add(new Value(((DoubleValue) expression).getValue(), "double"));
		   }
			return false;
		}
		
	}
	   
	public void visit(GreaterThan gt) {
		  // data.add(gt.getRightExpression().toString());
		   if(!inner_visitExp(gt.getRightExpression())){
			   gt.setRightExpression(new JdbcParameter());
		   }
	   }
	   
	   public void visit(GreaterThanEquals gteq) {
		// data.add(gt.getRightExpression().toString());
		   if(!inner_visitExp(gteq.getRightExpression())){
			   gteq.setRightExpression(new JdbcParameter());
		   }
	   }
	   
	   public void visit( InExpression in) {
	   }
	   
	    public void visit(Function function) {
	        if (function.getParameters() != null) {
	            function.getParameters().accept(this);
	        }
	        if (function.getKeep() != null) {
	            function.getKeep().accept(this);
	        }
	    }
	   public void visit(EqualsTo eq) {
		// data.add(gt.getRightExpression().toString());
		   if(!inner_visitExp(eq.getRightExpression())){
			   eq.setRightExpression(new JdbcParameter());
		   }
	   }
	   
	   public void visit(Between between) {
		   if(!inner_visitExp(between.getBetweenExpressionStart())){
			   between.setBetweenExpressionStart(new JdbcParameter());
		   }
		   if(!inner_visitExp(between.getBetweenExpressionEnd())){
			   between.setBetweenExpressionEnd(new JdbcParameter());
		   }
	   }
	   
   }
}