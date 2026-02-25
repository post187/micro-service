package com.example.Service.Impl;

import com.example.Exception.wrapper.RoleNotFoundException;
import com.example.Exception.wrapper.UserNotFoundException;
import com.example.Repository.RoleRepository;
import com.example.Repository.UserRepository;
import com.example.Service.RoleService;
import com.example.model.Entity.Role;
import com.example.model.Entity.RoleName;
import com.example.model.Entity.User;
import com.example.model.dto.Response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.Service.Impl.UserServiceImpl.mapToRoleName;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<Role> findByName(RoleName name) {
        return Optional.ofNullable(roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException("Role Not Found with name: " + name)));
    }

    @Override
    public boolean assignRole(Long id, String roleName) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        Role role = roleRepository.findByName(mapToRoleName(roleName))
                .orElseThrow(() -> new RoleNotFoundException("Role not found in system: " + roleName));

        if (existingUser.getRoles().contains(role)) {
            return false;
        }
        existingUser.getRoles().add(role);
        userRepository.save(existingUser);

        return true;
    }

    @Override
    public boolean revokeRole(Long id, String roleName) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        Role role = roleRepository.findByName(mapToRoleName(roleName))
                .orElseThrow(() -> new RoleNotFoundException("Role not found in system: " + roleName));

        if (!existingUser.getRoles().contains(role)) {
            return false;
        }
        existingUser.getRoles().remove(role);
        userRepository.save(existingUser);

        return true;
    }

    @Override
    public List<String> getUserRoles(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        List<String> roleNames = new ArrayList<>();
        user.getRoles().forEach(userRole -> roleNames.add(userRole.getRoleName().toString()));
        return roleNames;
    }
}
