package aor.paj.proj3_vc_re_jc.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "token")
@NamedQuery(name = "Token.findTokenById", query = "SELECT DISTINCT u FROM TokenEntity u WHERE u.id = :token")

public class TokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = true, unique = true, updatable = true)
    private String id;

    @Column(name = "token_expiration", nullable = true, unique = false, updatable = true)
    private Instant tokenExpiration;

    @Column(name = "deleted", nullable = false, unique = false, updatable = true)
    private boolean deleted;

    /*@OneToOne(mappedBy = "token", cascade = CascadeType.ALL)
    private UserEntity user;*/

    public TokenEntity() {
    }

    public String getId() {
        return id;
    }

    public Instant getTokenExpiration() {
        return tokenExpiration;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setTokenExpiration(Instant tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}