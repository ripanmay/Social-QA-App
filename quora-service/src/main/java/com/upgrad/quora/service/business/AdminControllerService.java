package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminControllerService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(String uuid,String authToken) throws UserNotFoundException, AuthorizationFailedException {
        UserEntity userEntity = userDao.getUser(uuid);
        UserAuthTokenEntity accessToken = userDao.getUserAuthTokenEntity(authToken);

        if(userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }else if(accessToken == null) {
                throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else if(accessToken.getLogoutAt() != null){
                throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }

        String role = accessToken.getUser().getRole();

        if(role.equals("nonadmin")){
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }
        UserEntity user = userDao.getUser(uuid);
        userDao.deleteUser(user);
        //if(!deletedEntity.getUuid().contains(uuid)){
            return uuid;
        //}else{
        //    return null;
        //}
    }
}
