package com.example.simon.typehandler;

import com.example.simon.entity.ProfileInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JsonTypeHandlerTest {

    private JsonTypeHandler jsonTypeHandler;

    @Mock
    private PreparedStatement ps;

    @Mock
    private ResultSet rs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jsonTypeHandler = new JsonTypeHandler(ProfileInfo.class);
    }

    @Test
    void testSetParameter() throws Exception {
        ProfileInfo profileInfo = new ProfileInfo();
        profileInfo.setNickname("测试用户");
        profileInfo.setAvatar("avatar.jpg");

        jsonTypeHandler.setNonNullParameter(ps, 1, profileInfo, null);

        verify(ps).setString(eq(1), contains("测试用户"));
        verify(ps).setString(eq(1), contains("avatar.jpg"));
    }

    @Test
    void testGetResult() throws Exception {
        String json = """
                {
                    "nickname": "测试用户",
                    "avatar": "avatar.jpg",
                    "bio": "测试简介",
                    "location": "北京"
                }
                """;

        when(rs.getString("profile_info")).thenReturn(json);

        Object result = jsonTypeHandler.getNullableResult(rs, "profile_info");

        assertNotNull(result);
        assertTrue(result instanceof ProfileInfo);
        ProfileInfo profileInfo = (ProfileInfo) result;
        assertEquals("测试用户", profileInfo.getNickname());
        assertEquals("avatar.jpg", profileInfo.getAvatar());
    }

    @Test
    void testNullValue() throws Exception {
        when(rs.getString("profile_info")).thenReturn(null);

        Object result = jsonTypeHandler.getNullableResult(rs, "profile_info");

        assertNull(result);
    }
}