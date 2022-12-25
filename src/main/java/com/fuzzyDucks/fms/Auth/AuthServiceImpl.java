package com.fuzzyDucks.fms.Auth;

import com.fuzzyDucks.fms.Exceptions.InvalidDataException;
import com.fuzzyDucks.fms.Logger.intf.ILogger;
import com.fuzzyDucks.fms.Logger.LoggingHandler;
import com.fuzzyDucks.fms.User.enums.UserFieldName;
import org.bson.Document;

import com.fuzzyDucks.fms.Auth.JWT.JWTService;
import com.fuzzyDucks.fms.Cache.Cache;
import com.fuzzyDucks.fms.User.services.UserService;
import com.fuzzyDucks.fms.User.utils.UserUtils;

public class AuthServiceImpl implements AuthService {

    private static final Cache cache = Cache.getInstance();
    private static final JWTService jwtService = new JWTService();
    private static final ILogger logger = LoggingHandler.getInstance();
    private static final UserService userService = new UserService();

    @Override
    public String getToken(Document user) {
        jwtService.signToken(user);
        return jwtService.getToken();
    }

    @Override
    public boolean validateUser(String username, String password) {
        Document user = userService.getUser(username);
        if (user != null && UserUtils.checkPassword(password, user.getString(UserFieldName.PASSWORD.getValue()))) {
            logger.logInfo("User: " + username + " validated successfully");
            return true;
        }
        logger.logWarning("User: " + username + " failed to validate credentials");
        throw new InvalidDataException("Invalid username or password");
    }

    @Override
    public void login(String username, String password) {
        if (validateUser(username, password)) {
            jwtService.signToken(userService.getUser(username));
            String token = jwtService.getToken();
            cache.put("token", token);
            cache.put("role", JWTService.decodeObject(token, "role"));
            logger.logInfo("User: " + username + " logged in successfully");
            return;
        }
        logger.logWarning("User: " + username + " failed to log in");
        throw new InvalidDataException("Invalid username or password");
    }

    @Override
    public void logout() {
        cache.remove("token");
        logger.logInfo("User logged out successfully");
    }

}
