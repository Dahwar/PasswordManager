package france.alsace.fl.passwordmanager;

import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encryptor class to cipher file
 * @author Florent
 */
public class Encryptor {
    
    private final int passwordIterations;
    private final int keySize;
    
    /**
     * Constructor
     * @param passwordIterations number of iterations
     * @param keySize size of the key 
     */
    public Encryptor(int passwordIterations, int keySize) {
        this.passwordIterations = passwordIterations;
        this.keySize = keySize;
    }
    
    /**
     * To cipher the file
     * @param stringClear the clear string
     * @param password the password
     * @return the ciphered file
     */
    public CipheredString cipher(String stringClear, String password) {
        try {
            //generate salt
            byte[] salt = generateSalt();
            
            //derive the key
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    passwordIterations,
                    keySize
            );
            
            SecretKey secretKey = factory.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
            
            //encrypt the message
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            
            return new CipheredString(cipher.doFinal(stringClear.getBytes()), salt, iv);
            
        } catch (NoSuchAlgorithmException 
                | InvalidKeySpecException 
                | NoSuchPaddingException 
                | InvalidKeyException 
                | InvalidParameterSpecException 
                | IllegalBlockSizeException 
                | BadPaddingException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * To generate the salt
     * @return the salt
     */
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        return bytes;
    }
}
