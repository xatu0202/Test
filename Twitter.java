import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

public class Twitter{
	public static String sendRequest(OAuth t){
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
		StringBuilder responses = new StringBuilder();;
		String response = "";
		try{
			URL url = new URL(t.urlStr + "?" + request);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(t.method);
			connection.setRequestProperty("Authorization", authorizationHeader);
			connection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()
			));
			while ((response = reader.readLine()) != null) {
				responses.append(response);
			}
		}catch(Exception e){
			
		}
		return responses.toString();
	}
}
