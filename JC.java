package com.arrow.qa.swb.testcases.api;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.testng.annotations.Test;
//import com.arrow.qa.swb.common.BaseTest;
import io.restassured.response.Response;

@Test
public class JC{

	public JC() {
		// TODO Auto-generated constructor stub
			}
	
	//A POST to /hash should accept a password.
	public String testJCHashPost(String pwd) {
		
			String mypwd = "{\"password\":\"" + pwd + "\"}";
			System.out.println("JC Hash Post password --> " + pwd);	
			System.out.println("JC Hash Post mypwd --> " + mypwd);	
			Response res = given()
							.header("Content-Type", "text/html")
							.body(mypwd)
				           .when().post("http://127.0.0.1:8088/hash");
			
			String ResponseString = res.asString();
			System.out.println("JC Hash Post ResponseString --> " + ResponseString);
			assertEquals(res.getStatusCode(), 200, "Status code expected to be OK 200");
		return ResponseString;
		
	}
	//A GET to /hash should accept a job identifier.
	public void testJCHashGet() throws NoSuchAlgorithmException, IOException {
		
	    
		String pwd = "angrymonkey";
		String hashCode = testJCHashPost(pwd);
//		System.out.println("hashCode --> :" + hashCode);
		
		String newURL = "http://127.0.0.1:8088/hash/"+ hashCode;		
		System.out.println("newURL --> :" + newURL);
		
		Response res = given()
						.header("Content-Type", "text/html")
				  		.body("")
				  		.when().get(newURL);
		String ResponseString = res.asString();
		assertEquals(res.getStatusCode(), 200, "Status code expected to be OK 200");
//		System.out.println("JC  Hash  Get  ResponseString --> " + ResponseString);	
		
		//converting given password to SHA512Base64 and returning a string
		String strSha512ToBase64 =  fileSha512ToBase64(pwd);
//		System.out.println("JC Hash Get strSha512ToBase64 --> " + strSha512ToBase64);	
		//comparing the SHA 512 base64 generated by the application is matching with the code used in the test
		assertEquals(strSha512ToBase64, ResponseString, "Sha512ToBase64 should match with the generated string");
	}
	
	//Below test make sure if the non existing job id is given test will fail
	public void testJCHashGetWhereJobIDDoesNotExists() throws NoSuchAlgorithmException, IOException {
		
	    
		String pwd = "angrymonkey";
		String hashCode = "100";
//		System.out.println("hashCode --> :" + hashCode);
		
		String newURL = "http://127.0.0.1:8088/hash/"+ hashCode;		
		System.out.println("newURL --> :" + newURL);
		
		Response res = given()
						.header("Content-Type", "text/html")
				  		.body("")
				  		.when().get(newURL);
		String ResponseString = res.asString();
		//As per standard expectation it should 404 but returning 400
		assertEquals(res.getStatusCode(), 400, "Status code expected to be OK 200");
}
	
	//A GET to /stats should accept no data
	public void testJCStatsGet() throws NoSuchAlgorithmException, IOException {	
		
		testJCHashGet();
		Response res = given()
						.header("Content-Type", "text/html")
				  		.body("")
			           .when().get("http://127.0.0.1:8088/stats");
		String ResponseString = res.asString();
		assertEquals(res.getStatusCode(), 200, "Status code expected to be OK 200");
		System.out.println("JC Stats ResponseString --> " + ResponseString);
		
	}
	
//Executing in loop to test concurrency with postman	
public void testJCStatsGetInLoop() throws NoSuchAlgorithmException, IOException {
		
		
		for (int i = 0; i<3; i++)
		{
		testJCHashGet();
		Response res = given()
						.header("Content-Type", "text/html")
				  		.body("")
			           .when().get("http://127.0.0.1:8088/stats");
		String ResponseString = res.asString();
		assertEquals(res.getStatusCode(), 200, "Status code expected to be OK 200");
		System.out.println("JC Stats ResponseString  --> " + ResponseString + "  " + " Iteration : " + i);
		}
	}

	//The software should support a graceful shutdown request.
	public void testShutDown() throws NoSuchAlgorithmException, IOException {
		
		
//		testJCHashGet();
		Response res = given()
						.header("Content-Type", "text/html")
				  		.body("shutdown")
			           .when().get("http://127.0.0.1:8088/hash");
		String ResponseString = res.asString();
		System.out.println("JC hash res.getStatusCode() --> " + res.getStatusCode());
		
		assertEquals(res.getStatusCode(), 200, "Status code expected to be OK 200");
		System.out.println("JC Stats ResponseString --> " + ResponseString);
		
	}

	
	//SHA512base64 password Conversion
	public static String fileSha512ToBase64(String str1) throws NoSuchAlgorithmException, IOException {
	    byte[] bytes = str1.getBytes("UTF-8");
	    
	    MessageDigest digester = MessageDigest.getInstance("SHA-512");
	    digester.update(bytes);
	    
	    String myenc = Base64.getEncoder().encodeToString(digester.digest());
//	    System.out.println("Base64.getEncoder().encodeToString(digester.digest()) in loop" + " :: pwd :: " + str1 + " :: "+ myenc);
	    return myenc;
	}

	
	
	
}