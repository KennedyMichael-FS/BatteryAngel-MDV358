package com.mskennedy.batteryangel.data;

import com.mskennedy.batteryangel.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        try {
            // TODO: Handle loggedInUser authentication
            LoggedInUser fakeUser = new LoggedInUser(
                    java.util.UUID.randomUUID().toString(),
                    "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            String errorMessage = "Error logging in: " + e.getMessage();
            return new Result.Error(errorMessage);
        }
    }


    public void logout() {
        // TODO: revoke authentication
    }
}