package com.example.libraryofalexandria.Controllers;

import com.example.libraryofalexandria.Models.User;
import com.example.libraryofalexandria.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<User> createUser(@RequestBody User user) {
    User createdUser = userService.createUser(user);
    return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    try {
      User user = userService.getUserById(id);
      return ResponseEntity.ok(user);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
    try {
      User updatedUser = userService.updateUser(id, user);
      return ResponseEntity.ok(updatedUser);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<User> deleteUser(@PathVariable Long id){
    try {
      userService.deleteUser(id);
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e ) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

}
