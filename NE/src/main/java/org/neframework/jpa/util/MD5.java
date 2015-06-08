package org.neframework.jpa.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD加密工具
 * 
 * @author zhangwei
 * 
 */
public class MD5 {
	private static String encrypte(String plainText, String algorithm) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(plainText.getBytes());
		byte[] b = md.digest();
		StringBuilder output = new StringBuilder(32);
		for (int i = 0; i < b.length; i++) {
			String temp = Integer.toHexString(b[i] & 0xff);
			if (temp.length() < 2) {
				output.append("0");
			}
			output.append(temp);
		}
		return output.toString();
	}

	public static String md5(String inStr) {
		if (inStr == null || inStr.equals("")) {
			return "";
		}
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		byte[] md5Bytes = md5.digest(byteArray);

		StringBuilder hexValue = new StringBuilder();

		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}

		return hexValue.toString();
	}

	public static void main(String[] args) {
		System.err.println("md5=" + MD5.md5("111111"));
		System.err.println("md5=" + MD5.encrypte("admin", "MD5"));
		System.err.println("md2=" + MD5.encrypte("admin", "MD2"));
		System.err.println("sha-512=" + MD5.encrypte("admin", "SHA-512"));
		System.err.println("sha-256=" + MD5.encrypte("admin", "SHA-256"));
		System.err.println("sha-1=" + MD5.encrypte("admin", "SHA-1"));

	}
}
