package com.yourstories.integrationtest;

import com.yourstories.authorizationserver.config.ServiceConfig;
import com.yourstories.authorizationserver.model.User;
import com.yourstories.authorizationserver.model.VerificationToken;
import com.yourstories.authorizationserver.repositories.UserRepository;
import com.yourstories.authorizationserver.repositories.VerificationTokenRepository;
import com.yourstories.authorizationserver.spring.TestDbConfig;
import com.yourstories.authorizationserver.spring.TestIntegrationConfig;
import com.yourstories.authorizationserver.validation.EmailExistsException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestDbConfig.class, ServiceConfig.class, TestIntegrationConfig.class })
@Transactional
public class UserIntegrationTest {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Long tokenId;
    private Long userId;

    //

    @Before
    public void givenUserAndVerificationToken() throws EmailExistsException {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("SecretPassword");
        user.setFirstName("First");
        user.setLastName("Last");
        entityManager.persist(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        entityManager.persist(verificationToken);

        entityManager.flush();
        entityManager.clear();

        tokenId = verificationToken.getId();
        userId = user.getId();
    }

    @After
    public void flushAfter() {
        entityManager.flush();
        entityManager.clear();
    }

    //

    @Test
    public void whenContextLoad_thenCorrect() {
        assertEquals(1, userRepository.count());
        assertEquals(1, tokenRepository.count());
    }

    // @Test(expected = Exception.class)
    @Test
    @Ignore("needs to go through the service and get transactional semantics")
    public void whenRemovingUser_thenFkViolationException() {
        userRepository.delete(userId);
    }

    @Test
    public void whenRemovingTokenThenUser_thenCorrect() {
        tokenRepository.delete(tokenId);
        userRepository.delete(userId);
    }

}
