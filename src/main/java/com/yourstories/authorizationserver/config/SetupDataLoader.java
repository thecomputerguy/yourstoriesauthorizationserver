package com.yourstories.authorizationserver.config;

import com.yourstories.authorizationserver.model.Privilege;
import com.yourstories.authorizationserver.model.Role;
import com.yourstories.authorizationserver.model.User;
import com.yourstories.authorizationserver.repositories.PrivilegeRepository;
import com.yourstories.authorizationserver.repositories.RoleRepository;
import com.yourstories.authorizationserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // API

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // == create initial privileges
        final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");
        //new privileges...
        final Privilege viewAllCategories = createPrivilegeIfNotFound("VIEW_ALL_CATEGORIES");
        final Privilege viewCategory = createPrivilegeIfNotFound("VIEW_CATEGORY");
        final Privilege createCategory = createPrivilegeIfNotFound("CREATE_CATEGORY");
        final Privilege updateCategory = createPrivilegeIfNotFound("UPDATE_CATEGORY");
        final Privilege deleteCategory = createPrivilegeIfNotFound("DELETE_CATEGORY");
        final Privilege viewAllComments = createPrivilegeIfNotFound("VIEW_ALL_COMMENTS");
        final Privilege viewAllCommentsByPost = createPrivilegeIfNotFound("VIEW_ALL_COMMENTS_BY_POST");
        final Privilege viewComment = createPrivilegeIfNotFound("VIEW_COMMENT");
        final Privilege createComment = createPrivilegeIfNotFound("CREATE_COMMENT");
        final Privilege updateOwnComment = createPrivilegeIfNotFound("UPDATE_OWN_COMMENT");
        final Privilege updateOthersComment = createPrivilegeIfNotFound("UPDATE_OTHERS_COMMENT");
        final Privilege deleteOwnComment = createPrivilegeIfNotFound("DELETE_OWN_COMMENT");
        final Privilege deleteOthersComment = createPrivilegeIfNotFound("DELETE_OTHERS_COMMENT");
        final Privilege viewAllPosts = createPrivilegeIfNotFound("VIEW_ALL_POSTS");
        final Privilege viewPost = createPrivilegeIfNotFound("VIEW_POST");
        final Privilege createPost = createPrivilegeIfNotFound("CREATE_POST");
        final Privilege updateOwnPost = createPrivilegeIfNotFound("UPDATE_OWN_POST");
        final Privilege updateOthersPost = createPrivilegeIfNotFound("UPDATE_OTHERS_POST");
        final Privilege deleteOwnPost = createPrivilegeIfNotFound("DELETE_OWN_POST");
        final Privilege deleteOthersPost = createPrivilegeIfNotFound("DELETE_OTHERS_POST");
        final Privilege readWriteAll = createPrivilegeIfNotFound("READ_WRITE_ALL");

        // == create initial roles
        final List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege,
                viewAllCategories, viewCategory, createCategory, updateCategory, deleteCategory, viewAllComments,
                viewAllCommentsByPost, viewComment, createComment, updateOwnComment, updateOthersComment, deleteOwnComment,
                deleteOthersComment, viewAllPosts, viewPost, createPost, updateOwnPost, updateOthersPost, deleteOwnPost,
                deleteOthersPost, readWriteAll);
        final List<Privilege> userPrivileges = Arrays.asList(readPrivilege, passwordPrivilege, viewAllCategories, viewCategory,
                viewAllCommentsByPost, viewComment, createComment, updateOwnComment, deleteOwnComment, viewAllPosts, viewPost,
                createPost, updateOwnPost, deleteOwnPost);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", userPrivileges);

        final Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        final User user = new User();
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setPassword(passwordEncoder.encode("test"));
        user.setEmail("test@test.com");
        user.setRoles(Arrays.asList(adminRole));
        user.setEnabled(true);
        userRepository.save(user);

        alreadySetup = true;
    }

    @Transactional
    private final Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    private final Role createRoleIfNotFound(final String name, final Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }

}