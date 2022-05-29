/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen, Simona Borghi, Sandro Vonlanthen, Anton Crazzolara, Shitao Zeng

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package ch.uzh.ifi.hase.soprafs22.rest.service;

import ch.uzh.ifi.hase.soprafs22.rest.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setPassword("testName");
    testUser.setUsername("testUsern");

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getPassword(), createdUser.getPassword());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.OFFLINE, createdUser.getStatus()); //check the implementation on why this should still be "offline"
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByPassword(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

    @Test
    public void loginUser_valid() {
        // given -> a first user has already been created
        userService.createUser(testUser);
        testUser.setStatus(UserStatus.OFFLINE);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByPassword(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to log in, this should set the status to online and give us back the user
        User u = userService.loginUser(testUser);
        assertEquals(UserStatus.OFFLINE, u.getStatus()); //check the implementation on why this should still be "offline"
        assertEquals(testUser.getUsername(), u.getUsername());
    }

    @Test
    public void loginUser_wrong_pw() {
        // given -> a first user has already been created
        userService.createUser(testUser);
        testUser.setStatus(UserStatus.OFFLINE);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);

        User login_user = new User();
        login_user.setUsername("testUsern");
        login_user.setPassword("wrongPw");

        // then -> attempt to log in, this throws an error
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(login_user));
    }

    @Test
    public void loginUser_wrong_user() {
        // given -> a first user has already been created
        userService.createUser(testUser);
        testUser.setStatus(UserStatus.OFFLINE);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);

        User login_user = new User();
        login_user.setUsername("someWrongUsername");
        login_user.setPassword("testName");

        // then -> attempt to log in, this throws an error
        assertThrows(ResponseStatusException.class, () -> userService.loginUser(login_user));
    }

    @Test
    public void getUserById_valid() {
        // given -> a first user has already been created
        userService.createUser(testUser);
        Optional<User> opt = Optional.of(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(opt);

        User find_user = new User();
        find_user.setId(1L); //ok, but any is ok

        // then -> attempt to find
        assertEquals(testUser, userService.getUserById(find_user.getId()));
    }

    @Test
    public void getUserById_invalid() {
        // given -> a first user has already been created
        userService.createUser(testUser);
        Optional<User> opt = Optional.ofNullable(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findById(testUser.getId())).thenReturn(opt);

        User find_user = new User();
        find_user.setId(1000L); //wrong

        // then -> attempt to find
        assertThrows(ResponseStatusException.class, () -> userService.getUserById(find_user.getId()));
    }

    @Test
    public void addWinsAndGoals() {
        // given -> a first user has already been created
        userService.createUser(testUser);
        Optional<User> opt = Optional.ofNullable(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findById(testUser.getId())).thenReturn(opt);

        // then -> attempt to find
        userService.addWins(testUser);
        userService.addNumberInGoal(testUser, 2);

        assertEquals(2, testUser.getGoals());
        assertEquals(1, testUser.getWins());
    }

  @Test
    public void leaderboard()
  {

      User testUser1 = new User();
      testUser1.setId(1L);
      testUser1.setPassword("testName");
      testUser1.setUsername("testUsern");

      User testUser2 = new User();
      testUser2.setId(2L);
      testUser2.setPassword("testName2");
      testUser2.setUsername("testUsern2");

      ArrayList<User> u = new ArrayList<>(Arrays.asList(testUser1, testUser2));

      Mockito.when(userRepository.getTopTenUsers()).thenReturn(u);

      assertEquals(u , userService.getLeaderboard());
  }

    @Test
    public void checkIfLoggedIn_valid() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);

        // then -> attempt login

        MockHttpServletRequest r = new MockHttpServletRequest();
        r.addHeader("Authorization", "BASIC " + testUser.getToken());

        assertDoesNotThrow(() -> userService.checkIfLoggedIn(r));
    }

    @Test
    public void checkIfLoggedIn_bad_token() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);

        // then -> attempt login

        MockHttpServletRequest r = new MockHttpServletRequest();
        r.addHeader("Authorization", "BASIC " + "wrongToken");

        assertThrows(ResponseStatusException.class, () -> userService.checkIfLoggedIn(r));
    }

    @Test
    public void checkIfLoggedIn_wrong_auth() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);

        // then -> attempt login

        MockHttpServletRequest r = new MockHttpServletRequest();
        r.addHeader("Authorization", "LAME " + testUser.getToken());

        assertThrows(ResponseStatusException.class, () -> userService.checkIfLoggedIn(r));
    }

    @Test
    public void checkIfLoggedInUser_valid() {
        // given -> a first user has already been created
        userService.createUser(testUser);
        Optional<User> opt = Optional.ofNullable(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(opt);

        // then -> attempt login

        MockHttpServletRequest r = new MockHttpServletRequest();
        r.addHeader("Authorization", "BASIC " + testUser.getToken());

        assertDoesNotThrow(() -> userService.CheckIfLoggedInAsUser(r, 1L));
    }

    @Test
    public void checkIfLoggedInUser_bad_token() {
        // given -> a first user has already been created
        userService.createUser(testUser);
        Optional<User> opt = Optional.ofNullable(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(opt);

        // then -> attempt login

        MockHttpServletRequest r = new MockHttpServletRequest();
        r.addHeader("Authorization", "BASIC " + "wrongToken");

        assertThrows(ResponseStatusException.class, () -> userService.CheckIfLoggedInAsUser(r, 1L));
    }

    @Test
    public void checkIfLoggedInUser_wrong_auth() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testUser);

        // then -> attempt login

        MockHttpServletRequest r = new MockHttpServletRequest();
        r.addHeader("Authorization", "LAME " + testUser.getToken());

        assertThrows(ResponseStatusException.class, () -> userService.CheckIfLoggedInAsUser(r, 1L));
    }

    @Test
    public void updateOfflineOnline() {
        // given -> a first user has already been created
        userService.createUser(testUser);
        Optional<User> opt = Optional.ofNullable(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(opt);

        // then -> update offline/online status

        userService.setUserOffline(testUser);
        assertEquals(UserStatus.OFFLINE,testUser.getStatus());
        userService.setUserOnline(testUser);
        assertEquals(UserStatus.ONLINE,testUser.getStatus());
        userService.setUserOffline(testUser);
        assertEquals(UserStatus.OFFLINE,testUser.getStatus());
   }

   @Test
   public void updateUser_valid() {
       // given -> a first user has already been created
       userService.createUser(testUser);
       Optional<User> opt = Optional.ofNullable(testUser);



       // when -> setup additional mocks for UserRepository
       Mockito.when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
       Mockito.when(userRepository.findById(Mockito.any())).thenReturn(opt);

       // then -> update user
       User new_user = new User();
       new_user.setId(testUser.getId());
       new_user.setUsername("NewUsern");
       new_user.setDescription("Hello World");
       new_user.setPassword("newPass");
       new_user.setAvatar(2);

       userService.updateUser(new_user);

       assertEquals(0L, testUser.getId());
       assertEquals(2, testUser.getAvatar());
       assertEquals(new_user.getUsername(), testUser.getUsername());
       assertEquals(new_user.getPassword(), testUser.getPassword());
       assertEquals(new_user.getDescription(), testUser.getDescription());
   }

    @Test
    public void updateUser_username_taken() {
        // given -> a first user has already been created
        userService.createUser(testUser);
        Optional<User> opt = Optional.ofNullable(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(opt);

        // then -> update user
        User new_user = new User();
        new_user.setId(testUser.getId());
        new_user.setUsername(testUser.getUsername()); //this username is taken
        new_user.setDescription("Hello World");

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(new_user));
    }

}
