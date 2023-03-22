package ru.kata.spring.boot_security.demo.controllers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import ru.kata.spring.boot_security.demo.DTO.UserDTO;
import ru.kata.spring.boot_security.demo.Util.UserErrorResponse;
import ru.kata.spring.boot_security.demo.Util.UserNotCreatedException;
import ru.kata.spring.boot_security.demo.Util.UserNotFoundException;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;


@RestController
public class AdminRestController {
    private UserService userService;
    private RoleService roleService;
    private ModelMapper modelMapper;

    @Autowired
    public AdminRestController(UserService userService, RoleService roleService, ModelMapper modelMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
    }


    @GetMapping("/hello")
    public String demo() {
        return "Hello";
    }

    @PostMapping("/admin/saveUser1")
    public ResponseEntity<HttpStatus> save(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            bindingResult.getAllErrors()
                    .forEach(error -> errorMsg
                            .append(error.getCode())
                            .append(" - ")
                            .append(error.getDefaultMessage())
                            .append(";"));
            throw new UserNotCreatedException(errorMsg.toString());
        }
        userService.saveUser(createUserFromDTO(userDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @GetMapping("/admin/{userId}1")
    @ResponseBody
    public UserDTO findById(@PathVariable Long userId) {
        return createDTOfromUser(userService.findUserById(userId));
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotFoundException e) {
        UserErrorResponse userErrorResponse = new UserErrorResponse("User with this id wasnt found",
                System.currentTimeMillis());
        return new ResponseEntity<>(userErrorResponse, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException e) {
        UserErrorResponse userErrorResponse = new UserErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(userErrorResponse, HttpStatus.BAD_REQUEST);

    }

    private User createUserFromDTO(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

   private UserDTO createDTOfromUser(User user) {
        return  modelMapper.map(user, UserDTO.class);
   }
}
