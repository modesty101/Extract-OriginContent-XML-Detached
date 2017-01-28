package genDetached;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

/**
 * Detached 시그니처 검증(DigestValue)
 *  - XML 파일의 경우, <xml ....> 부분을 삭제하고 한다.
 *  - 일반 파일은 그대로 사용하면 된다.
 *  
 * @author <a href="mailto:modesty101@daum.net">김동규</a>
 * @since 2017
 */
public class ExtractDetached {
	
	public static String stringToHex(String s) {
		String result = "";

		for (int i = 0; i < s.length(); i++) {
			result += String.format("%02X ", (int) s.charAt(i));
		}

		return result;
	}

	public static void main(String[] args) throws Exception {
		FileInputStream fis = new FileInputStream(new File("test.txt"));
		
		System.out.println("Total file size : +" + fis.available());
		
		byte[] readBuf = new byte[fis.available()];
		while (fis.read(readBuf) != -1) {
		}
		
		String re = new String(readBuf);
		System.out.println(re);
		
		fis.close();
		
		System.out.println("Hex : ");
		System.out.println(stringToHex(re));

		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(re.getBytes());

		byte byteData[] = md.digest();

		// convert the byte to hex format method 1
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		System.out.println();
		System.out.println("Hex format : " + sb.toString());
		System.out.println("Base64 Encoding format : " + Base64.encodeBase64String(byteData));
		
		/*
		 convert the byte to hex format method 2
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		System.out.println("Hex format : " + hexString.toString());
		*/
	}
}
