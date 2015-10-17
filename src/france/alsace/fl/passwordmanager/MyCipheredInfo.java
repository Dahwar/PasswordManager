/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package france.alsace.fl.passwordmanager;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Florent
 */
public class MyCipheredInfo implements Serializable {
    private String webSite;
    private String id;
    private CipheredString password;
    private Date lastChange;

    public MyCipheredInfo(String webSite, String id, CipheredString password, Date lastChange) {
        this.webSite = webSite;
        this.id = id;
        this.password = password;
        this.lastChange = lastChange;
    }
    
    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CipheredString getPassword() {
        return password;
    }

    public void setPassword(CipheredString password) {
        this.password = password;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }
    
    public String toString() {
        return webSite;
    }
}
