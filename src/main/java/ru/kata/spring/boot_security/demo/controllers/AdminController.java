package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Controller
public class AdminController {
    private UserService userService;
    private RoleService roleService;

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String jj() {
        return "redirect:/login";
    }

    @GetMapping("/admin")
    public String helloAdmin(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("user", user);
        model.addAttribute("newUser", new User());
        model.addAttribute("rolesList", roleService.getRoles());
        return "index1";
    }

    @DeleteMapping("admin/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        userService.deleteUser(id);
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("rolesList", roleService.getRoles());
        return "_tab1";
    }

    @PostMapping("/admin/saveUser")
    public String save(@RequestBody User user, Model model){
        updateUserFromRequestBody(user);
        userService.saveUser(user);
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("rolesList", roleService.getRoles() );
        return "_tab1";
    }

        @PostMapping("/admin/editUser")
        public String edit(@RequestBody User user, Model model) {
        updateUserFromRequestBody(user);
        userService.saveUser(user);
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("rolesList", roleService.getRoles() );
       return "_tab1";
    }

    @GetMapping("/{tab}")
    public String tab(@PathVariable String tab, Model model) {
        if (tab.equals("tab1")) {
            model.addAttribute("users", userService.findAllUsers());
            model.addAttribute("rolesList", roleService.getRoles());
        } else if (tab.equals("tab2")) {
            model.addAttribute("newUser", new User());
            model.addAttribute("rolesList", roleService.getRoles());
        }
        return "_" + tab;
    }

    @GetMapping("/admin/{userId}")
    @ResponseBody
    public User findById(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }

    private User updateUserFromRequestBody(User user) {
        Collection<Role> list = user.getRoles();
        Collection<Role> roles = new ArrayList<>();
        for (Role role: list) {
            roles.add(roleService.findRoleByName(role.getName()).orElseThrow());
        }
        user.setRoles(roles);
        return user;
    }







     /* @PostMapping ("/admin/edit")
    public String editUser(HttpServletRequest request) {
        Long id = Long.parseLong(request.getParameter("id"));
        String firstName = request.getParameter("firstName");
        String secondName = request.getParameter("secondName");
        String age = request.getParameter("age");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String roles = request.getParameter("roles");

        System.out.println(id);
        System.out.println(firstName);
        System.out.println(secondName);
        System.out.println(age);
        System.out.println(password);
        System.out.println(email);
        System.out.println(roles);
        return "redirect:/admin/allUsers";
    }*/
}
