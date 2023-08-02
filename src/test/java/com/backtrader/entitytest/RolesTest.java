package com.backtrader.entitytest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.backtrader.userentity.Roles;

public class RolesTest {
	@MockBean
    private Roles roles;

    @BeforeEach
    public void setup() {
        roles = new Roles();
        roles.setId(1);
        roles.setRole("ROLE_ADMIN");
    }

    @Test
    public void testGettersAndSetters() {
        // test getters
        assertEquals(1, roles.getId());
        assertEquals("ROLE_ADMIN", roles.getRole());

        // test setters
        roles.setId(2);
        roles.setRole("ROLE_USER");

        assertEquals(2, roles.getId());
        assertEquals("ROLE_USER", roles.getRole());
    }

    @Test
    public void testGetAuthority() {
        String authority = roles.getAuthority();
        assertEquals("ROLE_ADMIN", authority);
    }
    @Test
    public void testToStringMethod() {
    	String role=roles.toString();
    		String expected= "Roles(id=1, role=ROLE_ADMIN)";
    		assertEquals(expected, role);
    	
    }

	
}
