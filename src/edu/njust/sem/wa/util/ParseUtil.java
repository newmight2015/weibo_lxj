package edu.njust.sem.wa.util;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseUtil {
	private static Pattern pattern = Pattern.compile("\\d+");

	public static int getNum(String text) {
		Matcher matcher = pattern.matcher(text.trim());
		String numstr = null;
		int num = 0;
		if (matcher.find()) {
			numstr = matcher.group();
			if (numstr != null && numstr.length() > 0) {
				num = Integer.parseInt(numstr);
			}
		}
		return num;
	}

	public static void replaceImgWithAlt(Element e) {
		Elements imgs = e.getElementsByTag("img");
		// 把评论中出现的图片（表情等）替换为相应的文字
		for (Element imge : imgs) {
			String alt = imge.attr("alt");
			imge.html(alt);
		}
	}

	public static List<String> parseBloggerNameFromContent(String content) {
		List<String> names = new LinkedList<>();
		int index = content.indexOf('@');
		if (index != -1) {
			content = content.substring(index + 1);
			String[] arr = content.split("@");
			for (String str : arr) {
				str = str.trim();
				if (str.length() > 0) {
					String[] arrstr = str.split("\\s+|:|\\)|，|）|\\\\|,");
					if(arrstr.length>= 1){
						str = arrstr[0];
					}
					if (str.length() > 30) {
						System.out.println("字符长大于30————" + str);
					}
					names.add(str);
				}
			}
		}
		return names;
	}
}
