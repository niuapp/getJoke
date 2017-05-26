package jsoup_test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.text.html.parser.Entity;

public class UrlP {
	public static String url = "";
	public static String preCreateUrl = "";;

	public static String outPath = "";
	public static String vbsPath = "";
	public static String endFilePath = "";

	public static void main(String[] args) {

		
		if (getUrl()) {
			GetJoke.loadContentHtml(UrlP.url, UrlP.preCreateUrl);
		}else {
			System.out.println("≈‰÷√Œƒº˛¥ÌŒÛ");
		}
	}

	private static boolean getUrl() {

		File pathFile = new File("C:\\joke\\joke_url.txt");

		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					pathFile), "gbk"));

			HashMap<String, String> urlMap = new HashMap<String, String>();
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line != null && line.trim().length() > 0) {
					String[] mapStr = line.split("£§£§£§");
				urlMap.put(mapStr[0].trim(), mapStr[1].trim());
				}
			}
			in.close();
			
			if (urlMap.size() == 5) {
				
				url = urlMap.get("nextUrl");
				preCreateUrl = urlMap.get("preUrl");
				outPath = urlMap.get("outPath");
				vbsPath = urlMap.get("vbsPath");
				endFilePath = urlMap.get("endFilePath");
				
				if (empty(url) && empty(preCreateUrl) && empty(outPath) && empty(vbsPath) && empty(endFilePath)) {
					return true;
				}
			}
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	private static boolean empty(String str) {
		if (str == null || "".equals(str)) {
			return false;
		}
		return true;
	}
}
