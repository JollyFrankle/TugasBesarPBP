package com.example.tugasbesarpbp


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditReviewTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(FTOScreenActivity::class.java)

    @Test
    fun fTOScreenActivityTest() {
        onView(isRoot()).perform(waitFor(3500))
        val textInputEditText = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilLoginUsername),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText.perform(replaceText("admin"), closeSoftKeyboard())

        val textInputEditText2 = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilLoginPassword),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText2.perform(replaceText("admin"), closeSoftKeyboard())

        val materialButton = onView(
            allOf(
                withId(R.id.btnLogin), withText("Log in"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout2),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            1
                        )
                    ),
                    0
                )
            )
        )
        materialButton.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(2500))

        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.listItemFragment), withContentDescription("Search"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.bottomNavigationView),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())
        onView(isRoot()).perform(waitFor(2000))

        val materialCardView = onView(
            allOf(
                withId(R.id.cardView),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.rvItemKostContainer),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialCardView.perform(click())
        onView(isRoot()).perform(waitFor(2000))

        // agar scroll down
        onView(withId(R.id.mainView)).perform(swipeUp())

        val materialButton2 = onView(
            allOf(
                withId(R.id.btnCRUDReview), withText("Tambah/Edit Review Anda"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                        1
                    ),
                    12
                )
            )
        )
        materialButton2.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(2000))

        val textInputEditText3 = onView(
            allOf(
//                withText("Apakah unit testing ini bisa berjalan? Currently: 2022-12-15T21:11:20.294"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tilCRUDReview_Review),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        val newText = "Apakah unit testing ini bisa berjalan? Currently: ${java.time.LocalDateTime.now()}"
        textInputEditText3.perform(replaceText(newText), closeSoftKeyboard())

//        val textInputEditText4 = onView(
//            allOf(
////                withText("Apakah unit testing ini bisa berjalan? Currently: 2022-12-15T21:11:20.294 2"),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.tilCRUDReview_Review),
//                        0
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        )
//        textInputEditText4.perform(closeSoftKeyboard())

        val materialButton3 = onView(
            allOf(
                withId(R.id.btnTambah), withText("Ubah Review"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            1
                        )
                    ),
                    0
                )
            )
        )
        materialButton3.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(2000))

        pressBack()
        onView(isRoot()).perform(waitFor(2000))

        val bottomNavigationItemView2 = onView(
            allOf(
                withId(R.id.profileFragment), withContentDescription("Profile"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.bottomNavigationView),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView2.perform(click())
        onView(isRoot()).perform(waitFor(2000))

        val floatingActionButton = onView(
            allOf(
                withId(R.id.btnFloatSignOut), withContentDescription("Log out"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.fragProfileFrame),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())
        onView(isRoot()).perform(waitFor(1000))

        val materialButton4 = onView(
            allOf(
                withId(android.R.id.button1), withText("Yes"),
                childAtPosition(
                    childAtPosition(
                        withId(androidx.databinding.library.baseAdapters.R.id.buttonPanel),
                        0
                    ),
                    3
                )
            )
        )
        materialButton4.perform(scrollTo(), click())
        onView(isRoot()).perform(waitFor(2000))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

    private fun waitFor(delay: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "Wait for $delay milliseconds."
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}
