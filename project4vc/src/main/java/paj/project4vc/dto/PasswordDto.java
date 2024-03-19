package paj.project4vc.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PasswordDto {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private int id;
    @XmlElement
    private String password;
    @XmlElement
    private String newPass;
    @XmlElement
    private String confirmPass;


    public PasswordDto() {
    }

    public PasswordDto(int id, String password, String newPass, String confirmPass) {
        this.id = id;
        this.password = password;
        this.newPass = newPass;
        this.confirmPass = confirmPass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    public String getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }
}
