package top.xuyx.seafood.weixin.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiFilter {
	/**
	 * 检测是否有emoji字符
	 * 
	 * @param source
	 *            需要判断的字符串
	 * @return 一旦含有就抛出
	 */
	public static boolean containsEmoji(String source) {
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (!notisEmojiCharacter(codePoint)) {
				// 判断确认有表情字符
				return true;
			}
		}
		return false;
	}

	/**
	 * 非emoji表情字符判断
	 * 
	 * @param codePoint
	 * @return
	 */
	private static boolean notisEmojiCharacter(char codePoint) {
		return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
				|| ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	}

	/**
	 * 过滤emoji 或者 其他非文字类型的字符
	 * 
	 * @param source
	 *            需要过滤的字符串
	 * @return
	 */
	public static String filterEmoji(String source) {
		if (source != null) {
			Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
			Matcher emojiMatcher = emoji.matcher(source);
			if (emojiMatcher.find()) {
				source = emojiMatcher.replaceAll("*");
				return source;
			}
			return source;
		}
		return source;
	}

}