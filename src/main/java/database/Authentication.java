package database;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


/**
 * Hash passwords for storage, and test passwords against password tokens.
 *
 * Instances of this class can be used concurrently by multiple threads.
 *
 * @author erickson
 * @see <a href="http://stackoverflow.com/a/2861125/3474">StackOverflow</a>
 */
public final class Authentication {
    private byte[] DEFAULT_PUBLIC_KEY_BYTE = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCZiBk1ryhyVPKHcymNsgfSYpWRfITWAZFIoVYNsXkQA93oMpHUAp/jlPikstEQ4gyzpaf5RSLjl+Mt8gcczZ5FlgaCwhdu8AO1fi6X2+ttz52i9PyOrR+DdLz/smojMm5lhopH2o8ruVTmXk27fvbQEhKx+nRXEKeBK3vm2klSvQIDAQAB"
            .getBytes();
    private byte[] DEFAULT_PRIVATE_KEY_BYTE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJmIGTWvKHJU8odzKY2yB9JilZF8hNYBkUihVg2xeRAD3egykdQCn+OU+KSy0RDiDLOlp/lFIuOX4y3yBxzNnkWWBoLCF27wA7V+Lpfb623PnaL0/I6tH4N0vP+yaiMybmWGikfajyu5VOZeTbt+9tASErH6dFcQp4Ere+baSVK9AgMBAAECgYBRZgDHn5f4FeWHvpzXTP4soqvduIDM3YPjV/ZByEgBH6u1AaqjFskiZcb1uwBwzQgi7r8Bv4/hzpNZtqnisXkpXm1X2v0UAFdz+E4Hxfkbh34Akv5Nc9hYBsdEZy5H8St1cTkBY3wyika5ERrkNg17td3w2TTERsGh1NhIIsklAQJBAP/OAix6ioImdIarrvYQSTRBs/LLWgHrGBHgomxhjaihsjWw4H/5Z1bFYfGoTJBdrbuG3T39SMiNt5L/E2KnL+ECQQCZphpY1jTFfFJA3V7XQuoDmf/FLkUkWM1pyqxL+0yWOd8PfnOf+kNbfjU46QAsSISVoanrzGDRmTN7RCkpQq5dAkEAvSpK0TmNU9G8Ohqbt7szZ0FvIQzf8qs3kYDcm+lIKHvqnWm/muOEV9Z/J4WdmOStpQ+GbGDxgd3K8xlN7JZWAQJABDCGSWPV3oisSX5/xojZjq2VTtAbLImLWFYhapT4mqQLVAXTq0oMqiL/2oRn780uaFIXKwnQngZ6Y+MQ6EIcNQJABrk5vhnsERwMmpVO2/eNi/U35CcGGjmyMj/BNMbR80CO85CjEYiwTGvnXkoASR+SrpUGaVS56Ie8q1TgOhkVgQ=="
            .getBytes();
    private final int param= 1024;
    private byte[][] keyPairBytes=new byte[2][];

    /**
     * Each token produced by this class uses this identifier as a prefix.
     */
    public static final String ID = "$31$";

    /**
     * The minimum recommended cost, used by default
     */
    public static final int DEFAULT_COST = 16;

    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    private static final int SIZE = 128;

    private static final Pattern layout = Pattern.compile("\\$31\\$(\\d\\d?)\\$(.{43})");

    private final SecureRandom random;

    private final int cost;

    public static void main(String[] args) {
	Authentication auth = new Authentication();
	char[] pw= "3298fhvb92382355".toCharArray();
	char[] mail= "soysalsunan@gmail.com".toCharArray();

	String hash = auth.hash(pw);
	System.out.println(auth.hash(mail));
	System.out.println(hash);
	System.out.println(hash.length());

    Authentication au = new Authentication();
    System.out.println(au.authenticate(pw, hash));
    }

    public Authentication() {
        this(DEFAULT_COST);
    }

    /**
     * Create a password manager with a specified cost
     *
     * @param cost the exponential computational cost of hashing a password, 0 to 30
     */
    public Authentication(int cost) {
        iterations(cost); /* Validate cost */
        this.cost = cost;
        this.random = new SecureRandom();
    }

    private static int iterations(int cost) {
        if ((cost < 0) || (cost > 30))
            throw new IllegalArgumentException("cost: " + cost);
        return 1 << cost;
    }

