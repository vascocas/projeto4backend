package paj.project4vc.bean;

import paj.project4vc.dao.TokenDao;
import paj.project4vc.dao.UserDao;
import paj.project4vc.dto.*;
import paj.project4vc.entity.TokenEntity;
import paj.project4vc.entity.UserEntity;
import paj.project4vc.enums.UserRole;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
        return UUID.randomUUID().toString();
    }

    private boolean isTokenValid(TokenEntity t) {
        Instant expiration = t.getTokenExpiration();
        if(expiration != null && expiration != null && expiration.isAfter(Instant.now())){
            return true;
        }
        return false;
    }

    public boolean register(UserDto user) {
        UserEntity u = userDao.findUserByUsername(user.getUsername());
        if (u == null) {
            UserEntity newUser = convertUserDtotoEntity(user);
            newUser.setDeleted(false);
            newUser.setRole(UserRole.DEVELOPER);
            userDao.persist(newUser);
            return true;
        } else
            return false;
    }

    public boolean logout(String token) {
        UserEntity u = userDao.findUserByToken(token);
        TokenEntity t = tokenDao.findTokenByValue(token);
        if (u != null) {
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

    public void updateProfile(UserDto user, String token) {
        UserEntity u = userDao.findUserByToken(token);
        TokenEntity t = tokenDao.findTokenByValue(token);
        if (u != null) {
            u.setPhoto(user.getPhoto());
            u.setPhone(user.getPhone());
            u.setPassword(user.getPassword());
            u.setLastName(user.getLastName());
            u.setFirstName(user.getFirstName());
            u.setEmail(user.getEmail());
            u.setPassword(user.getPassword());
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            userDao.persist(u);
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

    public LoginDto getRole (String token) {
        UserEntity u = userDao.findUserByToken(token);
        LoginDto dto = new LoginDto();
        if (u!=null) {
            dto.setRole(u.getRole());
            return dto;
        }
        else return null;
    }

    public Response getAllUsers() {
        ArrayList<UserEntity> users = userDao.allUsers();
        if (users != null && !users.isEmpty()) {
            ArrayList<LoginDto> loginDtos = convertUsersFromEntityListToDtoList(users);
            return Response.status(200).entity(loginDtos).build(); // Successful response
        } else {
            return Response.status(404).entity("No users found").build();
        }
    }




    public UserDto checkProfile(String username, String token) {
        UserEntity u = userDao.findUserByUsername(username);
        TokenEntity t = tokenDao.findTokenByValue(token);
        UserDto checkU = new UserDto();
        checkU.setUsername(u.getUsername());
        checkU.setFirstName(u.getFirstName());
        checkU.setLastName(u.getLastName());
        checkU.setEmail(u.getEmail());
        checkU.setPhone(u.getPhone());
        checkU.setRole(u.getRole());
        checkU.setPhoto(u.getPhoto());
        t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
        return checkU;
    }

    public boolean createUser(String token, UserDto user) {
        UserEntity u = userDao.findUserByUsername(user.getUsername());
        TokenEntity t = tokenDao.findTokenByValue(token);
        if (u == null) {
            UserEntity newU = new UserEntity();
           newU.setUsername(user.getUsername());
           newU.setDeleted(user.isDeleted());
           newU.setPhone(user.getPhone());
           newU.setFirstName(user.getFirstName());
           newU.setLastName(user.getLastName());
           newU.setPassword(user.getPassword());
           newU.setPhoto(user.getPhoto());
           newU.setRole(user.getRole());
           newU.setEmail(user.getEmail());
            userDao.persist(newU);
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            return true;
        } else {
            return false;
    }
    }

    public ArrayList<UserDto> checkAll(String token) {
        TokenEntity t = tokenDao.findTokenByValue(token);
        ArrayList<UserEntity> userList = userDao.allUsers();

        ArrayList<UserDto> dtos = new ArrayList<>();

        for (UserEntity u : userList) {
            UserDto checkU = new UserDto();
            checkU.setUsername(u.getUsername());
            checkU.setFirstName(u.getFirstName());
            checkU.setLastName(u.getLastName());
            checkU.setEmail(u.getEmail());
            checkU.setPhone(u.getPhone());
            checkU.setRole(u.getRole());
            checkU.setPhoto(u.getPhoto());
            checkU.setDeleted(u.isDeleted());
            checkU.setId(u.getId());
            dtos.add(checkU);
        }
        t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
        return dtos;
    }

    public void deleteUser(String username, String token) {
        TokenEntity t = tokenDao.findTokenByValue(token);
        UserEntity u = userDao.findUserByUsername(username);
        if (u != null) {
            u.setDeleted(true);
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            userDao.persist(u);
        }
    }

    public UserDto userById (int id, String token) {
        TokenEntity t = tokenDao.findTokenByValue(token);
        UserEntity u = userDao.findUserById(id);
        UserDto dto = new UserDto();
        if (u != null) {
            dto.setUsername(u.getUsername());
            dto.setEmail(u.getEmail());
            dto.setPhone(u.getPhone());
            dto.setRole(u.getRole());
            dto.setEmail(u.getEmail());
            dto.setLastName(u.getLastName());
            dto.setFirstName(u.getFirstName());
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
        }
        return dto;
    }

    public void updateOtherProfile (String token, UserDto dto) {
        TokenEntity t = tokenDao.findTokenByValue(token);
        UserEntity u = userDao.findUserByUsername(dto.getUsername());
        if (u!=null) {
            u.setUsername(dto.getUsername());
            u.setEmail(dto.getEmail());
            u.setFirstName(dto.getFirstName());
            u.setLastName(dto.getLastName());
            u.setPhone(dto.getPhone());
            u.setDeleted(dto.isDeleted());
            u.setRole(dto.getRole());
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
        }
    }

    public UserDto userByToken(String token) {
        TokenEntity t = tokenDao.findTokenByValue(token);
        UserEntity u = userDao.findUserByToken(token);
        if (u!=null) {
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            UserDto dto = convertUserEntitytoDto(u);
            return dto;
        } else {
            return null;
        }
    }

    private LoginDto convertLoginEntitytoLoginDto(UserEntity user) {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(user.getUsername());
        loginDto.setPassword(user.getPassword());
        loginDto.setRole(user.getRole());
        return loginDto;
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
        userEntity.setDeleted(user.isDeleted());
        userEntity.setRole(user.getRole());
        return userEntity;
    }

    private UserDto convertUserEntitytoDto(UserEntity user) {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPhone(user.getPhone());
        userDto.setPhoto(user.getPhoto());
        userDto.setDeleted(user.isDeleted());
        userDto.setRole(user.getRole());
        userDto.setId(user.getId());
        return userDto;
    }

    private ArrayList<LoginDto> convertUsersFromEntityListToDtoList(ArrayList<UserEntity> userEntityEntities) {
        ArrayList<LoginDto> loginDtos = new ArrayList<>();
        for (UserEntity u : userEntityEntities) {
            loginDtos.add(convertLoginEntitytoLoginDto(u));
        }
        return loginDtos;
    }
}