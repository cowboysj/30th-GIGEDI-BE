package gigedi.dev.domain.auth.application;

import static gigedi.dev.global.common.constants.SecurityConstants.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import gigedi.dev.domain.auth.dto.response.GoogleLoginResponse;
import gigedi.dev.global.error.exception.CustomException;
import gigedi.dev.global.error.exception.ErrorCode;
import gigedi.dev.infra.config.oauth.GoogleProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GoogleService {
    private final GoogleProperties googleProperties;
    private final RestClient restClient;

    public GoogleLoginResponse getIdTokenByGoogleLogin(String code) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add(CODE_KEY, code);
            formData.add(CLIENT_ID_KEY, googleProperties.getId());
            formData.add(CLIENT_ID_SECRET, googleProperties.getSecret());
            formData.add(REDIRECT_URI_KEY, googleProperties.getRedirectUri());
            formData.add(GRANT_TYPE_KEY, googleProperties.getGrantType());

            return restClient
                    .post()
                    .uri(GET_ID_TOKEN_URL)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(formData)
                    .retrieve()
                    .onStatus(
                            status -> !status.is2xxSuccessful(),
                            (request, response) -> {
                                log.error("Google ID Token 요청 실패: {}", response.getStatusCode());
                                throw new CustomException(ErrorCode.GOOGLE_LOGIN_FAILED);
                            })
                    .body(GoogleLoginResponse.class);
        } catch (Exception e) {
            log.error("Google 로그인 중 예외 발생 : {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.GOOGLE_LOGIN_FAILED);
        }
    }
}
