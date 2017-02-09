package genDetached;

import java.io.File;
import java.io.FileInputStream;
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

	public static String stringToHex(String s) {
		String result = "";

		for (int i = 0; i < s.length(); i++) {
			result += String.format("%02X ", (int) s.charAt(i));
		}

		return result;
	}

	public static boolean main(String args) throws Exception {
		FileInputStream fis = new FileInputStream(new File(args));

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
		String encodingData = Base64.encodeBase64String(byteData);
		System.out.println("Base64 Encoding format : " + encodingData);

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
					// for (int j = 0; j < nl.getLength(); j++) {
					// nd = nl.item(0);
					// String name = nd.getTextContent();
					// System.out.println(name);
					// }
				}
				System.out.println("");
			}
		}

		String digestValue = new String(nd.getTextContent());
		boolean flag = false;
		if (digestValue.equals(encodingData)) {
			flag = true;
			System.out.println(digestValue + " 는 " + encodingData + " 와 같다 :" + flag);
		} else {
			System.out.println(digestValue + " != " + encodingData + ":" + flag);
		}
		
		return flag;
		/*
		 * convert the byte to hex format method 2 StringBuffer hexString = new
		 * StringBuffer(); for (int i = 0; i < byteData.length; i++) { String
		 * hex = Integer.toHexString(0xff & byteData[i]); if (hex.length() == 1)
		 * hexString.append('0'); hexString.append(hex); }
		 * System.out.println("Hex format : " + hexString.toString());
		 */
	}
}
