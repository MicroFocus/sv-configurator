/*
 *  Certain versions of software and/or documents ("Material") accessible here may contain branding from
 *  Hewlett-Packard Company (now HP Inc.) and Hewlett Packard Enterprise Company.  As of September 1, 2017,
 *  the Material is now offered by Micro Focus, a separately owned and operated company.  Any reference to the HP
 *  and Hewlett Packard Enterprise/HPE marks is historical in nature, and the HP and Hewlett Packard Enterprise/HPE
 *  marks are the property of their respective owners.
 * __________________________________________________________________
 * MIT License
 *
 * Copyright (c) 2012-2018 Micro Focus or one of its affiliates.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are set forth in the express warranty statements
 * accompanying such products and services. Nothing herein should be construed as
 * constituting an additional warranty. Micro Focus shall not be liable for technical
 * or editorial errors or omissions contained herein.
 * The information contained herein is subject to change without notice.
 * __________________________________________________________________
 *
 */
package com.microfocus.sv.svconfigurator.core.impl.encryption;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.getInstance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.microfocus.sv.svconfigurator.core.encryption.EncryptionMetadata;
import com.microfocus.sv.svconfigurator.core.encryption.EncryptionVersions;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.microfocus.sv.svconfigurator.core.impl.exception.SVCParseException;
import com.microfocus.sv.svconfigurator.util.XmlUtils;

public class ProjectElementDecryptorImpl implements ProjectElementDecryptor {

    private EncryptionMetadata encryptionMetadata;

    private Pattern nsmPattern = Pattern.compile("(?:xmlns\\()?(.*)$?(?:\\)xpath)??");
    private Pattern xPathPattern = Pattern.compile(".*xpath\\((.*)\\)");

    private static String aesSalt = "b5474521-21c5-4772-a8e1-1f2208e0dee2";

    public ProjectElementDecryptorImpl(EncryptionMetadata encryptionMetadata) {
        this.encryptionMetadata = encryptionMetadata;
    }

    @Override
    public byte[] decodeStream(InputStream stream, String password) throws SVCParseException {
        try {
            Document doc = XmlUtils.createDoc(stream);
            XPathFactory xPathFactory = XPathFactory.newInstance();

            List<EncryptedNode> encryptedNodes = encryptionMetadata.getEncryptedNodes();
            for (EncryptedNode encryptedNode : encryptedNodes) {
                XPath xPath = xPathFactory.newXPath();
                xPath.setNamespaceContext(new EncryptedNodesNamespaceContext(encryptedNode));
                String xPathStr = getXPath(encryptedNode);
                NodeList nl = (NodeList) xPath.evaluate(xPathStr, doc, XPathConstants.NODESET);
                for (int i = 0; i < nl.getLength(); i++) {
                    Node n = nl.item(i);
                    if (n instanceof Element) {
                        decryptElementContent(doc, (Element) n, password, encryptedNode.getTargetName());
                    } else if (n instanceof Attr) {
                        decryptAttributeValue(password, (Attr) n, encryptedNode.getTargetName());
                    }
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(baos);
            transformer.transform(domSource, streamResult);

            return baos.toByteArray();
        } catch (IOException e) {
            throw new SVCParseException("Unable to decrypt project content", e);
        } catch (XPathExpressionException e) {
            throw new SVCParseException("Unable to decrypt project content", e);
        } catch (TransformerConfigurationException e) {
            throw new SVCParseException("Unable to decrypt project content", e);
        } catch (TransformerException e) {
            throw new SVCParseException("Unable to decrypt project content", e);
        }
    }

    private void decryptAttributeValue(String password, Attr oldAttr, String targetName) throws SVCParseException {
        String decryptedValue = decryptString(oldAttr.getValue(), password);
        Attr attr = oldAttr.getOwnerDocument().createAttribute(targetName);
        attr.setValue(decryptedValue);
        Element oarentElement = oldAttr.getOwnerElement();
        oarentElement.removeAttributeNode(oldAttr);
        oarentElement.setAttributeNode(attr);
    }

    private void decryptElementContent(Document doc, Element oldElement, String password, String targetName) throws SVCParseException {
        Element newElement = doc.createElement(targetName);
        NamedNodeMap attrs = oldElement.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Attr attr = (Attr) attrs.item(i);
            if (attr.getNamespaceURI() != null) {
                newElement.setAttributeNS(attr.getNamespaceURI(), attr.getName(), attr.getValue());
            }
        }
        newElement.setTextContent(decryptString(oldElement.getTextContent(), password));
        Node parentNode = oldElement.getParentNode();
        parentNode.removeChild(oldElement);
        parentNode.appendChild(newElement);
    }

    public String decryptString(String text, String password) throws SVCParseException {
        try {
            byte[] encryptedValue = Base64.decodeBase64(text.getBytes("UTF-8"));

            PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), aesSalt.getBytes("UTF-8"), 1000, 2*8*48);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey sk = factory.generateSecret(keySpec);

            byte[] encoded = sk.getEncoded();
            if (this.getEncryptionVersion().equals(EncryptionVersions.EncryptionVersion370)){
                encoded = sk.getEncoded();
            }

            int shift = 0;
            String version = this.getEncryptionVersion();
            if (EncryptionVersions.EncryptionVersion370.equals(version)) {
                // for 3.70, 2nd 48 bytes are used instead of beginning
                shift = 48;
            }

            byte[] secret = Arrays.copyOfRange(encoded, shift + 0, shift + 32);
            byte[] iv = Arrays.copyOfRange(encoded, shift + 32, shift + 48);

            Key skeySpec = new SecretKeySpec(secret, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html

            Cipher cipher = getInstance("AES/CBC/NoPadding");
            //Cipher cipher = getInstance("AES/CBC/PKCS7Padding");
            //Cipher cipher = getInstance("AES/CBC/PKCS5Padding");
            cipher.init(DECRYPT_MODE, skeySpec, ivParameterSpec);

            byte[] decrypted = cipher.doFinal(encryptedValue);
            String decrString = new String(decrypted, "UTF-8");

            MessageDigest md = this.getMessageDigest();
            int digestLength = md.getDigestLength();


            // remove PKCS7 padding
            int paddingLength = decrypted[decrypted.length - 1];
            decrypted = Arrays.copyOfRange(decrypted, 0, decrypted.length - paddingLength);

            // split data and hash
            byte[] data = Arrays.copyOfRange(decrypted, 0, decrypted.length - digestLength);
            byte[] hash = Arrays.copyOfRange(decrypted, decrypted.length - digestLength, decrypted.length);

            // compute data hash
            byte[] dataHash = md.digest(data);

            if (Arrays.equals(hash, dataHash)) {
                return new String(data, "UTF-8");
            }
            throw new BadPaddingException();
        } catch (Exception e) {
            throw new SVCParseException("Unable to decrypt value", e);
        }
    }

