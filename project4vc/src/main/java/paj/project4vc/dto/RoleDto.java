package aor.paj.proj3_vc_re_jc.dto;



import aor.paj.proj3_vc_re_jc.enums.TaskState;
import aor.paj.proj3_vc_re_jc.enums.UserRole;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RoleDto {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private UserRole role;

    @XmlElement
    private String username;

    public RoleDto() {
    }

    public RoleDto(UserRole role, String username) {
        this.role = role;
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}