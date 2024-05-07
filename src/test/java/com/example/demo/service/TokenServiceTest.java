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

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
@ActiveProfiles("test")
class TokenServiceTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenService tokenService;

    @Test
    public void testGetAccessToken_WithValidAuthorizationHeader() {
        // 준비
        String testAccessToken = "Bearer testAccessToken";
        String testRefreshToken = "Bearer testRefreshToken";

        when(request.getHeader("Authorization")).thenReturn(testAccessToken);
        when(request.getHeader("X-Refresh-Token")).thenReturn(testRefreshToken);

        // 실행
        Token token = tokenService.getToken(request);

        // 검증
        assertEquals("testAccessToken", token.getAccessToken());
        assertEquals("testRefreshToken", token.getRefreshToken());

    }

    @Test
    public void testGetAccessToken_WithInvalidAuthorizationHeader() {
        // 준비
        String testAccessToken = "invalidFormat testAccessToken";
        String testRefreshToken = "invalidFormat testRefreshToken";

        when(request.getHeader("Authorization")).thenReturn(testAccessToken);
        when(request.getHeader("X-Refresh-Token")).thenReturn(testRefreshToken);

        // 실행
        Token token = tokenService.getToken(request);

        // 검증
        assertEquals("invalidFormat testAccessToken", token.getAccessToken());
        assertEquals("invalidFormat testRefreshToken", token.getRefreshToken());
    }

    @Test
    public void testGetAccessToken_WithoutAuthorizationHeader() {
        // 준비
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-Refresh-Token")).thenReturn(null);

        // 실행
        Token token = tokenService.getToken(request);

        // 검증
        assertNull(token.getAccessToken());
        assertNull(token.getRefreshToken());
    }

    //	@Test
    public void testUpdateStatusToken() {
        // 준비
        Token token = Token.builder()
                .accessToken("ValidAccessToken")
                .refreshToken("ValidRefreshToken")
                .build();

        Status status = Status.INVALID;

        // 실행
        tokenService.updateStatusToken(token, status);

        // 검증
        verify(tokenRepository).save(Token.ValidToken.builder()
                .accessToken("ValidAccessToken")
                .refreshToken("ValidRefreshToken")
                .status(status)
                .build());
    }
}