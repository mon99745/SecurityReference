package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.domain.Token;
import com.example.demo.repository.TokenRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
@Transactional
@ActiveProfiles("test")
class TokenServiceTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private TokenRepository tokenRepository;
    @InjectMocks
    private TokenService tokenService;

    /**
     * @Desc 헤더에서 토큰 정보 추출 테스트
     * case: 헤더에 Access Token 만 있는경우
     */
    @Test
    public void testGetToken_AuthorizationHeader() {
        // Arrange
        String testAccessToken = "Bearer testAccessToken";

        when(request.getHeader("Authorization")).thenReturn(testAccessToken);

        // Act
        Token token = tokenService.getToken(request);

        // Assert
        assertEquals("testAccessToken", token.getAccessToken());
    }

    /**
     * @Desc 헤더에서 토큰 정보 추출 테스트
     * case: 헤더에 Refresh Token 만 있는경우
     */
    @Test
    public void testGetToken_RefreshTokenHeader() {
        // Arrange
        String testRefreshToken = "Bearer testRefreshToken";

        when(request.getHeader("X-Refresh-Token")).thenReturn(testRefreshToken);

        // Act
        Token token = tokenService.getToken(request);

        // Assert
        assertEquals("testRefreshToken", token.getRefreshToken());
    }

    /**
     * @Desc 헤더에서 토큰 정보 추출 테스트
     * case: 헤더에 Access Token, Refresh Token 둘다 있는경우
     */
    @Test
    public void testGetToken_BothHeaders() {
        // Arrange
        String testAccessToken = "Bearer testAccessToken";
        String testRefreshToken = "Bearer testRefreshToken";

        when(request.getHeader("Authorization")).thenReturn(testAccessToken);
        when(request.getHeader("X-Refresh-Token")).thenReturn(testRefreshToken);

        // Act
        Token token = tokenService.getToken(request);

        // Assert
        assertEquals("testAccessToken", token.getAccessToken());
        assertEquals("testRefreshToken", token.getRefreshToken());
    }


    /**
     * @Desc 토큰 상태 변경 테스트
     */
    @Test
    public void testUpdateStatusToken() {
        // Arrange
        Token token = Token.builder()
                .accessToken("ValidAccessToken")
                .refreshToken("ValidRefreshToken")
                .build();

        Status status = Status.INVALID;

        // Act
        tokenService.updateStatusToken(token, status);

        // Assert
        verify(tokenRepository).save(any(Token.ValidToken.class));
    }
}