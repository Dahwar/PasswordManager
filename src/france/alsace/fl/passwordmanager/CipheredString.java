/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package france.alsace.fl.passwordmanager;

import java.io.Serializable;

/**
 *
 * @author Florent
 */
public class CipheredString implements Serializable {
    
    private byte[] stringCiphered;
    private byte[] salt;
    private byte[] iv;

    public CipheredString(byte[] stringCiphered, byte[] salt, byte[] iv) {
        this.stringCiphered = stringCiphered;
        this.salt = salt;
        this.iv = iv;
    }
    
    public byte[] getStringCiphered() {
        return stringCiphered;
    }

    public void setStringCiphered(byte[] stringCiphered) {
        this.stringCiphered = stringCiphered;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }
    
    
}
