package EcommerceProject.service;

import EcommerceProject.Security.request.LoginRequest;
import EcommerceProject.Security.request.SignupRequest;
import EcommerceProject.Security.response.MessageResponse;
import EcommerceProject.Security.response.UserInfoResponse;

import java.util.Map;

public interface AuthService {
    Map<String, Object> authenticateUser(LoginRequest loginRequest);
    MessageResponse registerUser(SignupRequest signUpRequest);
}

