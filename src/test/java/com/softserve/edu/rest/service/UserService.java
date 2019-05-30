package com.softserve.edu.rest.service;

import com.softserve.edu.rest.data.User;
import com.softserve.edu.rest.engine.*;

import com.softserve.edu.rest.entity.RestParameters;
import com.softserve.edu.rest.entity.SimpleEntity;

public class UserService extends GuestService {

    protected LogoutResource logoutResource;
    protected UserResource userResource;
    protected User user;
    protected UsersResourse usersResourse;

    public UserService(User user) {
        // super(); // by default
        logoutResource = new LogoutResource();
        userResource = new UserResource();
        usersResourse = new UsersResourse();
        this.user = user;
    }

    public UserService(LoginResource loginResource,
                       TokenlifetimeResource tokenlifetimeResource,
                       User user) {
        super(loginResource, tokenlifetimeResource);
        this.user = user;
    }

    public GuestService LogoutUser() {
        RestParameters bodyParameters = new RestParameters()
                .addParameter("name", user.getName())
                .addParameter("token", user.getToken());
        // SimpleEntity simpleEntity = loginResource
        //.httpDeleteAsEntity(null, null, bodyParameters);
        SimpleEntity simpleEntity = logoutResource
                .httpPostAsEntity(null, null, bodyParameters);
        checkEntity(simpleEntity, "Error Logout");
        user.setToken("");
        return new GuestService();
    }

    public String getUserName() {
        RestParameters urlParameters = new RestParameters()
                .addParameter("token",user.getToken());
        SimpleEntity simpleEntity = userResource
                .httpGetAsEntity(null, urlParameters);
        checkEntity(simpleEntity, user.getName());
        return simpleEntity.getContent();
    }


//    public String changePassword(){
//        RestParameters urlParameters = new RestParameters()
//                .addParameter("token",user.getToken());
//    }


}
