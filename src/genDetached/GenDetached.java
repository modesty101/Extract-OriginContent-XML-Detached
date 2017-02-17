package genDetached;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Collections;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * This is a simple example of generating a Detached XML Signature using the JSR
 * 105 API. The resulting signature will look like (key and signature values
 * will be different):
 *
 * <pre>
 * <code>
 * <Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
 *   <SignedInfo>
 *     <CanonicalizationMethod Algorithm=
"http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/>
 *     <SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#dsa-sha1"/>
 *     <Reference URI="http://www.w3.org/TR/xml-stylesheet">
 *       <DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
 *       <DigestValue>60NvZvtdTB+7UnlLp/H24p7h4bs=</DigestValue>
 *     </Reference>
 *   </SignedInfo>
 *   <SignatureValue>
 *     DpEylhQoiUKBoKWmYfajXO7LZxiDYgVtUtCNyTgwZgoChzorA2nhkQ==
 *   </SignatureValue>
 *   <KeyInfo>
 *     <KeyValue>
 *       <DSAKeyValue>
 *         <P>
 *           rFto8uPQM6y34FLPmDh40BLJ1rVrC8VeRquuhPZ6jYNFkQuwxnu/wCvIAMhukPBL
 *           FET8bJf/b2ef+oqxZajEb+88zlZoyG8g/wMfDBHTxz+CnowLahnCCTYBp5kt7G8q
 *           UobJuvjylwj1st7V9Lsu03iXMXtbiriUjFa5gURasN8=
 *         </P>
 *         <Q>
 *           kEjAFpCe4lcUOdwphpzf+tBaUds=
 *         </Q>
 *         <G>
 *           oe14R2OtyKx+s+60O5BRNMOYpIg2TU/f15N3bsDErKOWtKXeNK9FS7dWStreDxo2
 *           SSgOonqAd4FuJ/4uva7GgNL4ULIqY7E+mW5iwJ7n/WTELh98mEocsLXkNh24HcH4
 *           BZfSCTruuzmCyjdV1KSqX/Eux04HfCWYmdxN3SQ/qqw=
 *         </G>
 *         <Y>
 *           pA5NnZvcd574WRXuOA7ZfC/7Lqt4cB0MRLWtHubtJoVOao9ib5ry4rTk0r6ddnOv
 *           AIGKktutzK3ymvKleS3DOrwZQgJ+/BDWDW8kO9R66o6rdjiSobBi/0c2V1+dkqOg
 *           jFmKz395mvCOZGhC7fqAVhHat2EjGPMfgSZyABa7+1k=
 *         </Y>
 *       </DSAKeyValue>
 *     </KeyValue>
 *   </KeyInfo>
 * </Signature>
 * </code>
 * </pre>
 */

/**
 * XML-Detached-Signature ���� ����
 * 
 * @source <a href=
 *         "https://docs.oracle.com/javase/7/docs/technotes/guides/security/xmldsig/GenDetached.java"/>
 * @author <a href="mailto:modesty101@daum.net">�赿��</a>
 * @since 2017
 */
public class GenDetached {

	public static void main(String args) throws Exception {
		/* �α� ���� ���� */
		File file = new File("log.txt");
		PrintStream printStream = new PrintStream(new FileOutputStream(file));
		System.setOut(printStream);

		File f = new File(args);
		URI u = f.toURI();
		System.out.println("File to URI : " + u);
		String URI = new String(u.toString());
		
		// XMLSignatureFactoy ��ü�� DOM �ν��Ͻ��� �޾ƿ´�.
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

		/*
		 *  ���۷����� �����Ѵ�. �ܺ� URI�� SHA1���� ��������Ʈ�� ���̴�.
		 *  How to create URI Path ? ==> http://modesty101.tistory.com/137
		 */
		Reference ref = fac.newReference(URI, fac.newDigestMethod(DigestMethod.SHA1, null));

		// SignedInfo ��ü ����, xmlns �Ӽ��� ���Եȴ�.
		SignedInfo si = fac.newSignedInfo(
				fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
						(C14NMethodParameterSpec) null),
				fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));

		// RSA Ű ����
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(512);
		KeyPair kp = kpg.generateKeyPair();

		// RSA ����Ű�� KeyValue�� �����Ѵ�.
		KeyInfoFactory kif = fac.getKeyInfoFactory();
		KeyValue kv = kif.newKeyValue(kp.getPublic());

		// KeyInfo�� KeyValue�� �߰��Ѵ�.
		KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

		// XML �ñ״�ó ����(���� ���� ��������)
		XMLSignature signature = fac.newXMLSignature(si, ki);

		// XML �ñ״�ó�� ����� �� ������ �����Ѵ�.
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true); // must be set
		Document doc = dbf.newDocumentBuilder().newDocument();


		/*
		 * DOMSignContext ����, RSA ����Ű�� ������ �����Ѵ�.
		 * ���⼱, ������ ��Ʈ�� �߰��ȴ�. (Detached �ñ״�ó�� Ư¡)
		 */
		DOMSignContext signContext = new DOMSignContext(kp.getPrivate(), doc);

		/*
		 * detached �ñ״�ó�� �����Ѵ�. DOM ������ XML �ñ״�ó�� ������ ���̴�.
		 * (�޼ҵ� ���������� ���� �ߴٸ�..)
		 */
		signature.sign(signContext);

		// ����� .xml ���������Ѵ�.
		OutputStream os;
		os = new FileOutputStream(args + ".xml");

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		trans.transform(new DOMSource(doc), new StreamResult(os));
	}
}