/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package france.alsace.fl.passwordmanager;

import java.io.Serializable;
import java.util.Date;

/**
 * Ciphered infos (web site, id, pwd and date of last pwd change)
 * @author Florent
 */
public class MyCipheredInfo implements Serializable {
    private String webSite;
    private String id;
    private CipheredString password;
    private Date lastChange;

    /**
     * Constructor
     * @param webSite the web site
     * @param id the id
     * @param password the pwd
     * @param lastChange the date of last pwd change
     */
    public MyCipheredInfo(String webSite, String id, CipheredString password, Date lastChange) {
        this.webSite = webSite;
        this.id = id;
        this.password = password;
        this.lastChange = lastChange;
    }
    
    /**
     * Get the web site
     * @return the web site
     */
    public String getWebSite() {
        return webSite;
    }

    /**
     * Set the web site
     * @param webSite the web site
     */
    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    /**
     * Get the id
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the id
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the chipered pwd
     * @return the ciphered pwd
     */
    public CipheredString getPassword() {
        return password;
    }

    /** 
     * Set the ciphered pwd
     * @param password the ciphered pwd
     */
    public void setPassword(CipheredString password) {
        this.password = password;
    }

    /**
     * Get the date of last pwd change
     * @return the date of last pwd change
     */
    public Date getLastChange() {
        return lastChange;
    }

    /**
     * Set the date of last pwd change
     * @param lastChange the date of last pwd change
     */
    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }
    
    /**
     * @Override the toString method, return the web site as a String
     * @return the web site
     */
    @Override
    public String toString() {
        return webSite;
    }
}
