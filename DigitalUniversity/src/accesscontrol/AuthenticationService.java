package accesscontrol;

import java.util.HashMap;

/**
 * AuthenticationService for managing user authentication
 * Author: [Your Name]
 */
public class AuthenticationService {
    private static AuthenticationService instance;
    private HashMap<String, User> users; // username -> User mapping
    private User currentUser;
    
    // Private constructor for singleton pattern
    private AuthenticationService() {
        this.users = new HashMap<>();
        this.currentUser = null;
    }
    
    /**
     * Get singleton instance
     */
    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }
    
    /**
     * Register a new user
     */
    public boolean registerUser(User user) {
        if (user == null || user.getUsername() == null) {
            return false;
        }
        
        if (users.containsKey(user.getUsername())) {
            return false; // Username already exists
        }
        
        users.put(user.getUsername(), user);
        return true;
    }
    
    /**
     * Authenticate user
     */
    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        
        User user = users.get(username);
        if (user != null && user.isActive() && user.validatePassword(password)) {
            this.currentUser = user;
            return user;
        }
        
        return null;
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Check if current user has a specific role
     */
    public boolean hasRole(String role) {
        return currentUser != null && currentUser.hasRole(role);
    }
    
    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        return users.get(username);
    }
    
    /**
     * Get all users
     */
    public HashMap<String, User> getAllUsers() {
        return users;
    }
    
    /**
     * Update user password
     */
    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        User user = users.get(username);
        if (user != null && user.validatePassword(oldPassword)) {
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }
    
    /**
     * Delete user
     */
    public boolean deleteUser(String username) {
        if (users.containsKey(username)) {
            users.remove(username);
            return true;
        }
        return false;
    }
}