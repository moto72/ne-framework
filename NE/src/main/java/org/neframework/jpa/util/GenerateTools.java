package org.neframework.jpa.util;

import java.util.Random;
import java.util.UUID;

/**
 * 生成生产工具
 * 
 * @author zhangwei
 * 
 * 
 * @time 2012-12-28 下午02:59:36
 * 
 */
public class GenerateTools {
	/**
	 * 72位随机散列数组
	 */
	static char strRandom72[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
			'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', '!', '@', '#', '$', '-', '+', '[', ']', '{', '}' };
	/**
	 * 62位随机散列集合
	 */
	static char strRandom62[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
			'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9' };

	/**
	 * 生产UUID
	 * 
	 * @return
	 */
	public static String getUUID() {
		return java.util.UUID.randomUUID().toString();
	}

	public static String compressedUUID(UUID uuid) {
		byte[] byUuid = new byte[16];
		long least = uuid.getLeastSignificantBits();
		long most = uuid.getMostSignificantBits();
		long2bytes(most, byUuid, 0);
		long2bytes(least, byUuid, 8);
		String compressUUID = Base58.encode(byUuid);
		return compressUUID;
	}

	protected static void long2bytes(long value, byte[] bytes, int offset) {
		for (int i = 7; i > -1; i--) {
			bytes[offset++] = (byte) ((value >> 8 * i) & 0xFF);
		}
	}

	/**
	 * 根据长度参数生成密码（随机数）
	 * 
	 * @param length
	 * @return
	 */
	public static String createPass(int length) {
		if (length > strRandom62.length) {
			length = strRandom62.length;
		}
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = strRandom62[new Random().nextInt(strRandom62.length)];
		}
		return new String(chars);
	}

	/**
	 * 默认生成8位密码
	 * 
	 * @return
	 */
	public static String createPass() {
		return GenerateTools.createPass(8);
	}

	/**
	 * 获取N位随机数
	 * 
	 * @param b
	 * @return
	 */
	public static String getRomNum(int b) {
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b; i++) {
			sb.append(random.nextInt(10));
		}
		return sb.toString();
	}

	public static String getBase58ID() {
		return compressedUUID(java.util.UUID.randomUUID());
	}

	public final static char[] digits = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
			'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
			'X', 'Y', 'Z', '|', '#' };

	public static String Dec2AnyHex(int dec, int shift) {
		char[] buf = new char[32];
		int charPos = 32;
		int radix = 1 << shift;
		int mask = radix - 1;
		do {
			buf[--charPos] = digits[dec & mask];
			dec >>>= shift;
		} while (dec != 0);
		return new String(buf, charPos, (32 - charPos));
	}

	public static Integer AnyHex2Dec(String anyHex, int shift) {
		char[] s = anyHex.toCharArray();
		String digitsString = new String(digits);
		int Num = 0;
		for (int i = s.length; i > 0; i--) {
			int j = digitsString.indexOf(s[i - 1]);
			Num += j * Math.pow(1 << shift, s.length - i);
		}
		return Num;
	}

	public static synchronized String getShorId() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Dec2AnyHex((int) System.currentTimeMillis(), 6);
	}
}
