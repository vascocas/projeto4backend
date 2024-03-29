package paj.project4vc.bean;


import paj.project4vc.dao.TokenDao;
import paj.project4vc.dao.UserDao;
import paj.project4vc.dto.*;
import paj.project4vc.entity.TaskEntity;
import paj.project4vc.entity.TokenEntity;
import paj.project4vc.entity.UserEntity;
import paj.project4vc.enums.UserRole;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;


@Stateless
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    UserDao userDao;
    @EJB
    TokenDao tokenDao;
    @EJB
    PassEncoder passEncoder;

    int tokenTimer = 1000000;

    public LoginDto login(String username, String password) {
        LoginDto successLogin = new LoginDto();
        UserEntity userEntity = userDao.findUserByUsername(username);
        if (userEntity != null && !userEntity.isDeleted()) {
            // Retrieve the hashed password associated with the user
            String hashedPassword = userEntity.getPassword();
            // Check if the provided password matches the hashed password
            if (passEncoder.matches(password, hashedPassword)) {
                String tokenValue = generateNewToken();
                TokenEntity tokenEntity = new TokenEntity();
                tokenEntity.setTokenValue(tokenValue);
                tokenEntity.setUser(userEntity);
                tokenEntity.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
                tokenDao.persist(tokenEntity);
                successLogin.setToken(tokenValue);
                successLogin.setRole(userEntity.getRole());
                successLogin.setPhoto(userEntity.getPhoto());
                successLogin.setUsername(username);
                return successLogin;
            }
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
        // Check if a user with the provided username and email already exists
        UserEntity userByUsername = userDao.findUserByUsername(user.getUsername());
        UserEntity userByEmail = userDao.findUserByEmail(user.getEmail());
        if ((userByUsername == null) && (userByEmail == null)) {
            UserEntity newUser = convertUserDtotoEntity(user);
            newUser.setRole(UserRole.DEVELOPER);
            userDao.persist(newUser);
            return true;
        }
        return false;
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
        if (u != null && isTokenValid(t)) {
            t.setTokenExpiration(Instant.now().plus(tokenTimer, ChronoUnit.SECONDS));
            return true;
        } else {
            return false;
        }
    }

    public UserDto userById(int id) {
        UserEntity u = userDao.findUserById(id);
        if (u != null) {
            UserDto dto = convertUserEntitytoUserDto(u);
            return dto;
        } else return new UserDto();
    }


    public UserDto userByToken(String token) {
        UserEntity u = userDao.findUserByToken(token);
        if (u != null) {
            UserDto dto = convertUserEntitytoUserDto(u);
            return dto;
        } else return new UserDto();
    }

    public UserDto getProfile(String username, String token) {
        UserEntity userEntity = userDao.findUserByToken(token);
        UserRole userRole = userEntity.getRole();
        // Check if the user is a DEVELOPER: can only get own profile
        if (userRole != UserRole.DEVELOPER) {
            UserEntity u = userDao.findUserByUsername(username);
            if (u != null) {
                UserDto checkedUser = convertUserEntitytoUserDto(u);
                return checkedUser;
            }
        }
        return null;
    }

    public boolean editProfile(UserDto user, String token) {
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity != null) {
            userEntity.setEmail(user.getEmail());
            userEntity.setFirstName(user.getFirstName());
            userEntity.setLastName(user.getLastName());
            userEntity.setPhone(user.getPhone());
            userEntity.setPhoto(user.getPhoto());
            return true;
        }
        return false;
    }

    public boolean editUsersProfile(UserDto user, String token) {
        // Get user role by token
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity != null) {
            UserRole userRole = userEntity.getRole();
            // Check if the user is a DEVELOPER or SCRUM_MASTER: can only edit own profile
            if (userRole != UserRole.DEVELOPER && userRole != UserRole.SCRUM_MASTER) {
                UserEntity u = userDao.findUserById(user.getId());
                if (u != null) {
                    u.setEmail(user.getEmail());
                    u.setFirstName(user.getFirstName());
                    u.setLastName(user.getLastName());
                    u.setPhone(user.getPhone());
                    u.setPhoto(user.getPhoto());
                    return true;
                }
            }
        }
        return false;
    }

    public boolean editUserPassword(String token, PasswordDto newPass) {
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity != null) {
            // Retrieve the hashed password associated with the user
            String hashedPassword = userEntity.getPassword();
            if ((passEncoder.matches(newPass.getPassword(), hashedPassword)) && (newPass.getNewPass().equals(newPass.getConfirmPass()))) {
                String encryptedPassword = passEncoder.encode(newPass.getNewPass());
                userEntity.setPassword(encryptedPassword);
                return true;
            }
        }
        return false;
    }

    public boolean editUsersPassword(String token, PasswordDto newPass) {
        // Get user role by token
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity != null) {
            UserRole userRole = userEntity.getRole();
            // Check if the user is a DEVELOPER or SCRUM_MASTER: can only edit own profile
            if (userRole != UserRole.DEVELOPER && userRole != UserRole.SCRUM_MASTER) {
                UserEntity u = userDao.findUserById(newPass.getId());
                if (u != null) {
                    String hashedPassword = u.getPassword();
                    if ((passEncoder.matches(newPass.getPassword(), hashedPassword)) && (newPass.getNewPass().equals(newPass.getConfirmPass()))) {
                        String encryptedPassword = passEncoder.encode(newPass.getNewPass());
                        u.setPassword(encryptedPassword);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean updateRole(RoleDto user, String token) {
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity != null) {
            UserRole userRole = userEntity.getRole();
            // Check if the user is not a DEVELOPER or SCRUM_MASTER: cannot change user role
            if (userRole == UserRole.DEVELOPER || userRole == UserRole.SCRUM_MASTER) {
            return false;
            }
            UserEntity u = userDao.findUserById(user.getId());
            if (u != null) {
                u.setRole(user.getRole());
                userDao.persist(u);
                return true;
            }
        }
        return false;
    }

    public RoleDto getRole(String token) {
        UserEntity u = userDao.findUserByToken(token);
        RoleDto dto = new RoleDto();
        if (u != null) {
            dto.setRole(u.getRole());
            return dto;
        } else return new RoleDto();
    }

    public ArrayList<RoleDto> getAllUsernames() {
        ArrayList<UserEntity> users = userDao.findAllActiveUsers();
        if (users != null && !users.isEmpty()) {
            return convertUsersFromEntityListToRoleDtoList(users);
        } else {
            return new ArrayList<>();
        }
    }

    public UserDto createUser(String token, UserDto user) {
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity != null) {
            UserRole userRole = userEntity.getRole();
            // Check if the user is a DEVELOPER or SCRUM_MASTER: cannot create user
            if (userRole != UserRole.DEVELOPER && userRole != UserRole.SCRUM_MASTER) {
                // Check if a user with the provided username and email already exists
                UserEntity userByUsername = userDao.findUserByUsername(user.getUsername());
                UserEntity userByEmail = userDao.findUserByEmail(user.getEmail());
                if ((userByUsername == null) && (userByEmail == null)) {
                    UserEntity newUser = convertUserDtotoEntity(user);
                    newUser.setRole(user.getRole());
                    userDao.persist(newUser);
                    return convertUserEntitytoUserDto(newUser);
                }
            }
        }
        return null;
    }

    public ArrayList<UserDto> getAllUsers(String token) {
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity != null) {
            UserRole userRole = userEntity.getRole();
            // Check if the user is a DEVELOPER or SCRUM_MASTER: cannot get all users list
            if (userRole == UserRole.DEVELOPER || userRole == UserRole.SCRUM_MASTER) {
                return null;
            }
            ArrayList<UserEntity> userList = userDao.findAllActiveUsers();
            if (userList != null) {
                ArrayList<UserDto> users = convertUsersFromEntityListToUserDtoList(userList);
                return users;
            }
        }
        return null;
    }

    public ArrayList<UserDto> getDeletedUsers(String token) {
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity != null) {
            UserRole userRole = userEntity.getRole();
            // Check if the user is a DEVELOPER: cannot get deleted users list
            if (userRole == UserRole.DEVELOPER) {
                return null;
            }
            ArrayList<UserEntity> userDeletedList = userDao.findAllDeletedUsers();
            if (userDeletedList != null) {
                ArrayList<UserDto> deletedUsers = convertUsersFromEntityListToUserDtoList(userDeletedList);
                return deletedUsers;
            }
        }
        return null;
    }

    public boolean deleteUser(String token, int userId) {
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity != null) {
            UserRole userRole = userEntity.getRole();
            // Check user role: DEVELOPER or SCRUM_MASTER cannot delete users
            if (userRole != UserRole.DEVELOPER && userRole != UserRole.SCRUM_MASTER) {
                UserEntity u = userDao.findUserById(userId);
                if (u != null) {
                    u.setDeleted(true);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeUser(int id, String token) {
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity != null) {
            UserRole userRole = userEntity.getRole();
            // Check user role: DEVELOPER or SCRUM_MASTER cannot remove users
            if (userRole != UserRole.DEVELOPER && userRole != UserRole.SCRUM_MASTER) {
                UserEntity u = userDao.findUserById(id);
                if (u != null) {
                    for (TaskEntity task : u.getTasks()) {
                        task.setCreator(null);
                    }
                    userDao.remove(u);
                    return true;
                }
            }
        }
        return false;
    }

    private UserEntity convertUserDtotoEntity(UserDto user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(user.getUsername());
        // Encrypt the password before storing
        String encryptedPassword = passEncoder.encode(user.getPassword());
        userEntity.setPassword(encryptedPassword);
        userEntity.setEmail(user.getEmail());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setPhone(user.getPhone());
        if (user.getPhoto() == null || user.getPhoto().isEmpty()) {
            userEntity.setPhoto("https://cdn.pixabay.com/photo/2015/03/04/22/35/avatar-659651_640.png");
        } else {
            userEntity.setPhoto(user.getPhoto());
        }
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
        loginDto.setPhoto(user.getPhoto());
        return loginDto;
    }

    private ArrayList<LoginDto> convertUsersFromEntityListToLoginDtoList
            (ArrayList<UserEntity> userEntityEntities) {
        ArrayList<LoginDto> loginDtos = new ArrayList<>();
        for (UserEntity u : userEntityEntities) {
            loginDtos.add(convertUserEntitytoLoginDto(u));
        }
        return loginDtos;
    }

    private ArrayList<RoleDto> convertUsersFromEntityListToRoleDtoList
            (ArrayList<UserEntity> userEntityEntities) {
        ArrayList<RoleDto> roleDtos = new ArrayList<>();
        for (UserEntity u : userEntityEntities) {
            RoleDto roleDto = new RoleDto();
            roleDto.setId(u.getId());
            roleDto.setUsername(u.getUsername());
            roleDto.setRole(u.getRole());
            roleDtos.add(roleDto);
        }
        return roleDtos;
    }
}