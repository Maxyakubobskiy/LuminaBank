package com.lumina_bank.userservice.service;

import com.lumina_bank.userservice.dto.UserCreateDto;
import com.lumina_bank.userservice.dto.UserUpdateDto;
import com.lumina_bank.userservice.enums.Role;
import com.lumina_bank.userservice.model.Address;
import com.lumina_bank.userservice.model.User;
import com.lumina_bank.userservice.repository.UserRepository;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User createUser(UserCreateDto userDto){

        if (userRepository.existsByEmail(userDto.email())){
            throw new IllegalArgumentException("Email already exists");
        }

        Address address = Address.builder().
                street(userDto.street()).
                city(userDto.city()).
                country(userDto.country()).
                houseNumber(userDto.houseNumber()).
                zipCode(userDto.zipCode()).
                build();

        User user = User.builder().
                email(userDto.email()).
                password(userDto.password()).
                firstName(userDto.firstName()).
                lastName(userDto.lastName()).
                phoneNumber(userDto.phoneNumber()).
                birthDate(userDto.birthDate()).
                address(address).
                role(Role.USER).
                build();

         return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById (Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
    }

    @Transactional
    public User updateUser(Long id, UserUpdateDto userDto){
        User user = getUserById(id);

        if (!userDto.email().equals(user.getEmail()) && userRepository.existsByEmail(userDto.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setPhoneNumber(userDto.phoneNumber());
        user.setBirthDate(userDto.birthDate());
        user.setEmail(userDto.email());

        Address address = user.getAddress() != null ? user.getAddress() : Address.builder().
                street(userDto.street()).
                city(userDto.city()).
                country(userDto.country()).
                houseNumber(userDto.houseNumber()).
                zipCode(userDto.zipCode()).
                build();

        user.setAddress(address);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id){
        if(!userRepository.existsById(id))
            throw new NoSuchElementException("User with id " + id + " not found");
        userRepository.deleteById(id);
    }
}
