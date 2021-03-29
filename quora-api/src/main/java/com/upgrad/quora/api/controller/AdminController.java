package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminControllerService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {
    @Autowired
    private AdminControllerService adminControllerService; // Added dependency in pom.xml

    @RequestMapping(method= RequestMethod.DELETE,path = "/admin/user/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> getUser(@PathVariable("userId") final String userUUID,
                                                      @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        // ResourceNotFoundException invokes RestException resourceNotFoundException method..
        String accessToken = authorization.split("Bearer")[0];
        final String uuid = adminControllerService.deleteUser(userUUID,accessToken);

        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(uuid).status("USER SUCCESSFULLY DELETED");

        //Return type is of ResponseEntity object..
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse,HttpStatus.OK);
    }
}
