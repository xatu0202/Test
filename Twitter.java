import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Twitter {
	static OAuth t;
	static String ck;
	static String cs;
	static String ot;
	static String os;
	static Scanner in;
	static SortedMap<String, String> requestParams;
	public static void main(String args[]){
		in = new Scanner(System.in);
		ck = in.next();
		cs = in.next();
		ot = in.next();
		os = in.next();
		while(in.hasNext()){
			try{
				String input = in.next();
				if(input.equals(":tw")){
					StatusUpdate();
				}
				if(input.equals(":AR")){
					StreamingGetUser();
				}
				else{
					System.out.println("Unkonow command :"+input);
				}
			}catch(Exception e){
				e.printStackTrace();
			};
		}
	}
	private static void StatusUpdate(){
		requestParams = new TreeMap<String, String>();
		t = new OAuth(ck,cs,ot,os,"POST","https://api.twitter.com/1.1/statuses/update.json");
		requestParams.put("status",OAuth.urlEncode(in.next()));
		t.buildSignature(requestParams);
		t.sendRequest();
	}
	private static void StreamingGetUser(){
		requestParams = new TreeMap<String, String>();
		t = new OAuth(ck,cs,ot,os,"POST","https://userstream.twitter.com/1.1/user.json");
		requestParams.put("replies",OAuth.urlEncode("true"));
		t.buildSignature(requestParams);
		t.sendRequest();
	}


}

class OAuth{
	String consumerkey,consumerSecret,oauthToken,oauthTokenSecret,method,urlStr;
	SortedMap<String, String> params,requests;
	OAuth(String ck,String cs,String ot,String os,String met,String url){
		consumerkey=ck;
		consumerSecret=cs;
		oauthToken=ot;
		oauthTokenSecret=os;
		method=met;
		urlStr=url;
		params= new TreeMap<String, String>();
		params.put("oauth_consumer_key", consumerkey);
		params.put("oauth_signature_method", "HMAC-SHA1");
		params.put("oauth_timestamp", String.valueOf(getUnixTime()));
		params.put("oauth_nonce", String.valueOf(Math.random()));
		params.put("oauth_version", "1.0");
		params.put("oauth_token", oauthToken);
	}
	void buildSignature(SortedMap<String, String> req){
		String paramStr = "";
		requests = req;
		for (Entry<String, String> param : params.entrySet()) {
			paramStr += "&" + param.getKey() + "=" + param.getValue();
		}
		for (Entry<String, String> param : requests.entrySet()) {
			paramStr += "&" + param.getKey() + "=" + param.getValue();
		}
		paramStr = paramStr.substring(1);
		String text = method + "&" + urlEncode(urlStr) + "&" + urlEncode(paramStr);
 		String key = urlEncode(consumerSecret) + "&" + urlEncode(oauthTokenSecret);
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),"HmacSHA1");
		try{
			Mac mac = Mac.getInstance(signingKey.getAlgorithm());
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(text.getBytes());
			String signature = Base64.getEncoder().encodeToString(rawHmac);
			params.put("oauth_signature", signature);
		}catch(Exception e){
			
		}
	}
	String sendRequest(){
		String paramStr = "";
		String request = "";
		for (Entry<String, String> param : params.entrySet()) {
			paramStr += ", " + param.getKey() + "=\""
					+ urlEncode(param.getValue()) + "\"";
		}
		for (Entry<String, String> param : requests.entrySet()) {
			request +=param.getKey() + "=" + param.getValue() + "&";
		}
		paramStr = paramStr.substring(2);
		String authorizationHeader = "OAuth " + paramStr;
		request=request.substring(0, request.length()-1);
		StringBuilder responses = new StringBuilder();;
		String response = "";
		try{
			System.out.println(urlStr + "?" + request);
			URL url = new URL(urlStr + "?" + request);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			connection.setRequestProperty("Authorization", authorizationHeader);
			connection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()
			));
			while ((response = reader.readLine()) != null) {
				responses.append(response);
				System.out.println(response);
			}
		}catch(Exception e){
			
		}
		return responses.toString();
	}
		public static int getUnixTime() {
		return (int) (System.currentTimeMillis() / 1000L);
	}

	public static String urlEncode(String string) {
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
