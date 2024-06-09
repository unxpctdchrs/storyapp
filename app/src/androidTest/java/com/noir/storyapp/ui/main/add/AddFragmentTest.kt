package com.noir.storyapp.ui.main.add

import android.Manifest
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.noir.storyapp.data.remote.retrofit.APIConfig
import com.noir.storyapp.helper.EspressoIdlingResource
import com.noir.storyapp.utils.JsonConverter
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import com.noir.storyapp.R
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class AddFragmentTest {
    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        APIConfig.BASE_URL = "http://127.0.0.1.8080"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @get:Rule
    val revokePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @Test
    fun addStory_Success() {
        launchFragmentInContainer<AddFragment>(null, R.style.Base_Theme_StoryApp)

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("add_story_success_response.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.button_add))
            .check(matches(isDisplayed()))
    }
}