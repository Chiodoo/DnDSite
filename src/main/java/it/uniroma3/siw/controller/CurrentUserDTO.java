package it.uniroma3.siw.controller;

public class CurrentUserDTO {
    private String username;

    private boolean isOAuth2;

    public CurrentUserDTO(String username, boolean isOAuth2) {
        this.username = username;
        this.isOAuth2 = isOAuth2;
    }

    public String getUsername() {
        return username;
    }

    public boolean isOAuth2() {
        return isOAuth2;
    }
}