    private String getEncryptionVersion(){
        if (this.encryptionMetadata != null){
            return this.encryptionMetadata.getEncryptionVersion();
        }
        return EncryptionVersions.EncryptionVersionBefore370;
    }

    private MessageDigest getMessageDigest() throws SVCParseException, NoSuchAlgorithmException {
        String encryptionVersion = "1";
        if (this.encryptionMetadata != null && this.encryptionMetadata.hasEncryptionVersion()) {
            encryptionVersion = this.encryptionMetadata.getEncryptionVersion();
        }
        String algorithm = "";
        if (EncryptionVersions.EncryptionVersionBefore370.equals(encryptionVersion)){
            algorithm = "md5";
        }
        if (EncryptionVersions.EncryptionVersion370.equals(encryptionVersion)){
            algorithm = "sha-256";
        }

        if (algorithm == null || algorithm.length() == 0){
            throw new SVCParseException("Encryption version not supported. Version: " + encryptionVersion);
        }

        return MessageDigest.getInstance(algorithm);
    }

    private List<String[]> getNsMapping(EncryptedNode encryptedNode) {
        List<String[]> retVal = new ArrayList<String[]>();
        String[] nsParts = encryptedNode.getxPointer().split(",");
        for (String nsPart : nsParts) {
            if (nsPart.startsWith("xmlns(")) {
                nsPart = nsPart.substring("xmlns(".length(), nsPart.length());
            }
            int index = nsPart.indexOf(")xpath");
            if (index > -1) {
                nsPart = nsPart.substring(0, index);
            }
            index = nsPart.indexOf("=");
            if (index != 1) {
                String prefix = nsPart.substring(0, index);
                String ns = nsPart.substring(index + 1, nsPart.length());
                String[] ns2Prefix = new String[2];
                ns2Prefix[0] = prefix.trim();
                ns2Prefix[1] = ns.trim();
                retVal.add(ns2Prefix);
            }
        }
        return retVal;
    }

    private String getXPath(EncryptedNode encryptedNode) {
        Matcher m = xPathPattern.matcher(encryptedNode.getxPointer());
        if (!m.find()) {
            return null;
        }
        String xp = m.group(1);
        return xp;
    }

    private class EncryptedNodesNamespaceContext implements NamespaceContext {
        private Map<String, String> prefix2NsMap = new HashMap<String, String>();
        private Map<String, String> ns2PrefixMap = new HashMap<String, String>();

        public EncryptedNodesNamespaceContext(EncryptedNode encryptedNode) {
            if (encryptedNode == null) {
                return;
            }
            List<String[]> nsMapping = getNsMapping(encryptedNode);
            for (String[] nsm : nsMapping) {
                if (!prefix2NsMap.containsKey(nsm[0])) {
                    prefix2NsMap.put(nsm[0], nsm[1]);
                }
            }
        }

        @Override
        public String getNamespaceURI(String prefix) {
            return prefix2NsMap.get(prefix);
        }

        @Override
        public String getPrefix(String namespaceURI) {
            return ns2PrefixMap.get(namespaceURI);
        }

        @Override
        public Iterator getPrefixes(String namespaceURI) {
            return prefix2NsMap.keySet().iterator();
        }
    }
}

