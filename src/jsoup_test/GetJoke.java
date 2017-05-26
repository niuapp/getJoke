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
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetJoke {

	private static String loadNextUrl;
	private static int nextPagerCount = 33;

	// private static int count = 33;
	private static int startCount = 1;

	private static String skipUrl = "";
	private static String skipNextUrl = "";

	public static void loadContentHtml(String url, String preUrl) {
		final String tempUrl = url;
		final String tempPreUrl = preUrl;

		System.out.println("开始： " + startCount++);

		try {
			Thread.sleep(150);

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
				nextUrl = document.select("div.page_next>ul>li.next")
						.select("a").first().absUrl("href");
			}

			// 下一页url

			// 如果有图片，就跳过这页
			if (!el.toString().contains("<img")) {

				// 判断有没有跳过的页面，如果有 就重写其下一页信息，然后把跳过信息重置
				if (skipUrl != null && skipUrl.length() > 0
						&& skipNextUrl != null && skipNextUrl.length() > 0) {

					preUrl = skipUrl;

					// 如果发现跳过，就重写上一页文件，把 其下一页的url 改为nextUrl 对应的文件

					// 读 写 根据preUrl得到上一页 根据 url 得到对应链接，修改为 nextUrl

					File resetFile = new File(UrlP.outPath + "\\"
							+ preUrl.substring(preUrl.lastIndexOf("/") + 1)
							+ ".html");

					if (resetFile.exists()) {
						try {
							// LogUtils.d("路径--> " +
							// FileUtils.getExternalStoragePath() +
							// "exception.info");

							BufferedReader in = new BufferedReader(
									new InputStreamReader(new FileInputStream(
											resetFile), "utf-8"));

							StringBuffer stringBuffer = new StringBuffer();

							String line = null;
							while ((line = in.readLine()) != null) {
								stringBuffer.append(line + "\n");
							}
							in.close();

							Writer out = new BufferedWriter(
									new OutputStreamWriter(
											new FileOutputStream(resetFile),
											"utf-8"));

							if (stringBuffer.toString().length() > 0) {
								String contentCode = stringBuffer.toString();

								// 替换
								contentCode = contentCode.replace(
										skipNextUrl.substring(skipNextUrl
												.lastIndexOf("/") + 1)
												+ ".html",
										url.substring(url.lastIndexOf("/") + 1)
												+ ".html");

								out.write(contentCode);
								out.close();
							}

						} catch (IOException e) {
							e.printStackTrace();
						}

					}

					skipUrl = "";// 重置
					skipNextUrl = "";
				}

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
				// 如果需要跳过，就设置跳过标记，如果有标记，就直接跳过
				if ("".equals(skipUrl) && "".equals(skipNextUrl)) {
					skipUrl = preUrl;
					skipNextUrl = url;
				}

				loadNext(preUrl, nextUrl);// 不包含当前url
			}

		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("异常   要加载的： " + url);
			System.out.println("异常   上一页的： " + preUrl);

			// 通过更改 对应txt来停止脚本
			try {
				File endFile = new File(UrlP.endFilePath);
				Writer out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(endFile), "UTF-8"));
				out.write("error_end");
				out.close();

				Runtime.getRuntime().exec(
						new String[] { "wscript", UrlP.vbsPath });
			} catch (IOException e1) {
				e1.printStackTrace();
			}

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
			File fileNext = new File("C:\\joke\\joke_url.txt");

			if (fileNext.exists()) {
				try {

					Writer out = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(fileNext), "gbk"));

					out.write("nextUrl ￥￥￥ " + nextUrl 
							+ "\npreUrl ￥￥￥ " + url
							+ "\noutPath ￥￥￥ " + UrlP.outPath
							+ "\nvbsPath ￥￥￥ " + UrlP.vbsPath
							+ "\nendFilePath ￥￥￥ " + UrlP.endFilePath
							);
					out.close();

					// Runtime.getRuntime().exec(new String[] { "wscript",
					// UrlP.vbsPath});

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
					new FileOutputStream(UrlP.outPath + "\\" + fileName),
					"UTF-8"));
			out.write(str);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
