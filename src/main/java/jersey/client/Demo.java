package jersey.client;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

public class Demo {
	
	
	public static void main(String[] args) throws UnsupportedEncodingException {
			
			//用户信息   封装在统一的方法里面， 后面如有改变或添加 容易 统一修改header，
	        MultivaluedMap<String,Object> authHeaders = new MultivaluedHashMap<>();
	        authHeaders.add("X-REQ-USER", URLEncoder.encode("1","UTF-8"));    //用户id 
	        authHeaders.add("X-REQ-GROUP", URLEncoder.encode("1","UTF-8"));    //租户用户组id
	        authHeaders.add("X-REQ-TENANCY", URLEncoder.encode("34234234","UTF-8")); //租户id
	        authHeaders.add("X-REQ-TENANCYNAME", URLEncoder.encode("重点是", "UTF-8")); //租户id
	        authHeaders.add("X-REQ-USERNAME", URLEncoder.encode("重点是", "UTF-8")); //租户id
	        authHeaders.add("X-REQ-GROUPNAME", URLEncoder.encode("重点是", "UTF-8")); //租户id
	        authHeaders.add("Content-Type", "application/json");
//	        //查询客户群信息
//	        //GET  访问  http://127.0.0.1:8081/recEng/v1/customergrp?grpid=206
	        String json= ClientBuilder.newClient()
	        		.target("http://127.0.0.1:8081/recEng/v1/customergrp;sss=rrrrrrrrr?grpid=206")
	        		//.queryParam("namelike", "客户群")  //.target("http://10.1.2.6:8081/recEng/v1/customergrp?grpid=206") 也可以
	        		.request()
	        		.headers(authHeaders)
	        		.get(String.class);
	        System.out.println(json);
//	        
//	       String json= JerseyClientBuilder.newClient()
////	        		.target("http://10.1.2.225:8081/recEng/v1/customergrp")
////	        		.queryParam("grpid", 415)  //
//	        		.target("http://10.1.2.225:8081/recEng/v1/customergrp?grpid=415") 
//	        		//也可以
//	        		.request()
//	        		.headers(authHeaders)
//	        		.get(String.class);
//	        System.out.println(json);
//	        
////	        //受众分析
////	        //POST PUT
//	        Entity<String> entity = Entity.entity("{'condition':{'tag_conditon':{'$and':[{'t_i_1':{'$gt':18}},{'t_i_1':{'$lt':60}},{'t_i_2':{'$in':[1,2,3,4]}}]},'model_conditon':{'model_id':12321,'prod_conditon':{'$or':['r_1']}}},'groupby':{'tag_groupby':['t_s_3','t_s_4'],'prod_groupby':[{'model_id':12321,'prodid':'r_3'}]}}", MediaType.APPLICATION_JSON);
//	        String postjson= ClientBuilder.newClient()
//	        		.target("http://10.1.2.6:8081/recEng/v1/customergrp/analyseByCondition")
//	        		.request()
//	        		.headers(authHeaders)
//	        		.post(entity,String.class);
//	        System.out.println(postjson);
//	        
//	        String respstring = ClientBuilder.newClient()
//	        		.target("http://10.1.2.225:18081/nl-edc-recomm-enginee/services/v1/customegroup/persona?grpid=224")
//	        		.request()
//	        		.header("Content-Type", "application/json")
//	        		.get(String.class);
//	        System.out.println(respstring);
	        //文件上传
	        String filejson=JerseyClientBuilder.createClient()
				        .register(MultiPartFeature.class)
				        .target("http://10.1.2.225:8081/recEng/v1/material?id=xx")
			    		.request()
			    		.headers(authHeaders)
			    		.post(Entity.entity(new FormDataMultiPart().bodyPart(new FileDataBodyPart("file", new File("E:/1.png"))), MediaType.MULTIPART_FORM_DATA_TYPE),String.class);
    		 System.out.println(filejson);
	        
	        
//	        String result=JerseyClientBuilder.createClient()
//	        .target("http://10.1.2.220:8083/recEng-Recomm/v1/recqry/recQryAction#xxxxxxxx|yyyyyy")
//    		.request()
//    		.post(Entity.entity("tkn_name=qdcc1,qdcc2&tkn_val=38CB4,22c&msisdn=13946084253&mediaId=10001&positionId=ad10001&adNumbers=1&materialType=2&reservedOne=one&reservedTwo=two&reservedThree=three",MediaType.APPLICATION_FORM_URLENCODED),String.class);
//	        System.out.println(result);
	}
	

}
