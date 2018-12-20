package com.daitan.messenger.users.controller;

import com.daitan.messenger.constants.ConstantsUtils;
import com.daitan.messenger.exception.SQLException;
import com.daitan.messenger.exception.UserAlreadyExistsException;
import com.daitan.messenger.exception.UserInvalidException;
import com.daitan.messenger.exception.UserNotFoundException;
import com.daitan.messenger.login.CurrentUser;
import com.daitan.messenger.login.model.UserPrincipal;
import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.model.ChatResponse;
import com.daitan.messenger.message.model.Message;
import com.daitan.messenger.message.service.ChatService;
import com.daitan.messenger.message.service.MessageService;
import com.daitan.messenger.users.model.PagedResponse;
import com.daitan.messenger.users.model.User;
import com.daitan.messenger.users.model.UserIdentityAvailability;
import com.daitan.messenger.users.model.UserProfile;
import com.daitan.messenger.users.model.UserSummary;
import com.daitan.messenger.users.service.UserService;
import org.apache.logging.log4j.util.Strings;
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@EnableWebMvc
@RequestMapping("/api/v1")
public class UserController {

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    private ChatService chatService;

    private MessageService messageService;

    public UserController(UserService userService, ChatService chatService, MessageService messageService) {
        this.userService = userService;
        this.chatService = chatService;
        this.messageService = messageService;
    }


    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN') OR hasRole('AUDITOR')")
    @RequestMapping(value = "/users/current", method = RequestMethod.GET)
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return new UserSummary(currentUser.getId(), currentUser.getEmail(), currentUser.getName(), currentUser.getAuthorities());
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

    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN') OR hasRole('AUDITOR')")
    @RequestMapping(value = "/users/{email:.+}", method = RequestMethod.GET)
    public UserProfile getUserProfile(@PathVariable(value = "email") String email) {
        try {
            Optional<User> userOptional = userService.findByEmail(email);
            User user = userOptional.orElseThrow(() -> new UserNotFoundException("404", "User not found please try again!!"));
            return new UserProfile(user.getId(), user.getEmail(), user.getName(), user.getRole());
        } catch (SQLException e) {
            logger.error("Couldn't perform database operation");
            throw new SQLException("500", "Couldn't perform database operation, please try again!!!");
        }
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<PagedResponse<UserProfile>> getUsers(
            @RequestParam(value = "name", defaultValue = ConstantsUtils.EMPTY_STRING) String name,
            @RequestParam(value = "lastName", defaultValue = ConstantsUtils.EMPTY_STRING) String lastName,
            @RequestParam(value = "page", defaultValue = ConstantsUtils.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = ConstantsUtils.DEFAULT_PAGE_SIZE) int size) {
        try {
            PagedResponse<UserProfile> userProfiles = this.userService.findByNameAndOrLastName(name, lastName, page, size);
            return new ResponseEntity<>(userProfiles, HttpStatus.PARTIAL_CONTENT);
        } catch (SQLException e) {
            logger.error("Couldn't perform database operation");
            throw new SQLException("500", "Couldn't perform database operation, please try again!!!");
        }
    }

    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN') OR hasRole('AUDITOR')")
    @RequestMapping(value = "/users", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if(Objects.isNull(user)|| Strings.isBlank(user.getEmail())  || Strings.isBlank(user.getPassword()) || Strings.isBlank(user.getName())) {
            logger.error("Invalid user");
            throw new UserInvalidException("403", "User is invalid, please check if all necessary fields are filled");
        }
        try {
            if (userService.findByEmail(user.getEmail()).isPresent()) {
                throw new UserAlreadyExistsException("409", "User with this email already exists!");
            }
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
        return this.userService.findIdByEmail(email).map(user -> {
            this.userService.deleteById(user.getId());
            return ResponseEntity.ok(user);
        }).orElseThrow(() -> new UserNotFoundException("404", "User not found please try again!!"));
    }

    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN') OR hasRole('AUDITOR')")
    @RequestMapping(value = "/chats", method = RequestMethod.PUT)
    public HttpEntity<Chat> saveChat(@RequestBody Chat[] chats){
        try {
            List<Chat> chatList = Arrays.asList(chats);
            this.chatService.createChatOneToOne(chatList);
            return new ResponseEntity(chats, HttpStatus.CREATED);
        }
        catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN') OR hasRole('AUDITOR')")
    @RequestMapping(value = "/chats/{chatId}", method = RequestMethod.PUT)
    public HttpEntity<Chat> updateChat(@RequestBody Chat[] chats,
                                       @PathVariable(value = "chatId", required = true) String chatId){
        try {
            List<Chat> chatList = Arrays.asList(chats);
            this.chatService.updateChat(chatList, chatId);
            return new ResponseEntity(chats, HttpStatus.CREATED);
        }
        catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PreAuthorize("hasRole('AUDITOR')")
    @RequestMapping(value = "/chats", method = RequestMethod.GET)
    public ResponseEntity<PagedResponse<ChatResponse>> findAllChats(
            @RequestParam(value = "emitter", defaultValue = ConstantsUtils.EMPTY_STRING) String emitter,
            @RequestParam(value = "receptor", defaultValue = ConstantsUtils.EMPTY_STRING) String receptor,
            @RequestParam(value = "content", defaultValue = ConstantsUtils.EMPTY_STRING) String content,
            @RequestParam(value = "page", defaultValue = ConstantsUtils.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = ConstantsUtils.DEFAULT_PAGE_SIZE) int size){
        try {
            PagedResponse<ChatResponse> chat = this.chatService.findAllChats(emitter, receptor, content, page, size);
            return new ResponseEntity<>(chat, HttpStatus.PARTIAL_CONTENT);
        }catch (SQLException e) {
            logger.error("Couldn't perform database operation");
            throw new SQLException("500", "Couldn't perform database operation, please try again!!!");
        }
    }

    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN') OR hasRole('AUDITOR')")
    @RequestMapping(value = "/chats/{chatId}/{email}/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserProfile>> findUsersByChat(
            @PathVariable(value = "chatId", required = true) String chatId,
            @PathVariable(value = "email", required = true) String email
    ){
        try {
            List<UserProfile> userProfiles = this.chatService.findUsersByChat(chatId).stream()
                    .filter(userProfile -> !userProfile.getEmail().equals(email))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userProfiles, HttpStatus.OK);
        }catch (SQLException e) {
            logger.error("Couldn't perform database operation");
            throw new SQLException("500", "Couldn't perform database operation, please try again!!!");
        }
    }

//    @PreAuthorize("hasRole('AUDITOR')")
    @RequestMapping(value = "/chats/messages", method = RequestMethod.GET)
    public ResponseEntity<PagedResponse<Message>> findAllMessages(
            @RequestParam(value = "emitter", defaultValue = ConstantsUtils.EMPTY_STRING) String emitter,
            @RequestParam(value = "receptor", defaultValue = ConstantsUtils.EMPTY_STRING) String receptor,
            @RequestParam(value = "page", defaultValue = ConstantsUtils.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = ConstantsUtils.DEFAULT_PAGE_SIZE) int size){
        try {
            PagedResponse<Message> message = this.messageService.findAllMessages(emitter, receptor, page, size);
            if(!Objects.isNull(message)){
                message.getContent().sort(Comparator.comparing(Message::getTimestamp));
            }
            return new ResponseEntity<>(message, HttpStatus.PARTIAL_CONTENT);
        }catch (SQLException e) {
            logger.error("Couldn't perform database operation");
            throw new SQLException("500", "Couldn't perform database operation, please try again!!!");
        }
    }

    @RequestMapping(value = "/chats/{chatId}/messages", method = RequestMethod.GET)
    public HttpEntity<Resources<Resource<Message>>> findMessagesByChatId(@PathVariable("chatId") String chatId) {
        try {
            List<Message> message = this.messageService.findMessageByChatId(chatId);
            if(!message.isEmpty()){
                message.sort(Comparator.comparing(Message::getTimestamp));
            }
            message.sort(Comparator.comparing(Message::getTimestamp));
            return new ResponseEntity(new Resources(message), HttpStatus.ACCEPTED);
        } catch (SQLException e) {
            logger.error("Couldn't perform database operation");
            throw new SQLException("500", "Couldn't perform database operation, please try again!!!");
        }
    }


    @RequestMapping(value = "/chats/{userId}", method = RequestMethod.GET)
    public HttpEntity<Resources<Resource<Chat>>> findChatsByUserId(@PathVariable("userId") String userId) {
        try {
            List<Chat> chats = chatService.findChatByUserId(userId);
            return new ResponseEntity(chats, HttpStatus.OK);
        } catch (SQLException e) {
            logger.error("Couldn't perform database operation");
            throw new SQLException("500", "Couldn't perform database operation, please try again!!!");
        }
    }

    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN') OR hasRole('AUDITOR')")
    @RequestMapping(value = "/chats/{chatId}", method = RequestMethod.DELETE)
    public HttpEntity<Resources<Resource<Chat>>> deleteChats(@PathVariable("chatId") String chatId) {
        try {
            chatService.deleteChat(chatId);
            messageService.deleteMessagesFromChat(chatId);
            return new ResponseEntity(HttpStatus.OK);
        } catch (SQLException e) {
            logger.error("Couldn't perform database operation");
            throw new SQLException("500", "Couldn't perform database operation, please try again!!!");
        }
    }
}
