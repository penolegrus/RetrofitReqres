package tests;

import endpoints.UserService;
import models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTests {
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://reqres.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private final UserService userService = retrofit.create(UserService.class);

    @Test
    public void testUserListPage() throws IOException {
        int page = 1;
        Response<UserListRootResponse> response = userService.getUserList(page).execute();
        Assertions.assertTrue(response.isSuccessful());

        UserListRootResponse rootResponse = response.body();
        assertEquals(page, rootResponse.getPage());

        List<UserResponse> userData = rootResponse.getData();
        assertTrue(userData.size() > 0);
    }

    @Test
    public void testGetUserById() throws IOException {
        int id = 2;
        Response<SingleUserResponse> response = userService.getUserById(id).execute();

        assertTrue(response.isSuccessful());
        SingleUserResponse userResponse = response.body();
        assertEquals(id, userResponse.getData().getId());
    }

    @Test
    public void testCreateUser() throws IOException {
        String correctTimePattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z";
        String name = "Oleg";
        String job = "ThreadQA Батя";
        UserRequest userRequest = new UserRequest(name, job);

        Response<CreateUserResponse> response = userService.createUser(userRequest).execute();
        assertTrue(response.isSuccessful());

        CreateUserResponse userResponse = response.body();
        assertEquals(name, userResponse.getName());
        assertEquals(job, userResponse.getJob());
        assertTrue(userResponse.getCreatedAt().matches(correctTimePattern));
    }

    @Test
    public void testUpdateUser() throws IOException {
        String name = "morpheus";
        String job = "zion resident";

        UserRequest userRequest = new UserRequest(name, job);
        Response<UserUpdateResponse> response = userService.updateUserById(2, userRequest).execute();

        assertTrue(response.isSuccessful());
        assertTrue(isTimePatternCorrect(response.body().getUpdatedAt()));
    }

    @Test
    public void testDeleteUser() throws IOException {
        Response<Void> response = userService.deleteUserById(4).execute();
        assertTrue(response.isSuccessful());
        assertEquals(204,response.code());
    }

    private boolean isTimePatternCorrect(String time){
        return time.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z");
    }
}
