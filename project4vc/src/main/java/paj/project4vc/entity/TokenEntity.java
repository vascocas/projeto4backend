package paj.project4vc.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;


@Entity
@Table(name = "token")
@NamedQuery(name = "Token.findTokenByValue", query = "SELECT t FROM TokenEntity t WHERE t.tokenValue = :tokenValue")
public class TokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tokenId;

    @Column(name = "token_value", unique = true)
    private String tokenValue;

    @Column(name = "token_expiration")
    private Instant tokenExpiration;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public TokenEntity() {
    }

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Instant getTokenExpiration() {
        return tokenExpiration;
    }

    public void setTokenExpiration(Instant tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}