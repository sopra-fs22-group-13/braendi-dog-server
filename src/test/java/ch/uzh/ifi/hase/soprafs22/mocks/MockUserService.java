/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen

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

package ch.uzh.ifi.hase.soprafs22.mocks;

import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.service.IUserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public abstract class MockUserService implements IUserService {
    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public User createUser(User newUser) {
        return null;
    }

    @Override
    public User loginUser(User user) {
        return null;
    }

    @Override
    public User getUserByToken(String token) {
        return null;
    }

    @Override
    public User getUserById(Long id) {
        return null;
    }

    @Override
    public void addWins(User user) {

    }

    @Override
    public void addNumberInGoal(User user, int marbleInGoal) {

    }

    @Override
    public User checkIfLoggedIn(HttpServletRequest request) {
        return null;
    }

    @Override
    public void CheckIfLoggedInAsUser(HttpServletRequest request, Long userId) {

    }

    @Override
    public void updateUser(User reqUser) {

    }
}
