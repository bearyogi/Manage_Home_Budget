package com.university.project.entity;

import com.university.project.backend.entity.Budget;
import com.university.project.backend.entity.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(groups = "Entity")
public class UserTest {
    private final User emptyUser = new User();
    private final User fullUser = new User();
    private final Budget budget = new Budget();

    @Test
    public void shouldCreateEmptyBudget(){
        //Given
        User testUser = new User();

        //When
        testUser.setId(null);
        testUser.setEmail(null);
        testUser.setFirstName(null);
        testUser.setLastName(null);
        testUser.setPrivateBudget(null);
        testUser.setPasswordHash(null);
        testUser.setPasswordSalt(null);
        testUser.setPhone(null);
        testUser.setUsername(null);

        //Then
        assertEquals(testUser.getId(),emptyUser.getId(),"Should have null id, but have not.");
        assertEquals(testUser.getEmail(),emptyUser.getEmail(),"Should have null email, but have not.");
        assertEquals(testUser.getFirstName(),emptyUser.getFirstName(),"Should have null first name, but have not.");
        assertEquals(testUser.getLastName(),emptyUser.getLastName(),"Should have null last name, but have not.");
        assertEquals(testUser.getPrivateBudget(),emptyUser.getPrivateBudget(),"Should have null budget, but have not.");
        assertEquals(testUser.getPasswordHash(),emptyUser.getPasswordHash(),"Should have null password hash, but have not.");
        assertEquals(testUser.getPasswordSalt(),emptyUser.getPasswordSalt(),"Should have null password salt, but have not.");
        assertEquals(testUser.getPhone(),emptyUser.getPhone(),"Should have null phone, but have not.");
        assertEquals(testUser.getUsername(),emptyUser.getUsername(),"Should have null username, but have not.");
    }

    //Given
    @Test
    public void shouldCreateFullBudget(){


        //When
        User testUser = new User("1","1","1","1","1","1");
        testUser.setPrivateBudget(budget);

        //Then
        assertEquals(testUser.getEmail(),fullUser.getEmail(),"Should have full email, but have not.");
        assertEquals(testUser.getFirstName(),fullUser.getFirstName(),"Should have full first name, but have not.");
        assertEquals(testUser.getLastName(),fullUser.getLastName(),"Should have full last name, but have not.");
        assertEquals(testUser.getPrivateBudget(),fullUser.getPrivateBudget(),"Should have full budget, but have not.");
        assertEquals(testUser.getPhone(),fullUser.getPhone(),"Should have full phone, but have not.");
        assertEquals(testUser.getUsername(),fullUser.getUsername(),"Should have full username, but have not.");
    }

    @BeforeTest
    public void fillExpense(){

        String plainPassword = "1";

        fullUser.setUsername("1");
        fullUser.setPasswordSalt(RandomStringUtils.random(32));
        fullUser.setPasswordHash(DigestUtils.sha1Hex(plainPassword + fullUser.getPasswordSalt()));
        fullUser.setPrivateBudget(budget);
        fullUser.setFirstName("1");
        fullUser.setLastName("1");
        fullUser.setEmail("1");
        fullUser.setPhone("1");
    }
}
