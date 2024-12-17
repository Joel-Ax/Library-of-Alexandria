package com.example.libraryofalexandria.Services;

import com.example.libraryofalexandria.Exceptions.ResourceNotFoundException;
import com.example.libraryofalexandria.Models.User;
import com.example.libraryofalexandria.Repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  // Konstruktor som injicerar UserRepository och PasswordEncoder
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = new BCryptPasswordEncoder(); // Skapar en instans av BCryptPasswordEncoder
  }

  // Hämta alla användare
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  // Skapa en ny användare
  public User createUser(User user) {
    // Kontrollera om lösenordet är giltigt
    if (!isValidPassword(user.getPassword())) {
      throw new IllegalArgumentException("Password must meet security requirements");
    }

    // Hasha lösenordet innan användaren sparas
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  // Hämta användare efter ID
  public User getUserById(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
  }

  // Uppdatera en användare
  public User updateUser(Long id, User user) {
    User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

    Optional.ofNullable(user.getFirstName()).ifPresent(existingUser::setFirstName);
    Optional.ofNullable(user.getLastName()).ifPresent(existingUser::setLastName);
    Optional.ofNullable(user.getEmail()).ifPresent(existingUser::setEmail);

    // Om lösenordet har ändrats, hasha det
    if (user.getPassword() != null) {
      // Kontrollera om det nya lösenordet är giltigt
      if (!isValidPassword(user.getPassword())) {
        throw new IllegalArgumentException("Password must meet security requirements");
      }
      existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    return userRepository.save(existingUser);
  }

  // Ta bort en användare
  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    userRepository.delete(user);
  }

  // Validera lösenordets komplexitet
  private boolean isValidPassword(String password) {
    return password != null &&
            password.length() >= 12 && // Minst 12 tecken
            password.matches(".*[A-Z].*") && // Innehåller minst en versal
            password.matches(".*[a-z].*") && // Innehåller minst en gemen
            password.matches(".*\\d.*") && // Innehåller minst en siffra
            password.matches(".*[!@#$%^&*(),.?\":{}|<>].*"); // Innehåller minst ett specialtecken
  }
}
