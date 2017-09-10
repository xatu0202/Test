import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import mjson.Json;
import org.json.JSONObject;
public class TwitterStreaming extends Twitter implements Runnable{
	protected static boolean stopper;
	protected OAuth t;
	TwitterStreaming(OAuth T){
		this.t=T;
	}
	public void run(){
		stopper = false;
		String paramStr = "";
		String request = "";
		for (Entry<String, String> param : t.params.entrySet()) {
			paramStr += ", " + param.getKey() + "=\""
					+ OAuth.urlEncode(param.getValue()) + "\"";
		}
		for (Entry<String, String> param : t.requests.entrySet()) {
			request +=param.getKey() + "=" + param.getValue() + "&";
		}
		paramStr = paramStr.substring(2);
		String authorizationHeader = "OAuth " + paramStr;
		request=request.substring(0, request.length()-1);
		String response = "";
		Json jsonObject;
		try{
			URL url = new URL(t.urlStr + "?" + request);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(t.method);
			connection.setRequestProperty("Authorization", authorizationHeader);
			connection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()
			));
			System.out.println("start streaming");
			while ((response = reader.readLine()) != null) {
				//System.out.println(response);
				if(response.indexOf("text")!=-1){
				try{
				jsonObject = Json.read(response);
				System.out.println(jsonObject.at("created_at"));
				System.out.println(jsonObject.at("user").at("name")+"\t@"+jsonObject.at("user").at("screen_name"));
				System.out.println(jsonObject.at("text"));
				System.out.println("################################################################################");
				if(stopper){
					System.out.println("StopFlag:true");
					break;
				}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			}
		}catch(Exception e){
			
		}
		System.out.println("finish streaming");
	}
}
