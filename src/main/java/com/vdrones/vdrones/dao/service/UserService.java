package com.vdrones.vdrones.dao.service;


import com.vdrones.vdrones.dao.entity.post.SubmittedApplicationsEntity;
import com.vdrones.vdrones.dao.entity.users.UserEntity;
import com.vdrones.vdrones.dao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@Transactional
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.of(userRepository.findByUsername(username)).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserEntity findByUserName(String username){
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public UserEntity findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(String.format("User with ID = %s, not found!")));
    }

    @Transactional(readOnly = true)
    public List<UserEntity> allUsers() {
        return (List<UserEntity>) userRepository.findAll();
    }

    //@Transactional
    public UserEntity saveUser(UserEntity user) {
        user.setRole("ROLE_USER");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public UserEntity saveUserWithSubmittedApplication(UserEntity user, SubmittedApplicationsEntity submittedApplications) {
        //user.setRole("ROLE_USER");
        //user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        // Привязываем заявку к пользователю
        submittedApplications.setUser(user);


        if (user.getSubmittedApplications() == null) {
            user.setSubmittedApplications(new ArrayList<>());
        }
        user.getSubmittedApplications().add(submittedApplications);

        // Сохраняем пользователя
        //userRepository.save(user);

        return user;
    }

    @Transactional
    public boolean deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);

            return true;
        }

        return false;
    }
}