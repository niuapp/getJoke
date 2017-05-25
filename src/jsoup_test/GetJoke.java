package jsoup_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetJoke {

	private static String url = "http://wap.jokeji.cn/JokeHtml/jt/2017052217012842.asp";
	private static String preCreateUrl="http://wap.jokeji.cn/JokeHtml/mj/2017052217025310.asp";;
	private static String loadNextUrl;
	private static int nextPagerCount = 3;

	public static void main(String[] args) {

		// System.out.println("开始： " + startCount++);
		loadContentHtml(url, preCreateUrl);
	}

	// private static int count = 33;
	private static int startCount = 1;

	private static void loadContentHtml(String url, String preUrl) {
		final String tempUrl = url;
		final String tempPreUrl = preUrl;

		System.out.println("开始： " + startCount++);

		try {
			Thread.sleep(100);

			Document document = Jsoup.connect(
					URLEncoder.encode(url, "utf-8").toLowerCase()
							.replaceAll("%3a", ":").replaceAll("%2f", "/"))
					.get();

			// 下一页
			String nextUrl = "";

			Element el = document.select("div.main>div.view_cont").first();
			if (el.toString().contains("class=\"view_cont bod_line\"")) {
				if (el.toString().contains("class=\"jingpin\"")) {
					// 专题 下一页的地址要更改
					el = Jsoup
							.parse("<div>" + el.select("p").toString()
									+ "</div>").select("div").first();
					nextUrl = document
							.select("div.main>div.page>ul>li.active+li")
							.select("a").first().absUrl("href");
				} else {
					el = el.select("p").first();
					nextUrl = document.select("div.main>div.page_next>ul>li")
							.select("a").first().absUrl("href");
				}
			} else {
				nextUrl = document.select("div.main>div.page_next>ul>li.next")
						.select("a").first().absUrl("href");
			}

			// 下一页url

			// 如果有图片，就跳过这页
			if (!el.toString().contains("<img")) {

				el.attr("style", "padding:40px;");

				// html
				final StringBuffer stringBuffer = new StringBuffer();
				stringBuffer
						.append("<!DOCTYPE html>\n"
								+ "<html>\n"
								+ "<head>\n"
								+ "\t<meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml; charset=UTF-8\">\n"
								+ "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0\">\n"
								+

								"</head>\n" + "<body>");

				stringBuffer.append(el.toString());

				String nextPage = "".equals(nextUrl) ? "" : nextUrl
						.substring(nextUrl.lastIndexOf("/") + 1) + ".html";
				String prePage = "".equals(preUrl) ? "" : preUrl
						.substring(preUrl.lastIndexOf("/") + 1) + ".html";
				stringBuffer
						.append("<a style=\"position:fixed;left:0px;bottom:10px;\" href=\""
								+ prePage
								+ "\">上一页"
								+ "<a style=\"position:fixed;right:0px;bottom:10px;\" href=\""
								+ nextPage
								+ "\">下一页"
								+ "</a></body>\n"
								+ "\n"
								+ "</html>");

				// 一页 写
				writeFile(stringBuffer.toString(),
						url.substring(url.lastIndexOf("/") + 1) + ".html");

				final String finalNextUrl = nextUrl;

				loadNext(url, nextUrl);

			} else {
				loadNext(url, nextUrl);
			}

		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("异常   要加载的： " + url);
			System.out.println("异常   上一页的： " + preUrl);
			System.exit(0);
			return;
		}
	}

	private static void loadNext(String url, String nextUrl) {
		if (nextPagerCount > 1) {// 已经加载过一次
			nextPagerCount--;
			loadContentHtml(nextUrl, url);

		} else {
			loadNextUrl = nextUrl;
			System.out.println("正常   要加载的： " + url);
			System.out.println(nextUrl);

			// 修改当前文件
			File fileNext = new File(
					"D:\\WorkSpace\\test\\Jsoup\\src\\jsoup_test\\GetJoke.java");
			
			if(fileNext.exists()){
				try {
				// LogUtils.d("路径--> " + FileUtils.getExternalStoragePath() +
				// "exception.info");
				

				BufferedReader in = new BufferedReader(new InputStreamReader(
						new FileInputStream(fileNext), "gbk"));

				StringBuffer stringBuffer = new StringBuffer();

				String line = null;
				while ((line = in.readLine()) != null) {
					stringBuffer.append(line + "\n");
				}
				in.close();

				
				Writer out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(fileNext), "gbk"));
				
				if (stringBuffer.toString().length() > 0) {
					String contentCode = stringBuffer.toString();
					String rStr = contentCode.substring(contentCode.indexOf("\"") + 1, contentCode.indexOf(".asp\";") + 4);
					contentCode = contentCode.replace(rStr, nextUrl);
					rStr = contentCode.substring(contentCode.indexOf("preCreateUrl=\"") + 14, contentCode.indexOf(".asp\";;") + 4);
					contentCode = contentCode.replace(rStr, url);
					System.out.println(rStr);
					
					
					out.write(contentCode);
					out.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			}
			
			System.exit(0);
			return;
		}
	}

	// 获取url列表
	private static void getNewUrl() {

		String rootUrl = "http://wap.jokeji.cn/";
		try {
			Document document = Jsoup.connect(rootUrl).get();
			for (int j = 4; j >= 1; j--) {
				Elements els = document.select("div#tabBox" + j
						+ ">div.bd>div.cont");
				for (int i = 0; i < 3; i++) {
					Element element = els.get(i);
					Elements bs = element.select("b");
					for (Element b : bs) {
						// new RootUrlBean(b.select("a").first().absUrl("href"),
						// b.text())

						// writeFile(b.select("a").first().absUrl("href")
						// + "  " + b.text());
						// File file = new File("F:\\test\\11");
						// if (!file.exists()) {
						// file.mkdirs();
						// }
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void writeFile(String str, String fileName) {
		try {
			// LogUtils.d("路径--> " + FileUtils.getExternalStoragePath() +
			// "exception.info");
			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("F:\\test\\" + fileName), "UTF-8"));
			out.write(str);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
