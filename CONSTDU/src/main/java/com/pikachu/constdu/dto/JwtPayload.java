package com.pikachu.constdu.dto;

public class JwtPayload {
    private String iss;
    private String sub;
    private long exp;
    private long iat;
    private String roles;

    // Getters and setters
    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "JwtPayload{" +
                "iss='" + iss + '\'' +
                ", sub='" + sub + '\'' +
                ", exp=" + exp +
                ", iat=" + iat +
                ", roles='" + roles + '\'' +
                '}';
    }
}
