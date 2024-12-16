package mk.ukim.finki.wp.lab.service.impl;

import mk.ukim.finki.wp.lab.model.User;
import mk.ukim.finki.wp.lab.model.enums.Role;
import mk.ukim.finki.wp.lab.model.exceptions.InvalidArgumentsException;
import mk.ukim.finki.wp.lab.model.exceptions.InvalidUserCredentialsException;
import mk.ukim.finki.wp.lab.model.exceptions.PasswordsDoNotMatchException;
import mk.ukim.finki.wp.lab.model.exceptions.UsernameAlreadyExistsException;
import mk.ukim.finki.wp.lab.repository.jpa.UserRepository;
import mk.ukim.finki.wp.lab.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User login(String username, String password) {
        if(username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidArgumentsException();
        }
        return userRepository.findByUsernameAndPassword(username,password).orElseThrow(InvalidUserCredentialsException::new);
    }

    @Override
    public User register(String username, String password, String repeatPassword, String firstName, String lastName, Role role) {
        if(username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidArgumentsException();
        }
        if(!password.equals(repeatPassword)) {
            throw new PasswordsDoNotMatchException();
        }

        if(this.userRepository.findByUsername(username).isPresent() || !this.userRepository.findByUsernameAndPassword(username,password).isEmpty()) {
            throw new UsernameAlreadyExistsException(username);
        }

        User user = new User(username, password, firstName, lastName, role);
        return userRepository.save(user);
    }
}