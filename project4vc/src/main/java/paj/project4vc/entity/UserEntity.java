package paj.project4vc.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import paj.project4vc.enums.UserRole;

@Entity
@Table(name = "user")
@NamedQuery(name = "User.findUserByUsername", query = "SELECT u FROM UserEntity u WHERE u.username = :username")
@NamedQuery(name = "User.findUserByToken", query = "SELECT u FROM UserEntity u WHERE u.tokens = :token")
@NamedQuery(name = "User.findUserById", query = "SELECT u FROM UserEntity u WHERE u.id = :id")
@NamedQuery(name = "User.findAllUsers", query = "SELECT u FROM UserEntity u")

public class UserEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @Column(name="email", nullable=false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "profile_photo", nullable = false)
    private String photo;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "username", nullable = false, unique = true, updatable = false)
    private String username;

    @Column(name="deleted", nullable = false)
    private boolean deleted;

    @Column(name = "role", nullable = false)
    private int role;

    @OneToMany(mappedBy = "creator")
    private ArrayList<TaskEntity> tasks;

    @OneToMany(mappedBy = "user")
    private ArrayList<TokenEntity> tokens;

    //default empty constructor
    public UserEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isDeleted() {
        return deleted;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public ArrayList<TokenEntity> getTokens() {
        return tokens;
    }
    public void setTokens(ArrayList<TokenEntity> tokens) {
        this.tokens = tokens;
    }
    public ArrayList<TaskEntity> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<TaskEntity> tasks) {
        this.tasks = tasks;
    }

    public UserRole getRole() {
        return UserRole.valueOf(this.role);
    }

    public void setRole(UserRole role) {
        this.role = role.getValue();
    }
}