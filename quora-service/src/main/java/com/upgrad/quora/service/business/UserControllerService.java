package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserControllerService {

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        String password = userEntity.getPassword();
        if(password == null){
            userEntity.setPassword("proman@123"); // default Password for new user
        }
        /*
            Check if the user name and email is already taken.
         */
        UserEntity userByEmail = userDao.getUserByEmail(userEntity.getEmail());
        UserEntity userByName  = userDao.getUserByName(userEntity.getUsername());

        if(userByName != null && userByName.getUsername().equals(userEntity.getUsername())){
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }else if( userByEmail != null && userByEmail.getEmail().equals(userEntity.getEmail())){
            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
        }

        /*
            Password Encryption
         */
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        return userDao.createUser(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signin(final String username, final String password) throws SignUpRestrictedException, AuthenticationFailedException {
        /*
            Check if the user name and email is already taken.
         */
        UserEntity userByName  = userDao.getUserByName(username);

        if(userByName == null){
            throw new SignUpRestrictedException("ATH-001","This username does not exist");
        }

        /*
            using encrypt method that gives encrypted password for comparision against the actual one.
         */
        String encryptedPassword = cryptographyProvider.encrypt(password,userByName.getSalt());
        if(encryptedPassword.equalsIgnoreCase(userByName.getPassword())){
            // creating a JWT Token for the password.
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity(); // Entity class
            userAuthToken.setUser(userByName);
            userAuthToken.setUuid(UUID.randomUUID().toString());
            // token expiration time.
            final LocalDateTime now = LocalDateTime.now();
            final LocalDateTime expiresAt = now.plusHours(8);
            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userByName.getUuid(), now, expiresAt));

            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);

            userDao.createAuthToken(userAuthToken);
            userDao.updateUser(userByName);

            return userAuthToken;
        }else{
            throw new AuthenticationFailedException("AUTH-002","Password failed");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signout(final String accessToken) throws SignOutRestrictedException {

        UserAuthTokenEntity userAuthToken = userDao.getUserAuthTokenEntity(accessToken);

        if(userAuthToken == null){
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }

        final LocalDateTime now = LocalDateTime.now();
        userAuthToken.setLogoutAt(now);
        userDao.updateAuthToken(userAuthToken);
        UserEntity userEntity = userAuthToken.getUser();
        return userEntity;
    }
}

