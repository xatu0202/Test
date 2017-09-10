import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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