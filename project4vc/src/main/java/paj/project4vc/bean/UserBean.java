package aor.paj.proj3_vc_re_jc.bean;

import aor.paj.proj3_vc_re_jc.dao.TokenDao;
import aor.paj.proj3_vc_re_jc.dao.UserDao;
import aor.paj.proj3_vc_re_jc.dto.*;
import aor.paj.proj3_vc_re_jc.entity.TaskEntity;
import aor.paj.proj3_vc_re_jc.entity.TokenEntity;
import aor.paj.proj3_vc_re_jc.entity.UserEntity;
import aor.paj.proj3_vc_re_jc.enums.UserRole;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.core.Response;
import org.hibernate.annotations.Check;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;

import javax.management.relation.Role;
import java.io.Serializable;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


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
        if (userEntity != null && !userEntity.isDeleted()) {
            if (userEntity.getPassword().equals(password)) {
                String token = generateNewToken();
                userEntity.setTokenId(token);
                TokenEntity t = new TokenEntity();
                t.setId(token);
                t.setDeleted(false);
                t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
                tokenDao.persist(t);
                return token;
            }
        }
        return null;
    }

    public boolean register(UserDto user) {
        UserEntity u = userDao.findUserByUsername(user.getUsername());
        if (u == null) {
            userDao.persist(convertUserDtotoUserEntity(user));
            return true;
        } else
            return false;
    }

    private UserEntity convertUserDtotoUserEntity(UserDto user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(user.getUsername());
        userEntity.setPassword(user.getPassword());
        userEntity.setEmail(user.getEmail());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setPhone(user.getPhone());
        userEntity.setPhotoURL(user.getPhotoURL());
        userEntity.setTokenId(user.getToken());
        userEntity.setDeleted(user.isDeleted());
        userEntity.setRole(user.getRole());
        return userEntity;
    }

    private UserDto convertUserEntitytoUserDto(UserEntity user) {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPhone(user.getPhone());
        userDto.setPhotoURL(user.getPhotoURL());
        userDto.setToken(user.getTokenId());
        userDto.setDeleted(user.isDeleted());
        userDto.setRole(user.getRole());
        userDto.setId(user.getId());
        return userDto;
    }

    private String generateNewToken() {
        SecureRandom secureRandom = new SecureRandom(); //threadsafe
        Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public boolean logout(String token) {
        UserEntity u = userDao.findUserByToken(token);
        TokenEntity t = tokenDao.findTokenById(token);
        if (u != null) {
            u.setTokenId(null);
            t.setDeleted(true);
            //  tokenDao.remove(t);
            return true;
        }
        System.out.println("HERE");
        System.out.println(token);
        return false;
    }

    public boolean tokenExist(String token) {
        UserEntity u = userDao.findUserByToken(token);
        TokenEntity t = tokenDao.findTokenById(token);
        if (u != null && isTokenValid(t))
            return true;
        else {
            return false;
        }
    }

    public void updateProfile(EditDto user, String token) {
        UserEntity u = userDao.findUserByToken(token);
        TokenEntity t = tokenDao.findTokenById(token);
        if (u != null) {
            u.setPhotoURL(user.getPhotoURL());
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

    public void updateRole(RoleDto user, String token) {
        UserEntity u = userDao.findUserByUsername(user.getUsername());
        TokenEntity t = tokenDao.findTokenById(token);
        if (u != null) {
            u.setRole(user.getRole());
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            userDao.persist(u);
        }
    }

    public RoleDto getRole (String token) {
        UserEntity u = userDao.findUserByToken(token);
        RoleDto dto = new RoleDto();
        if (u!=null) {
            dto.setRole(u.getRole());
            return dto;
        }
        else return null;
    }

    public Response getAllUsers() {
        ArrayList<UserEntity> users = userDao.allUsers();
        if (users != null && !users.isEmpty()) {
            ArrayList<UserDto> userDtos = convertUsersFromEntityListToDtoList(users);
            return Response.status(200).entity(userDtos).build(); // Successful response
        } else {
            return Response.status(404).entity("No users found").build();
        }
    }


    private boolean isTokenValid(TokenEntity t) {
        Instant now = Instant.now();
        Instant expiration = t.getTokenExpiration();
        if (expiration.isAfter(now)) {
            return true;
        }
        return false;
    }

    public CheckProfileDto checkProfile(String username, String token) {
        UserEntity u = userDao.findUserByUsername(username);
        TokenEntity t = tokenDao.findTokenById(token);
        CheckProfileDto checkU = new CheckProfileDto();
        checkU.setUsername(u.getUsername());
        checkU.setFirstName(u.getFirstName());
        checkU.setLastName(u.getLastName());
        checkU.setEmail(u.getEmail());
        checkU.setPhone(u.getPhone());
        checkU.setRole(u.getRole().getValue());
        checkU.setPhotoURL(u.getPhotoURL());
        t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
        return checkU;
    }

    public boolean createUser(String token, UserDto user) {
        UserEntity u = userDao.findUserByUsername(user.getUsername());
        TokenEntity t = tokenDao.findTokenById(token);
        if (u == null) {
            System.out.println("Não existe");
            UserEntity newU = new UserEntity();
           newU.setUsername(user.getUsername());
           newU.setDeleted(user.isDeleted());
           newU.setPhone(user.getPhone());
           newU.setFirstName(user.getFirstName());
           newU.setLastName(user.getLastName());
           newU.setPassword(user.getPassword());
           newU.setPhotoURL(user.getPhotoURL());
           newU.setRole(user.getRole());
           newU.setEmail(user.getEmail());
            userDao.persist(newU);
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            return true;
        } else {
            return false;
    }
    }

    public ArrayList<CheckProfileDto> checkAll(String token) {
        TokenEntity t = tokenDao.findTokenById(token);
        ArrayList<UserEntity> userList = userDao.allUsers();

        ArrayList<CheckProfileDto> dtos = new ArrayList<>();

        for (UserEntity u : userList) {
            CheckProfileDto checkU = new CheckProfileDto();
            checkU.setUsername(u.getUsername());
            checkU.setFirstName(u.getFirstName());
            checkU.setLastName(u.getLastName());
            checkU.setEmail(u.getEmail());
            checkU.setPhone(u.getPhone());
            checkU.setRole(u.getRole().getValue());
            checkU.setPhotoURL(u.getPhotoURL());
            checkU.setDeleted(u.isDeleted());
            checkU.setId(u.getId());
            dtos.add(checkU);
        }
        t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
        return dtos;
    }

    public void deleteUser(String username, String token) {
        TokenEntity t = tokenDao.findTokenById(token);
        UserEntity u = userDao.findUserByUsername(username);
        if (u != null) {
            u.setDeleted(true);
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            userDao.persist(u);
        }
    }

    public CheckProfileDto userById (int id, String token) {
        TokenEntity t = tokenDao.findTokenById(token);
        UserEntity u = userDao.findUserById(id);
        CheckProfileDto dto = new CheckProfileDto();
        if (u != null) {
            dto.setUsername(u.getUsername());
            dto.setEmail(u.getEmail());
            dto.setPhone(u.getPhone());
            dto.setRole(u.getRole().getValue());
            dto.setEmail(u.getEmail());
            dto.setLastName(u.getLastName());
            dto.setFirstName(u.getFirstName());
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
        }
        return dto;
    }

    public void updateOtherProfile (String token, EditOtherDto dto) {
        TokenEntity t = tokenDao.findTokenById(token);
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
        TokenEntity t = tokenDao.findTokenById(token);
        UserEntity u = userDao.findUserByToken(token);
        if (u!=null) {
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            UserDto dto = convertUserEntitytoUserDto(u);
            return dto;
        } else {
            return null;
        }
    }

    private ArrayList<UserDto> convertUsersFromEntityListToDtoList(ArrayList<UserEntity> userEntityEntities) {
        ArrayList<UserDto> userDtos = new ArrayList<>();
        for (UserEntity u : userEntityEntities) {
            userDtos.add(convertShortUserEntitytoLoginDto(u));
        }
        return userDtos;
    }

    private UserDto convertShortUserEntitytoLoginDto(UserEntity user) {
        UserDto shortDto = new UserDto();
        shortDto.setUsername(user.getUsername());
        shortDto.setPassword(user.getPassword());
        return shortDto;
    }
}