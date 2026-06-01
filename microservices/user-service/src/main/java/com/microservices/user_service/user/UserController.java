package com.microservices.user_service.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins="*")

public class UserController {

    private final UserService userService;

    public  UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password){
        User user = userService.authenticate(username, password);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        User newUser = userService.register(user);
        return ResponseEntity.ok(newUser);
    }

    @GetMapping("/debug")
    public ResponseEntity<?> debugUser(@RequestParam String username, @RequestParam String rawPassword) {
        try {
            User user = userService.authenticate(username, rawPassword);
            return ResponseEntity.ok("Valid! User: " + user.getUsername() + ", Hash: " + user.getPasswordHash());
        } catch (Exception e) {
            java.util.Optional<User> opt = userService.getUserByUsername(username);
            if (opt.isEmpty()) return ResponseEntity.ok("User not found in DB.");
            User u = opt.get();
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder enc = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            String hash = u.getPasswordHash();
            return ResponseEntity.ok("Error was: " + e.getMessage() + "\n" +
                "DB Hash Length: " + (hash == null ? "null" : hash.length()) + "\n" +
                "DB Hash: " + hash + "\n" +
                "Does it match rawPassword? " + enc.matches(rawPassword, hash));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam Integer reqId) {
        List<User> users = userService.getAllUser(reqId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        java.util.Map<String, String> response = new java.util.HashMap<>();
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("roleName", user.getRole().getRoleName());
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user, @RequestParam Integer reqId){
        User savedUser = userService.createUser(user,reqId);
        return ResponseEntity.ok(savedUser);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User user, @RequestParam Integer reqId){
        User updateduser = userService.updateUser(user, reqId, id);
        return ResponseEntity.ok(updateduser);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, @RequestParam Integer reqId){
        userService.deleteUser(id, reqId);
        return ResponseEntity.ok("User has been deleted");
    }

}
