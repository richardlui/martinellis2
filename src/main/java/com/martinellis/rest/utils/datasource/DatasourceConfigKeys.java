package com.martinellis.rest.utils.datasource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;

public class DatasourceConfigKeys {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String SECRET_KEY_ALGORITHM = "PBEWithMD5AndDES";
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,};
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private String pvtKey;
    private String jdbcDriver;

    public void setUrl(String url) {
        this.dbUrl = url;
    }

    public void setUser(String user) {
        this.dbUser = user;
    }

    public void setPassword(String password) {
        this.dbPassword = password;
    }

    public void setPvtKey(String pvtKey) {
        this.pvtKey = pvtKey;
    }
    
    public void setJdbcDriver(String jdbcDriver) {
    	this.jdbcDriver = jdbcDriver;
    }

    public String getPvtKey() {
        return pvtKey;
    }

    public String getUser() {
        return this.dbUser;
    }

    public String getPassword() {
        return this.dbPassword;
    }

    public String getUrl() {
        return this.dbUrl;
    }
    
    public String getJdbcDriver() {
    	return this.jdbcDriver;
    }

    @SuppressWarnings("resource")
    private InputStream getPathAsStream(String propertyFileLocation) throws FileNotFoundException {
        InputStream stream;
        File file = new File(propertyFileLocation);
        
        if (!file.exists()) {
            logger.debug("loading {} from classpath", propertyFileLocation);
            stream = getClass().getResourceAsStream("/" + propertyFileLocation);
        } else {
            logger.debug("loading {} from file system", propertyFileLocation);
            stream = new BufferedInputStream(new FileInputStream(file));
        }
        
        return stream;
    }

    public void decryptKeys(String privateKeyLoc, String privateConfigLocation) throws Exception {
        InputStream keyStream = null;
        InputStream configStream = null;
        try {
            keyStream = getPathAsStream(privateKeyLoc);
            configStream = getPathAsStream(privateConfigLocation);

            BufferedReader br = new BufferedReader(new InputStreamReader(keyStream));
            String currLine = br.readLine();
            setPvtKey(currLine);
            br.close();
            br = new BufferedReader(new InputStreamReader(configStream));
            int i = 0;

            while ((currLine = br.readLine()) != null) {
                switch (i) {
                case 0:
                	setUrl(currLine);
                	break;
                case 1:
                	//setUser(decrypt(currLine));
                    setUser(currLine);
                	break;
                case 2:
                	//setPassword(decrypt(currLine));
                    setPassword(currLine);
                	break;
                case 3:
                	setJdbcDriver(currLine);
                	break;
                }
                i++;
            }
            
            if (this.jdbcDriver == null) {
            	this.setJdbcDriver("com.vertica.jdbc.Driver");
            }
        } catch (IOException ex) {
            logger.error("IO Exception during decode. Probably because private key and config could not be found", ex);
            throw ex;
        } finally {
            if (configStream != null) {
                try {
                    configStream.close();
                } catch (IOException e) {
                    logger.error("could not close property file " + privateConfigLocation, e);
                }
            }
            if (keyStream != null) {
                try {
                    keyStream.close();
                } catch (IOException e) {
                    logger.error("could not close property file " + privateKeyLoc, e);
                }
            }
        }
    }

    private String decrypt(String property) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(getPvtKey().toCharArray()));
        Cipher pbeCipher = Cipher.getInstance(SECRET_KEY_ALGORITHM);
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        return new BASE64Decoder().decodeBuffer(property);
    }

    public static void main(String[] args) throws Exception {
        String keyLoc = "/Users/janand/security/pvtkey.dat";
        String encryptKeyLoc = "/Users/janand/security/keys.dat";
        DatasourceConfigKeys dck = new DatasourceConfigKeys();
        dck.decryptKeys(keyLoc, encryptKeyLoc);
        System.out.println("URL: " + dck.getUrl());
        System.out.println("User: " + dck.getUser());
        System.out.println("Password: " + dck.getPassword());
    }
}
