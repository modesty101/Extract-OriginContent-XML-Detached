package genDetached;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Detached 시그니처 검증(DigestValue) - XML 파일의 경우, <xml ....> 부분을 삭제하고 한다. - 일반 파일은
 * 그대로 사용하면 된다.
 * 
 * @author <a href="mailto:modesty101@daum.net">김동규</a>
 * @since 2017
 */
public class ExtractDetached {

	/**
	 * 문자열을 헥스값으로 변환한다.
	 * 
	 * @param s
	 * @return result
	 */
	public static String stringToHex(String s) {
		String result = "";

		for (int i = 0; i < s.length(); i++) {
			result += String.format("%02X ", (int) s.charAt(i));
		}

		return result;
	}

	/*
	 * convert the byte to hex format method 2 StringBuffer hexString = new
	 * StringBuffer(); for (int i = 0; i < byteData.length; i++) { String hex =
	 * Integer.toHexString(0xff & byteData[i]); if (hex.length() == 1)
	 * hexString.append('0'); hexString.append(hex); }
	 * System.out.println("Hex format : " + hexString.toString());
	 */
	/**
	 * 다이제스트값을 비교하여 검증한다.
	 * 
	 * @param encodingData
	 * @param value
	 * @return
	 */
	public static boolean logging(String encodingData, String value) {
		/* 검증 */
		String digestValue = new String(value);
		boolean flag = false;
		if (digestValue.equals(encodingData)) {
			flag = true;
			System.out.println(digestValue + " 는 " + encodingData + " 와 같다 :" + flag);
		} else {
			System.out.println(digestValue + " != " + encodingData + ":" + flag);
		}

		return flag;
	}

	/**
	 * 다이제스트 값을 추출한다.
	 * 
	 * @param args
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String beforeComparison(String args) throws Exception, IOException {
		File xmlFile = new File(args + ".xml");

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		doc.getDocumentElement().normalize();
		Node node = null;
		Node nd = null;
		NodeList nList = doc.getElementsByTagName("DigestValue");
		for (int i = 0; i < nList.getLength(); i++) {
			node = nList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				if (eElement.hasChildNodes()) {
					NodeList nl = node.getChildNodes();
					nd = nl.item(0);
					System.out.println(nd);
				}
				System.out.println("");
			}
		}

		return nd.getTextContent();
	}

	/**
	 * Base64 인코딩할 파일을 불러오고 저장합니다.
	 * 
	 * @param encodeFile
	 * @param encodingFile
	 * @param isChunked
	 * @return
	 * @throws IOException
	 */
	public static byte[] encodeFile(byte[] encodingFile, boolean isChunked) throws IOException {

		byte[] encodingImage = Base64.encodeBase64(encodingFile, isChunked);

		return encodingImage;
	}

	/**
	 * 파일을 불러옵니다.
	 * 
	 * @param encodeFile
	 * @return bytes
	 * @throws IOException
	 */
	public static byte[] loadFile(String fileName) throws IOException {
		File file = new File(fileName);
		int len = (int) file.length();

		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
		byte[] bytes = new byte[len];
		reader.read(bytes, 0, len);
		reader.close();

		return bytes;
	}

	public static boolean main(String args) throws Exception {
		byte[] bytes = loadFile(args);

		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(bytes);
		byte byteData[] = md.digest();

		// 바이트를 헥스값으로 변환한다.
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		byte[] encoded = encodeFile(byteData, false);
		String str = new String(encoded);

		/* 다이제스트 값 확인 */
		String value = beforeComparison(args);
		return logging(str, value);
	}
}
