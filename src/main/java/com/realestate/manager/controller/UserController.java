package com.realestate.manager.controller;

import com.realestate.manager.model.entity.User;
import com.realestate.manager.service.UserService;
import org.springframework.http.HttpStatus;
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
        try{
            User user = userService.authenticate(username, password);
            return ResponseEntity.ok(user);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        try{
            User newUser = userService.register(user);
            return ResponseEntity.ok(newUser);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }






    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam Integer reqId)
    {
        try{
            List<User> users = userService.getAllUser(reqId);
            return ResponseEntity.ok(users);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user, @RequestParam Integer reqId){
        try{
            User savedUser = userService.createUser(user,reqId);
            return ResponseEntity.ok(savedUser);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User user, @RequestParam Integer reqId){
        try{
            User updateduser = userService.updateUser(user, reqId, id);
            return ResponseEntity.ok(updateduser);

        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, @RequestParam Integer reqId){
        try{
            userService.deleteUser(id, reqId);
            return ResponseEntity.ok("User has been deleted");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
