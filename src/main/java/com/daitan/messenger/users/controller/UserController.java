package com.daitan.messenger.users.controller;

import com.daitan.messenger.constants.ConstantsUtils;
import com.daitan.messenger.exception.SQLException;
import com.daitan.messenger.exception.UserNotFoundException;
import com.daitan.messenger.login.CurrentUser;
import com.daitan.messenger.login.model.UserPrincipal;
import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.model.Message;
import com.daitan.messenger.message.service.ChatService;
import com.daitan.messenger.message.service.MessageService;
import com.daitan.messenger.users.model.*;
import com.daitan.messenger.users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.*;

@RestController
@EnableWebMvc
@RequestMapping("/api/v1")
public class UserController {

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    private ChatService chatService;

    private MessageService messageService;
//
//    @Autowired
//    private SessionRegistry sessionRegistry;

    public UserController(UserService userService, ChatService chatService, MessageService messageService) {
        this.userService = userService;
        this.chatService = chatService;
        this.messageService = messageService;
    }


//    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
//    @RequestMapping(value = "/users/loggedusers", method = RequestMethod.GET)
//    public List<Object> getLoggedUsers() {
//        List<Object> principals = sessionRegistry.getAllPrincipals();
//        return principals;
//    }
//
//    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
//    @RequestMapping(value = "/users/logout/{email:.+}", method = RequestMethod.GET)
//    public String killUserSession(@PathVariable(value = "email") String email) {
//        for (Object principal : sessionRegistry.getAllPrincipals()) {
//            UserDetails userDetails = (UserDetails) principal;
//            if (userDetails.getUsername().equals(email)) {
//                sessionRegistry.getAllPrincipals().remove(principal);
//                for (SessionInformation information : sessionRegistry.getAllSessions(userDetails, true)) {
//                    sessionRegistry.removeSessionInformation(information.getSessionId());
//                    information.expireNow();
//                }
//            }
//        }
//        return "Session killed for user: " + email;
//    }

    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
    @RequestMapping(value = "/users/current", method = RequestMethod.GET)
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getEmail(), currentUser.getName(), currentUser.getAuthorities());
        return userSummary;
    }

    @RequestMapping(value = "/users/emailavailability", method = RequestMethod.GET)
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        try {
            Boolean isAvailable = !userService.existsByEmail(email);
            return new UserIdentityAvailability(isAvailable);
        } catch (SQLException e) {
            logger.error("Couldn't perform database operation");
            throw new SQLException("500", "Couldn't perform database operation, please try again!!!");
        }
    }

    @RequestMapping(value = "/users/{email:.+}", method = RequestMethod.GET)
    public UserProfile getUserProfile(@PathVariable(value = "email") String email) {
        try {
            Optional<User> userOptional = userService.findByEmail(email);
            User user = userOptional.orElseThrow(() -> new UserNotFoundException("404", "User not found please try again!!"));
            UserProfile userProfile = new UserProfile(user.getId(), user.getEmail(), user.getNome(), user.getRole());
            return userProfile;
        } catch (SQLException e) {
            logger.error("Couldn't perform database operation");
            throw new SQLException("500", "Couldn't perform database operation, please try again!!!");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<PagedResponse<UserProfile>> getAllUsers(
            @RequestParam(value = "page", defaultValue = ConstantsUtils.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = ConstantsUtils.DEFAULT_PAGE_SIZE) int size) {
        try {
            PagedResponse<UserProfile> userProfiles = this.userService.findAll(page, size);
            return new ResponseEntity<>(userProfiles, HttpStatus.PARTIAL_CONTENT);
        } catch (SQLException e) {
            logger.error("Couldn't perform database operation");
            throw new SQLException("500", "Couldn't perform database operation, please try again!!!");
        }
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            this.userService.insert(user);
        } catch (SQLException e) {
            logger.error("Couldn't insert user in database");
            throw new SQLException("500", "Couldn't insert user in database, please try again!!!");
        }
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/users", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<Void> updateUser(@RequestBody User users) {
        try {
            this.userService.save(users);
        } catch (SQLException e) {
            logger.error("Couldn't save user in database");
            throw new SQLException("500", "Couldn't save user in database, please try again!!!");
        }
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/users", method = RequestMethod.DELETE)
    public ResponseEntity<User> deleteUser(@RequestParam(value = "email") String email) {
        return Optional.of(this.userService.findIdByEmail(email)).map(user -> {
            this.userService.deleteById(user.getId());
            return ResponseEntity.ok(user);
        }).orElseThrow(() -> new UserNotFoundException("404", "User not found please try again!!"));
    }

    @RequestMapping(value = "/chats", method = RequestMethod.PUT)
    public HttpEntity<Chat> saveChat(@RequestBody Chat[] chats){
        try {
            List chatList = Arrays.asList(chats);
            chatService.createChatOneToOne(chatList);
            return new ResponseEntity(chats, HttpStatus.CREATED);
        }
        catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/chats/{chatId}/messages", method = RequestMethod.GET)
    public HttpEntity<Resources<Resource<Message>>> findMessages(@PathVariable("chatId") String chatId){
        List<Message> message = messageService.findMessageByChatId(chatId);
        Collections.sort(message, Comparator.comparing(Message::getTimestamp));
        return new ResponseEntity(new Resources(message), HttpStatus.ACCEPTED);
    }


    @RequestMapping(value = "/chats/{userId}", method = RequestMethod.GET)
    public HttpEntity<Resources<Resource<Chat>>> findChats(@PathVariable("userId") String userId){
        List<Chat> chats = chatService.findChatByUserId(userId);
        return new ResponseEntity(chats,  HttpStatus.OK);
    }

}
