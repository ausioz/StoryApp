package com.example.storyapp.ui.user.login

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.supportsInputMethods
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.storyapp.R
import com.example.storyapp.data.remote.ApiConfig
import com.example.storyapp.util.EspressoIdlingResource
import com.example.storyapp.utils.JsonConverter
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {
    private val mockWebServer = MockWebServer()
    private val mail = "yoyo@yyy.com"
    private val pass = "zxcasdqwe"

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginSuccessThenLogout() {
        ActivityScenario.launch(LoginActivity::class.java)
        onView(withId(R.id.emailEditTextLayoutLogin)).check(matches(isDisplayed()))
        onView(
            allOf(
                supportsInputMethods(),
                isDescendantOfA(withId(R.id.emailEditTextLayoutLogin)),
            )
        ).perform(typeText(mail)).perform(closeSoftKeyboard())
        onView(withId(R.id.passwordEditTextLayoutLogin)).check(matches(isDisplayed()))
        onView(
            allOf(
                supportsInputMethods(), isDescendantOfA(withId(R.id.passwordEditTextLayoutLogin))
            )
        ).perform(typeText(pass)).perform(closeSoftKeyboard())

        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).check(matches(isEnabled()))
        onView(withId(R.id.loginButton)).perform(click())

        val mockResponse = MockResponse().setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("login_success_response.json"))
        mockWebServer.enqueue(mockResponse)
        Thread.sleep(500)
        onView(withId(R.id.logout)).check(matches(isDisplayed()))
        onView(withId(R.id.logout)).perform(click())
        onView(withId(R.id.bt_login)).check(matches(isDisplayed()))
    }
}