    /**
     * Hash a password for storage.
     *
     * @return a secure authentication token to be stored for later authentication
     */
    public String hash(char[] password) {
        byte[] salt = new byte[SIZE / 8];
        random.nextBytes(salt);
        byte[] dk = pbkdf2(password, salt, 1 << cost);
        byte[] hash = new byte[salt.length + dk.length];
        System.arraycopy(salt, 0, hash, 0, salt.length);
        System.arraycopy(dk, 0, hash, salt.length, dk.length);
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        return ID + cost + '$' + enc.encodeToString(hash);
    }

    /**
     * Authenticate with a password and a stored password token.
     *
     * @return true if the password and token match
     */
    public boolean authenticate(char[] password, String token) {
        Matcher m = layout.matcher(token);
        if (!m.matches())
            throw new IllegalArgumentException("Invalid token format");
        int iterations = iterations(Integer.parseInt(m.group(1)));
        byte[] hash = Base64.getUrlDecoder().decode(m.group(2));
        byte[] salt = Arrays.copyOfRange(hash, 0, SIZE / 8);
        byte[] check = pbkdf2(password, salt, iterations);
        int zero = 0;
        for (int idx = 0; idx < check.length; ++idx)
            zero |= hash[salt.length + idx] ^ check[idx];
        return zero == 0;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
        KeySpec spec = new PBEKeySpec(password, salt, iterations, SIZE);
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
            return f.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Missing algorithm: " + ALGORITHM, ex);
        } catch (InvalidKeySpecException ex) {
            throw new IllegalStateException("Invalid SecretKeyFactory", ex);
        }
    }

    /*
    ENCRYPTION STAGE
     */
    //START OF ENCRYPTION
    public byte[] encrypt(byte[] text) throws Exception {
        byte[][] keyPairs= genKeyPair(this.param);
        return encrypt(keyPairs[0], text);
    }

    public byte[] encrypt(byte[] key, byte[] text) throws Exception {
        if (key == null) {
            key = this.DEFAULT_PRIVATE_KEY_BYTE;
        }
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key);
        KeyFactory factory = KeyFactory.getInstance("RSA", "SunRsaSign");
        PrivateKey privateKey = factory.generatePrivate(spec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        try {
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        } catch (InvalidKeyException e) {
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(),
                    rsaPrivateKey.getPrivateExponent());
            Key fakePublicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, fakePublicKey);
        }
        return cipher.doFinal(text);
    }

    public byte[][] genKeyPair(int keySize)
            throws NoSuchAlgorithmException, NoSuchProviderException {

        KeyPairGenerator gen=KeyPairGenerator.getInstance("RSA","SunRsaSign");
        gen.initialize(keySize, new SecureRandom());
        KeyPair pair=gen.generateKeyPair();

        keyPairBytes[0]=pair.getPrivate().getEncoded();
        keyPairBytes[1]=pair.getPublic().getEncoded();

        return keyPairBytes;
    }
    //END OF ENCRYPTION

    //START OF DECRYPTION
    public byte[] decrypt(byte[] publicKeyByte, byte[] cipherText) throws Exception {
        PublicKey publicKey=getPublicKey(publicKeyByte);
        return decrypt(publicKey, cipherText);
    }

    public byte[] decrypt(PublicKey publicKey, byte[] cipherText) throws Exception{
        Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
        try{
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
        }catch(InvalidKeyException e){
            RSAPublicKey rsaPublicKey=(RSAPublicKey) publicKey;
            RSAPrivateKeySpec spec=new RSAPrivateKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
            Key fakePrivateKey=KeyFactory.getInstance("RSA").generatePrivate(spec);
            cipher=Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, fakePrivateKey);
        }
        if(cipherText==null || cipherText.length==0){return cipherText;}

        return cipher.doFinal(cipherText);
    }

    public PublicKey getPublicKey(byte[] publicKeyByte){
        if(publicKeyByte==null ){
            publicKeyByte=this.DEFAULT_PUBLIC_KEY_BYTE;
        }

        try{
            X509EncodedKeySpec x509KeySpec=new X509EncodedKeySpec(publicKeyByte);
            KeyFactory keyFactory= KeyFactory.getInstance("RSA","SunRsaSign");
            return keyFactory.generatePublic(x509KeySpec);
        }catch(Exception e){
            throw new IllegalArgumentException("Failed to get public key", e);
        }
    }
    //END OF DECRYPTION

    //GETTERS
    public byte[] getPublicKey(){
        return keyPairBytes[1];
    }
    public byte[] getPrivateKey(){
        return keyPairBytes[0];
    }
    public byte[][] getKeyPairs(){
        return keyPairBytes;
    }
    //END OF GETTERS

}

