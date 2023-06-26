package com.mskennedy.batteryangel.activity.accounts.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.mskennedy.batteryangel.data.LoginRepository;
import com.mskennedy.batteryangel.data.Result;
import com.mskennedy.batteryangel.data.model.LoggedInUser;
import com.mskennedy.batteryangel.R;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private MutableLiveData<RegistrationResult> registrationResult = new MutableLiveData<>();

    private final LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    LiveData<RegistrationResult> getRegistrationResult() {
        return registrationResult;
    }


    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    public void register(String username, String password) {
        // Validate the registration form
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
            return;
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
            return;
        }

        // Perform the registration logic here
        Result<LoggedInUser> result = loginRepository.register(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            registrationResult.setValue(new RegistrationResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            registrationResult.setValue(new RegistrationResult(R.string.registration_failed));
        }
    }



    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public class RegistrationResult {
        private LoggedInUserView success;
        private Integer error;

        public RegistrationResult(LoggedInUserView success) {
            this.success = success;
            this.error = null;
        }

        public RegistrationResult(Integer error) {
            this.success = null;
            this.error = error;
        }

        public LoggedInUserView getSuccess() {
            return success;
        }

        public Integer getError() {
            return error;
        }
    }

}