package paj.project4vc.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "token")
@NamedQuery(name = "Token.findTokenById", query = "SELECT DISTINCT u FROM TokenEntity u WHERE u.id = :token")

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

    public java.lang.String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(java.lang.String tokenValue) {
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

    // public boolean isValid() {
    //            return tokenValue != null && tokenExpiration != null && tokenExpiration.isAfter(Instant.now());
    //        }
}