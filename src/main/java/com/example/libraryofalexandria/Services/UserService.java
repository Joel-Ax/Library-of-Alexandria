package com.example.libraryofalexandria.Services;

import com.example.libraryofalexandria.Models.User;
import com.example.libraryofalexandria.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  private UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User createUser(User user) {
    return userRepository.save(user);
  }

  public User getUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  public User updateUser(Long id, User user){
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));


    Optional.ofNullable(user.getFirstName()).ifPresent(existingUser::setFirstName);
    Optional.ofNullable(user.getLastName()).ifPresent(existingUser::setLastName);
    Optional.ofNullable(user.getEmail()).ifPresent(existingUser::setEmail);

    return userRepository.save(existingUser);
  }

  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));
    userRepository.delete(user);
  }
}
