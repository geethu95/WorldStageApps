package worldstage;

import static io.restassured.RestAssured.*;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

public class NDI {

	@Test
	public void UserLogin() {
		RestAssured.baseURI="https://w-call-backend-chime.herokuapp.com/";
		String response=given().header("Content-Type","application/json").body("{\"email\": \"geethu@abacies.com\", \"password\": \"Geethu@6383\"}").
				when().post("user/login").
				then().assertThat().statusCode(200).header("Server","gunicorn/20.0.4").
				extract().response().asString();  //extracting response as a string
		System.out.println(response);
		JsonPath js= new JsonPath(response);  //passing json
		String AuthToken=js.getString("auth_token");        //authtoken store
		System.out.println("Auth Token is "+AuthToken);

		JsonPath js1= new JsonPath(response);  //passing json
		String user_id=js1.getString("data.id");        //authtoken store
		System.out.println("userid is "+user_id);

		System.out.println("invite new user");
		given().header("Authorization","Token "+AuthToken+"").header("Content-Type","application/json").body("{\"email\": \"testabacies0@gmail.com\", \"name\": \"Test Abacies\", \"user_role\": 3}").
		when().post("/admin/invite_new_user").
		then().assertThat().log().all().statusCode(200);
		//body("msg", equalTo("success"));  JSON path msg doesn't match error

		//signup,Reset password- token needed , token received in mail

		System.out.println("Forget password");
		given().header("Content-Type","application/json").body("{\"email\": \"geethu@abacies.com\"}").
		when().post("user/forgot_password").
		then().assertThat().log().all().statusCode(200);

		//list users
		given().header("Authorization","Token "+AuthToken+"").
		when().get("user/list_users").
		then().assertThat().log().all().statusCode(200).extract().response().asString();

		//user in page2		
		given().header("Authorization","Token "+AuthToken+"").queryParam("page","2").
		when().get("admin/list_users").
		then().assertThat().log().all().statusCode(200).extract().response().asString();

		System.out.println("edit user detail");
		given().header("Authorization","Token "+AuthToken+"").header("Content-Type","application/json").body("{\"name\": \"Geethu Test \", \"file\": \"input_file\"}").
		when().put("/user/edit_user_details/" +user_id+"").
		then().assertThat().log().all().statusCode(200);

		System.out.println("add new project");
		given().header("Authorization","Token "+AuthToken+"").header("Content-Type","application/json").body("{\"name\": \"API_Automation\", \"client_name\": \"Geethu\", \"job_number\": \"post123\",  \"add_users\": [1,++], \"recording\": true}").
		when().post("/project/add").
		then().assertThat().log().all().statusCode(200);

		System.out.println("List project and get ID");
		String projectID=given().header("Authorization","Token "+AuthToken+"").header("Content-Type","application/json").queryParam("page","1").queryParam("per_page","10").queryParam("time_zone","Asia/Kolkata").
				when().get("/project/list").
				then().assertThat().log().all().statusCode(200).extract().response().asString();

		JsonPath ID= new JsonPath(projectID);  
		String PID=ID.getString("data.id");   
		String name=ID.getString("data.name");
		System.out.println("Project ID is "+PID);
		System.out.println("Project name is "+name);
	}
}
