/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package france.alsace.fl.passwordmanager;

import java.io.Serializable;

/**
 * Ciphered string, with salt and initialization vector
 * @author Florent
 */
public class CipheredString implements Serializable {
    
    private byte[] stringCiphered;
    private byte[] salt;
    private byte[] iv;

    /**
     * Constructor
     * @param stringCiphered the ciphered string
     * @param salt the salt
     * @param iv the initialization vector
     */
    public CipheredString(byte[] stringCiphered, byte[] salt, byte[] iv) {
        this.stringCiphered = stringCiphered;
        this.salt = salt;
        this.iv = iv;
    }
    
    /**
     * Get the ciphered string
     * @return return the ciphered string
     */
    public byte[] getStringCiphered() {
        return stringCiphered;
    }

    /**
     * Set the ciphered string
     * @param stringCiphered the ciphered string
     */
    public void setStringCiphered(byte[] stringCiphered) {
        this.stringCiphered = stringCiphered;
    }

    /**
     * Get the salt
     * @return the salt
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * Set the salt
     * @param salt the salt
     */
    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    /**
     * Get the iv
     * @return the iv
     */
    public byte[] getIv() {
        return iv;
    }

    /**
     * Set the iv
     * @param iv the iv
     */
    public void setIv(byte[] iv) {
        this.iv = iv;
    }
}
