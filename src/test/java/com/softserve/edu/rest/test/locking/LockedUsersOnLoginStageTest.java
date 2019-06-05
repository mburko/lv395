package com.softserve.edu.rest.test.locking;

import com.softserve.edu.rest.data.User;
import com.softserve.edu.rest.data.UserRepository;
import com.softserve.edu.rest.service.GuestService;
import com.softserve.edu.rest.service.UserService;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * This class includes three tests
 * for checking locking functionality,
 * after wrong login for four time.
 *
 * @author Yurii Kril
 */
public class LockedUsersOnLoginStageTest extends ATestRunner {

    /** The variable needs for counting wrong attempts login.*/
    private int counter = 1;

    /**
     * DataProvider for test below.
     * @see #lockingUserByUnsuccessfulLoginTest
     * Three wrong data for login in rest-api.
     * Fourth - correct for login in rest-api.
     * @return User - with wrong password,
     *         int - counter.
     */
    @DataProvider
    public Object[][] userData() {
        counter = 1;
        return new Object[][]{
                {UserRepository.getUserWrongPassword(), counter++},
                {UserRepository.getUserWrongPassword(), counter++},
                {UserRepository.getUserWrongPassword(), counter++},
                // the same user, but with correct password.
                {UserRepository.getUser1(), counter},
        };
    }


    /**
     * This test checks locking functionality in rest-api on login stage:
     * After 3 wrong attempts to login, user will be locked.
     * @param user - user data for login.
     * @param attempt - for change False on True after 3 attempt.
     */
    @Severity(SeverityLevel.BLOCKER)
    @Description(" Locking user after three failed login attempts \n" +
            "Expected result: user will be locked.")
    @Story("Locking user by system")
    @Test(dataProvider = "userData")
    public void lockingUserByUnsuccessfulLoginTest(final User user,
                                                   final int attempt) {
        log.info("Test lockingUserByUnsuccessfulLogin started!");
        GuestService guestService = new GuestService();
        if (attempt == 4) {
            Assert.assertTrue(guestService.isUserLockedAfterTryToLogin(user));
            adminService.unlockAllUsers();
        } else {
            Assert.assertFalse(guestService.isUserLockedAfterTryToLogin(user));
        }
        log.info("Test lockingUserByUnsuccessfulLogin finished!\n");
    }

    /**
     * DataProvider for test below.
     * @see #lockingAdminByUnsuccessfulLoginTest
     * Three wrong data for login in rest-api.
     * Fourth - correct for login in rest-api.
     * @return User - user with admin rights and with wrong password,
     *         int - counter.
     */
    @DataProvider
    public Object[][] adminData() {
        counter = 1;
        return new Object[][]{
                {UserRepository.getAdminWrongPassword(), counter++},
                {UserRepository.getAdminWrongPassword(), counter++},
                {UserRepository.getAdminWrongPassword(), counter++},
                // the same user, but with correct password.
                {UserRepository.newUserWithAdminRights(), counter},
        };
    }

    /**
     * This test checks locking functionality in rest-api on login stage:
     * After 3 wrong attempts to login, user with admin rights will be locked.
     * @param admin - user with admin rights data for login.
     * @param attempt - for change False on True after 3 attempt.
     */
    @Severity(SeverityLevel.BLOCKER)
    @Description(" Locking admin after three failed login attempts \n" +
            "Expected result: user will be locked.")
    @Story("Locking admin (user with admin rights) by system")
    @Test (dataProvider = "adminData", priority = 1)
    public void lockingAdminByUnsuccessfulLoginTest(final User admin,
                                                    final int attempt) {
        log.info("Test lockingAdminByUnsuccessfulLogin started!");
        GuestService guestService = new GuestService();
        if (attempt == 4) {
            // Check
            Assert.assertTrue(guestService.isUserLockedAfterTryToLogin(admin));
            adminService.unlockAllUsers();
        } else {
            Assert.assertFalse(guestService.isUserLockedAfterTryToLogin(admin));
        }
        log.info("Test lockingAdminByUnsuccessfulLogin finished!\n");
    }

    /**
     * This test checks, that login can successful will be logged
     * after two wrong attempts before.
     */
    @Severity(SeverityLevel.BLOCKER)
    @Description(" User will not be locked after two failed login attempts \n" +
            "Expected result: user will not be locked.")
    @Story("Locking admin (user with admin rights) by system")
    @Test (priority = 2)
    public void loginAfterSecondUnsuccessfulAttemptTest() {
        log.info("Test loginAfterSecondUnsuccessfulAttempt started!");
        // Initialize two users with same name
        User userValidPassword = UserRepository.getUser1();
        User userInvalidPassword = UserRepository.getUserWrongPassword();
        GuestService guestService = new GuestService();
        // Unsuccessful login
        guestService.isUserLockedAfterTryToLogin(userInvalidPassword);
        guestService.SuccessfulUserLogin(userValidPassword);
        // Successful login
        UserService userService = guestService.
                SuccessfulUserLogin(userValidPassword);
        System.out.println(userService.getCurrentLifetime());
        Assert.assertTrue(adminService.isUserLogged(userValidPassword));
        userService.logoutUser();
        log.info("Test loginAfterSecondUnsuccessfulAttempt finished!\n");
    }

}
