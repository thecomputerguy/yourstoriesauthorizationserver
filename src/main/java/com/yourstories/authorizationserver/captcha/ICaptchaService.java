package com.yourstories.authorizationserver.captcha;

import com.yourstories.authorizationserver.error.ReCaptchaInvalidException;

public interface ICaptchaService {
    void processResponse(final String response) throws ReCaptchaInvalidException;

    String getReCaptchaSite();

    String getReCaptchaSecret();
}
