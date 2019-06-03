package com.uzpeng.sign.bo;

/**
 */
public class SignWebSocketLinkBO {
    private String websocketLink;

    private Integer signID;

    public String getLink() {
        return websocketLink;
    }

    public void setLink(String link) {
        this.websocketLink = link;
    }

    public void setSignID(Integer signID) {
        this.signID = signID;
    }

    public Integer getSignID() {
        return signID;
    }

}
