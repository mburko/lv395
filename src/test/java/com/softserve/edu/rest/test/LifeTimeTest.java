package com.softserve.edu.rest.test;


import org.powermock.api.mockito.PowerMockito;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.softserve.edu.rest.data.Lifetime;
import com.softserve.edu.rest.data.LifetimeRepository;
import com.softserve.edu.rest.data.User;
import com.softserve.edu.rest.data.UserRepository;
import com.softserve.edu.rest.engine.LoginResource;
import com.softserve.edu.rest.engine.TokenlifetimeResource;
import com.softserve.edu.rest.entity.SimpleEntity;
import com.softserve.edu.rest.service.AdminService;
import com.softserve.edu.rest.service.GuestService;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

public class LifeTimeTest {

    @DataProvider // (parallel = true)
    public Object[][] updateLifeTime() {
        return new Object[][] {
            { UserRepository.getAdmin(), LifetimeRepository.GetLongTime() },
        };
    }

    @Description("Test Description: class LifeTimeTest; checkLoginReport(...)")
    @Severity(SeverityLevel.BLOCKER)
    @Story("checkLoginReport STORY")
    @Test(dataProvider = "updateLifeTime")
    public void checkLoginReport(User adminUser, Lifetime lifetime) {
        //
        // Steps
        GuestService guestService = new GuestService();
        Lifetime currentLifetime = guestService.getCurrentLifetime();
        System.out.println("\tStarted currentLifetime = " + currentLifetime);
        Assert.assertEquals(currentLifetime.getTimeAsString(),
                LifetimeRepository.DEFAULT_TOKEN_LIFETIME);
        //
        // Steps
        AdminService adminService = guestService
                .SuccessfulAdminLogin(adminUser)
                .UpdateTokenlifetime(lifetime);
        currentLifetime = adminService.getCurrentLifetime();
        System.out.println("\tUpdated currentLifetime = " + currentLifetime);
        Assert.assertEquals(currentLifetime.getTimeAsString(),
                LifetimeRepository.LONG_TOKEN_LIFETIME);
        //
        // Return to Previous State
        adminService = adminService
                .UpdateTokenlifetime(new Lifetime(LifetimeRepository.DEFAULT_TOKEN_LIFETIME));
        currentLifetime = adminService.getCurrentLifetime();
        System.out.println("\tReturn to Previous currentLifetime = " + currentLifetime);
        Assert.assertEquals(currentLifetime.getTimeAsString(),
                LifetimeRepository.DEFAULT_TOKEN_LIFETIME);
        //
        //System.out.println("LogoutUser");
        guestService = adminService.LogoutUser();
        //System.out.println("adminUser.getToken() = " + adminUser.getToken());
        Assert.assertTrue(adminUser.getToken().isEmpty());
    }
    
    //@Test(dataProvider = "updateLifeTime")
    public void checkLoginMock(User adminUser, Lifetime lifetime) throws Exception {
        //
        // Mock Object
        LoginResource loginResource = PowerMockito.mock(LoginResource.class);
        //
        TokenlifetimeResource tokenlifetimeResource = PowerMockito.mock(TokenlifetimeResource.class);
        PowerMockito.when(tokenlifetimeResource.httpGetAsEntity(null, null))
                .thenReturn(new SimpleEntity(LifetimeRepository.DEFAULT_TOKEN_LIFETIME));
        // TODO
        //PowerMockito.whenNew(TokenlifetimeResource.class).withNoArguments().thenReturn(tokenlifetimeResource);
        //
        // Steps
        //GuestService guestService = new GuestService();
        GuestService guestService = new GuestService(loginResource, tokenlifetimeResource);
        Lifetime currentLifetime = guestService.getCurrentLifetime();
        System.out.println("\tStarted currentLifetime = " + currentLifetime);
        Assert.assertEquals(currentLifetime.getTimeAsString(),
                LifetimeRepository.DEFAULT_TOKEN_LIFETIME);
        //
        //PowerMockito.verify(tokenlifetimeResource).getPriceBySku(SKU);
    }
}
