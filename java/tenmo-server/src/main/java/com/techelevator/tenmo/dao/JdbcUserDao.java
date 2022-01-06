package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    private JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;

    public JdbcUserDao(JdbcTemplate jdbcTemplate, AccountDao accountDao) {

        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = accountDao;
    }

    @Override
    public int findIdByUsername(String username) {
        String sql = "SELECT user_id FROM users WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null) {
            return id;
        } else {
            return -1;
    }
    }

    @Override
    public List<User> findAll() {  //will use in transfer method to select user to send
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM users;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }
        return users;
    }

    @Override
    public UserDTO[] findAllDTOs() {  //will use in transfer method to select user to send
        List<UserDTO> users = new ArrayList<>();
        String sql = "SELECT user_id, username FROM users";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            UserDTO user = new UserDTO();
            user.setId(results.getInt("user_id"));
            user.setUsername(results.getString("username"));
            user.setAccountId(accountDao.getAccountByUserId(user.getId()).getAccountId());
            users.add(user);
        }

        UserDTO[] userArray = new UserDTO[users.size()];
        userArray = users.toArray(userArray);
        return userArray;
    }


    @Override
    public UserDTO[] findAllNotPrincipal(Principal principal) {  //will use in transfer method to select user to send
        List<UserDTO> tempUserList = new ArrayList<>();
        String sql = "SELECT user_id, username FROM users WHERE username != ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, principal.getName());
        while(results.next()) {
            UserDTO user = new UserDTO();
            user.setId(results.getInt("user_id"));
            user.setUsername(results.getString("username"));
            user.setAccountId(accountDao.getAccountByUserId(user.getId()).getAccountId());

            tempUserList.add(user);
        }

        UserDTO[] userArray = new UserDTO[tempUserList.size()];
        userArray = tempUserList.toArray(userArray);
        return userArray;
    }


    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM users WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToUser(rowSet);
            }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public boolean create(String username, String password) {

        // create user
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
        } catch (DataAccessException e) {
            return false;
                }

        // create account
        sql = "INSERT INTO accounts (user_id, balance) values(?, ?)";
        try {
            jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
        } catch (DataAccessException e) {
            return false;
        }

        return true;
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }
}
