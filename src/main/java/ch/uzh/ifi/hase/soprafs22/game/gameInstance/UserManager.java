package ch.uzh.ifi.hase.soprafs22.game.gameInstance;

import ch.uzh.ifi.hase.soprafs22.game.gameInstance.player.Player;
import ch.uzh.ifi.hase.soprafs22.rest.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.websocket.SpringContext;
import ch.uzh.ifi.hase.soprafs22.websocket.controller.UpdateController;
import ch.uzh.ifi.hase.soprafs22.websocket.dto.UpdateDTO;


import java.util.ArrayList;
import java.util.Hashtable;

    /*
    UserManager
    This class maps users to players and vice versa
    Expects the _users and _players lists to be ordered accordingly so that the indexes correspond to the same (logical) entity in both lists
    */

public class UserManager {

    private final UpdateController updateController = SpringContext.getBean(UpdateController.class);
    private final UserRepository userRepository = SpringContext.getBean(UserRepository.class);


    private ArrayList<Player> _players;
    private ArrayList<User> _correspondingUsers;


    private Hashtable<Long, Player> _userIdToPlayer = new Hashtable<Long, Player>();
    private Hashtable<Player, User> _playerToUser = new Hashtable<Player, User>();

    public UserManager(ArrayList<Player> players, ArrayList<User> users){
        this._correspondingUsers = users;
        this._players = players;

        for (int i = 0; i<4; i++) {
            _userIdToPlayer.put(_correspondingUsers.get(i).getId(), _players.get(i));
            _playerToUser.put(_players.get(i), _correspondingUsers.get(i));
        }
    }

    public void sendUpdateToPlayer(Player player, UpdateDTO update){
        String usertoken = getUserFromPlayer(player).getToken();
        updateController.sendUpdateToUser(usertoken, update);
    }

    public void sendUpdateToAll(UpdateDTO update){
        for (Player p : _players){
            String usertoken = getUserFromPlayer(p).getToken();
            updateController.sendUpdateToUser(usertoken, update);
        }
    }

    public Player getPlayerFromUserToken(String usertoken){

        Long id = userRepository.findByToken(usertoken).getId();
        Player player = _userIdToPlayer.get(id);
        System.out.println("Dict:" + _userIdToPlayer);
        return player;
    }

    public Player getPlayerFromUser(User user){
        return _userIdToPlayer.get(user.getId());
    }

    public User getUserFromPlayer(Player player){
        return _playerToUser.get(player);
    }

    public User getUserFromUsertoken(String usertoken){
        return userRepository.findByToken(usertoken);
    }

}
