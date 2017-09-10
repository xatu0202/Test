import java.util.SortedMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;

public class Main {
	static OAuth t;
	static String ck;
	static String cs;
	static String ot;
	static String os;
	static Scanner in;
	static TwitterStreaming currentStreaming;
	static SortedMap<String, String> requestParams;
	public static void main(String args[]){
		in = new Scanner(System.in);
		File file = new File("keys");
		System.out.println("Loading saved keys?  [Y/N]");
		String yn=in.next();
		if(yn.equals("Y")||yn.equals("y")){
			if(file.exists()){
				try{
					BufferedReader br = new BufferedReader(new FileReader(file));
					ck = br.readLine();
					cs = br.readLine();
					ot = br.readLine();
					os = br.readLine();
					br.close();
				}
				catch(Exception e){
					System.out.println("Error:Reading file");
				}
			}
			else{
				System.out.println("No Key File found.Please saving before loading.");
				System.out.println("Input your keys:consumer key,consumer secret,OAuthToken,and OAuth Secret");
				ck = in.next();
				cs = in.next();
				ot = in.next();
				os = in.next();
			}
		}
		else{
			System.out.println("Input your keys:consumer key,consumer secret,OAuthToken,and OAuth Secret");
			ck = in.next();
			cs = in.next();
			ot = in.next();
			os = in.next();
		}
		while(in.hasNext()){
			try{
				String input = in.next();
				if(input.equals(":tw")){
					StatusUpdate();
				}
				else if(input.equals(":AR")){
					currentStreaming = StreamingGetUser();
				}
				else if(input.equals(":stop")){
					currentStreaming.stopper=true;
				}
				else if(input.equals(":save")){
					if(SaveKeys())System.out.println("Saved Keys");
					else System.out.println("ERROR");
				}
				else if(input.equals(":help")){
					System.out.println(
						":tw\t\t Tweet Post\n"+
						":AR\t\t See TimeLine\n"+
						":save \t\tsave your current keys\n"+
						":help \t\tlook all commands\n"
					);
				}
				else{
					System.out.println("***Unkonow command*** "+input+"\nYou can find all commands by using ':help'");
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
		Twitter.sendRequest(t);
	}
	private static TwitterStreaming StreamingGetUser(){
		requestParams = new TreeMap<String, String>();
		t = new OAuth(ck,cs,ot,os,"POST","https://userstream.twitter.com/1.1/user.json");
		requestParams.put("replies",OAuth.urlEncode("true"));
		t.buildSignature(requestParams);
		TwitterStreaming TL = new TwitterStreaming(t);
		Thread T1 = new Thread(TL);
		T1.start();
		return TL;
	}
	private static boolean SaveKeys(){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("keys")));
			bw.write(ck);
			bw.newLine();
			bw.write(cs);
			bw.newLine();
			bw.write(ot);
			bw.newLine();
			bw.write(os);
			bw.newLine();
			bw.close();
		}
		catch(Exception e){
			return false;
		}
		return true;
	}
}