package com.example.libraryofalexandria.DTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @Id
    private Long id;
    
    private String firstName;
    private String lastName;
    private String email;
    private String memberNumber;
}
