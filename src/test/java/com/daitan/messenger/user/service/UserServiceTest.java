package com.daitan.messenger.user.service;

import com.daitan.messenger.users.model.PagedResponse;
import com.daitan.messenger.users.model.User;
import com.daitan.messenger.users.model.UserProfile;
import com.daitan.messenger.users.repository.UserRepository;
import com.daitan.messenger.users.service.UserServiceImpl;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private static final String ID = "5dasd2264646215";
    private static final String NOME = "test";
    private static final String SOBRENOME = "test";
    private static final String EMAIL = "test123@gmail.com.br";
    private static final String PASSWORD = "123";
    private static final String ROLE = "ADMIN";

    private UserRepository userRepositoryMock;

    private UserServiceImpl userServiceImpl;

    private Pageable pageable;

    private User user;

    @Before
    public void setUp() {
        user = new User(NOME, SOBRENOME, EMAIL, PASSWORD, ROLE);
        pageable = isNull(pageable) ?  new PageRequest(0, 2, Sort.Direction.ASC, "email") : pageable;
        userRepositoryMock = mock(UserRepository.class);
        userServiceImpl = new UserServiceImpl(userRepositoryMock);
    }


    @Test
    public void saveNewUser() {
        userServiceImpl.save(user);

        ArgumentCaptor<User> toDoArgument = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryMock, times(1)).save(toDoArgument.capture());
        verifyNoMoreInteractions(userRepositoryMock);

        User userSaved = toDoArgument.getValue();

        assertNull(userSaved.getId());
        assertThat(userSaved.getNome(), is(user.getNome()));
        assertThat(userSaved.getSobrenome(), is(user.getSobrenome()));
        assertThat(userSaved.getEmail(), is(user.getEmail()));
        assertThat(userSaved.getPassword(), is(user.getPassword()));
    }

    @Test
    public void findIdByEmail() {
        when(userRepositoryMock.findIdByEmail(EMAIL)).thenReturn(user);
        User actual = userServiceImpl.findIdByEmail(EMAIL);
        verify(userRepositoryMock, times(1)).findIdByEmail(EMAIL);
        verifyNoMoreInteractions(userRepositoryMock);
        assertThat(actual, is(user));
    }

    @Test
    public void findById() {
        when(userRepositoryMock.findById(ID)).thenReturn(Optional.ofNullable(user));
        Optional<User> actual = userServiceImpl.findById(ID);
        verify(userRepositoryMock, times(1)).findById(ID);
        verifyNoMoreInteractions(userRepositoryMock);
        assertThat(actual, is(user));
    }

    @Test
    public void deleteById() {
        when(userRepositoryMock.findById(ID)).thenReturn(null);
        userServiceImpl.deleteById(ID);
        verify(userRepositoryMock, times(1)).deleteById(ID);
        verifyNoMoreInteractions(userRepositoryMock);
    }


    @Test
    public void insert() {
        userServiceImpl.insert(user);

        ArgumentCaptor<User> toDoArgument = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryMock, times(1)).insert(toDoArgument.capture());
        verifyNoMoreInteractions(userRepositoryMock);

        User userSaved = toDoArgument.getValue();

        assertNull(userSaved.getId());
        assertThat(userSaved.getNome(), is(user.getNome()));
        assertThat(userSaved.getSobrenome(), is(user.getSobrenome()));
        assertThat(userSaved.getEmail(), is(user.getEmail()));
        assertThat(userSaved.getPassword(), is(user.getPassword()));
    }

    @Test
    public void findAll() {
        int page = 0;
        int size = 2;
        List<User> expectedUsers = Lists.newArrayList(user);
        List<UserProfile> userProfiles = expectedUsers.stream()
                .map(user -> new UserProfile(user.getId(), user.getEmail(), user.getNome(), user.getRole()))
                .collect(Collectors.toList());

        when(userRepositoryMock.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(expectedUsers));

        PagedResponse<UserProfile> actual = userServiceImpl.findAll(page, size);

        verify(userRepositoryMock, times(1)).findAll(pageable);
        verifyNoMoreInteractions(userRepositoryMock);

        PagedResponse<UserProfile> expectedPagedResponse = new PagedResponse<>(userProfiles, 0, 0, 1, 1, true);

        checkPagedResponses(actual, expectedPagedResponse);
    }

    private void checkPagedResponses(PagedResponse<UserProfile> actual, PagedResponse<UserProfile> expectedPagedResponse) {
        assertEquals(actual.getTotalElements(), expectedPagedResponse.getTotalElements());
        assertEquals(actual.getPage(), expectedPagedResponse.getPage());
        assertEquals(actual.getSize(), expectedPagedResponse.getSize());
        assertEquals(actual.getTotalPages(), expectedPagedResponse.getTotalPages());
        assertEquals(actual.isLast(), expectedPagedResponse.isLast());
        assertEquals("Found " + actual.getContent().size(), expectedPagedResponse.getContent().size(), actual.getContent().size());
        assertThat(actual.getContent(), is(expectedPagedResponse.getContent()));
    }

    @Test
    public void existsByEmail() {
        when(userRepositoryMock.existsByEmail(EMAIL)).thenReturn(true);
        boolean existsByEmail = userServiceImpl.existsByEmail(EMAIL);
        verify(userRepositoryMock, times(1)).existsByEmail(EMAIL);
        verifyNoMoreInteractions(userRepositoryMock);
        assertTrue(existsByEmail);
    }

    @Test
    public void findByEmail() {
        Optional<User> user = Optional.ofNullable(new User(NOME, SOBRENOME, EMAIL, PASSWORD, ROLE));
        when(userRepositoryMock.findByEmail(EMAIL)).thenReturn(user);
        Optional<User> actual = userServiceImpl.findByEmail(EMAIL);
        verify(userRepositoryMock, times(1)).findByEmail(EMAIL);
        verifyNoMoreInteractions(userRepositoryMock);
        assertThat(actual, is(user));
    }

    @Test
    public void deleteAll() {
        userServiceImpl.deleteAll();
        verify(userRepositoryMock, times(1)).deleteAll();
        verifyNoMoreInteractions(userRepositoryMock);
    }
}
