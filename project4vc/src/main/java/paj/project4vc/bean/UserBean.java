package paj.project4vc.bean;

import paj.project4vc.dao.TokenDao;
import paj.project4vc.dao.UserDao;
import paj.project4vc.dto.*;
import paj.project4vc.entity.CategoryEntity;
import paj.project4vc.entity.TaskEntity;
import paj.project4vc.entity.TokenEntity;
import paj.project4vc.entity.UserEntity;
import paj.project4vc.enums.UserRole;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;


@Stateless
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    UserDao userDao;

    @EJB
    TokenDao tokenDao;

    int tokenTimer = 10000000;

    public String login(String username, String password) {
        UserEntity userEntity = userDao.findUserByUsername(username);
        if (userEntity != null && !userEntity.isDeleted() && userEntity.getPassword().equals(password)) {
            String tokenValue = generateNewToken();
            TokenEntity tokenEntity = new TokenEntity();
            tokenEntity.setTokenValue(tokenValue);
            tokenEntity.setUser(userEntity);
            tokenEntity.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            tokenDao.persist(tokenEntity);
            return tokenValue;
        }
        return null;
    }

    private String generateNewToken() {
        SecureRandom secureRandom = new SecureRandom();
        Base64.Encoder base64Encoder = Base64.getUrlEncoder();
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    private boolean isTokenValid(TokenEntity t) {
        Instant expiration = t.getTokenExpiration();
        if (expiration != null && expiration != null && expiration.isAfter(Instant.now())) {
            return true;
        }
        return false;
    }

    public boolean register(UserDto user) {
        // Check if a user with the provided username already exists
        UserEntity userByUsername = userDao.findUserByUsername(user.getUsername());
        if (userByUsername != null) {
            return false;
        }
        // Check if a user with the provided email already exists
        UserEntity userByEmail = userDao.findUserByEmail(user.getEmail());
        if (userByEmail != null) {
            return false;
        }
        UserEntity newUser = convertUserDtotoEntity(user);
        newUser.setRole(UserRole.DEVELOPER);
        userDao.persist(newUser);
        return true;
    }

    public boolean logout(String token) {
        UserEntity u = userDao.findUserByToken(token);
        if (u != null) {
            TokenEntity t = tokenDao.findTokenByValue(token);
            t.setTokenValue(null);
            return true;
        }
        return false;
    }

    public boolean tokenExist(String token) {
        UserEntity u = userDao.findUserByToken(token);
        TokenEntity t = tokenDao.findTokenByValue(token);
        if (u != null && isTokenValid(t))
            return true;
        else {
            return false;
        }
    }

    public UserDto getProfile(String username, String token) {
        UserEntity u = userDao.findUserByUsername(username);
        TokenEntity t = tokenDao.findTokenByValue(token);
        if (u != null && t != null) {
            UserDto checkU = convertUserEntitytoUserDto(u);
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            return checkU;
        }
        return null;
    }

    public void editProfile(UserDto user, String token) {
        UserEntity u;
        // Get user role by token
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity != null) {
            UserRole userRole = userEntity.getRole();
            // Check if the user is a DEVELOPER: can only edit own profile
            if (userRole == UserRole.DEVELOPER) {
                u = userEntity;
            } else {
                u = userDao.findUserByUsername(user.getUsername());
            }
            TokenEntity t = tokenDao.findTokenByValue(token);
            if (u != null && t != null) {
                u.setPassword(user.getPassword());
                u.setEmail(user.getEmail());
                u.setFirstName(user.getFirstName());
                u.setLastName(user.getLastName());
                u.setPhone(user.getPhone());
                u.setPhoto(user.getPhoto());
                t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            }
        }
    }

    public void updateRole(LoginDto user, String token) {
        UserEntity u = userDao.findUserByUsername(user.getUsername());
        TokenEntity t = tokenDao.findTokenByValue(token);
        if (u != null) {
            u.setRole(user.getRole());
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            userDao.persist(u);
        }
    }

    public LoginDto getRole(String token) {
        UserEntity u = userDao.findUserByToken(token);
        LoginDto dto = new LoginDto();
        if (u != null) {
            dto.setRole(u.getRole());
            return dto;
        } else return null;
    }

    public ArrayList<LoginDto> getAllUsernames() {
        ArrayList<UserEntity> users = userDao.findAllActiveUsers();
        if (users != null && !users.isEmpty()) {
            return convertUsersFromEntityListToLoginDtoList(users);
        } else {
            return null;
        }
    }

    public boolean createUser(String token, UserDto user) {
        UserEntity u = userDao.findUserByUsername(user.getUsername());
        TokenEntity t = tokenDao.findTokenByValue(token);
        if (u == null) {
            UserEntity newU = convertUserDtotoEntity(user);
            newU.setRole(user.getRole());
            userDao.persist(newU);
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<UserDto> getAllUsers(String token) {
        TokenEntity t = tokenDao.findTokenByValue(token);
        ArrayList<UserEntity> userList = userDao.findAllActiveUsers();
        if (t != null && userList != null) {
            ArrayList<UserDto> users = convertUsersFromEntityListToUserDtoList(userList);
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            return users;
        } else return null;
    }

    public void deleteUser(String username, String token) {
        TokenEntity t = tokenDao.findTokenByValue(token);
        UserEntity u = userDao.findUserByUsername(username);
        if (t != null && u != null) {
            u.setDeleted(true);
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
        }
    }

    public UserDto userById(int id, String token) {
        TokenEntity t = tokenDao.findTokenByValue(token);
        UserEntity u = userDao.findUserById(id);
        if (t != null && u != null) {
            UserDto dto = convertUserEntitytoUserDto(u);
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            return dto;
        } else return null;
    }


    public UserDto userByToken(String token) {
        TokenEntity t = tokenDao.findTokenByValue(token);
        UserEntity u = userDao.findUserByToken(token);
        if (t != null && u != null) {
            UserDto dto = convertUserEntitytoUserDto(u);
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            return dto;
        } else return null;
    }


    private UserEntity convertUserDtotoEntity(UserDto user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(user.getUsername());
        userEntity.setPassword(user.getPassword());
        userEntity.setEmail(user.getEmail());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setPhone(user.getPhone());
        userEntity.setPhoto(user.getPhoto());
        userEntity.setDeleted(false);
        return userEntity;
    }

    private UserDto convertUserEntitytoUserDto(UserEntity user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPhone(user.getPhone());
        userDto.setPhoto(user.getPhoto());
        userDto.setDeleted(user.isDeleted());
        userDto.setRole(user.getRole());
        return userDto;
    }

    private ArrayList<UserDto> convertUsersFromEntityListToUserDtoList(ArrayList<UserEntity> userEntityEntities) {
        ArrayList<UserDto> userDtos = new ArrayList<>();
        for (UserEntity u : userEntityEntities) {
            userDtos.add(convertUserEntitytoUserDto(u));
        }
        return userDtos;
    }

    private LoginDto convertUserEntitytoLoginDto(UserEntity user) {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(user.getUsername());
        loginDto.setPassword(user.getPassword());
        loginDto.setRole(user.getRole());
        return loginDto;
    }

    private ArrayList<LoginDto> convertUsersFromEntityListToLoginDtoList(ArrayList<UserEntity> userEntityEntities) {
        ArrayList<LoginDto> loginDtos = new ArrayList<>();
        for (UserEntity u : userEntityEntities) {
            loginDtos.add(convertUserEntitytoLoginDto(u));
        }
        return loginDtos;
    }
}