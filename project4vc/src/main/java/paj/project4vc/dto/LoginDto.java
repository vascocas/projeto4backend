package paj.project4vc.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import paj.project4vc.enums.UserRole;

@XmlRootElement
public class LoginDto {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private String username;
    @XmlElement
    private String password;
    @XmlElement
    private UserRole role;
    @XmlElement
    private String token;
    @XmlElement
    private String photo;

    public LoginDto() {
    }


    public LoginDto(String username, String password, UserRole role, String token, String photo) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.token = token;
        this.photo = photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}