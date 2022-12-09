package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    // viewModel under test
    private lateinit var remindersListViewModel: RemindersListViewModel

    //  inject fake data into the viewModel
    private lateinit var fakeDataSource: FakeDataSource

    private val reminder1 = ReminderDTO("Rem1", "Desc1", "Loc1", 1.0, 1.0,"1")
    private val reminder2 = ReminderDTO("Rem2", "Desc2", "loc2", 2.0, 2.0, "2")
    private val reminder3 = ReminderDTO("Rem3", "Desc3", "loc3", 3.0, 3.0, "3")

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    //initialize fakeDatasource and viewModel
    @Before
    fun setUpViewModel(){
        stopKoin()
        fakeDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    //clean the fake data source
    @After
    fun clearDataSource() = runBlockingTest{
        fakeDataSource.deleteAllReminders()
    }

    //delete all reminders in fake data source and invalidateShowNoData with empty list
    @Test
    fun invalidateShowNoData_showNoData_isTrue()= mainCoroutineRule.runBlockingTest{
        //when: delete and then load
        fakeDataSource.deleteAllReminders()

        remindersListViewModel.loadReminders()

        //then
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue().size, `is` (0))
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is` (true))

    }

    //delete all reminder,then save three reminder and check that we load the three reminder and show no data has disappeared
    @Test
    fun loadReminders_loadsThreeReminders()= mainCoroutineRule.runBlockingTest {

        fakeDataSource.deleteAllReminders()
        //given
        fakeDataSource.saveReminder(reminder1)
        fakeDataSource.saveReminder(reminder2)
        fakeDataSource.saveReminder(reminder3)
        //when:load reminder
        remindersListViewModel.loadReminders()
        //Then
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue().size, `is` (3))
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is` (false))

    }
    //check that the loading indicator appear after load the reminders and disappear when the coroutine is resumed
    @Test
    fun loadReminders_checkLoading()= mainCoroutineRule.runBlockingTest{
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        fakeDataSource.deleteAllReminders()
        fakeDataSource.saveReminder(reminder1)

        //when: load data
        remindersListViewModel.loadReminders()

        // Then loading indicator is shown
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        // Then loading indicator is hidden
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))

    }

    // test that when there is an error and you try to load reminders it will return error message
    @Test
    fun loadReminders_shouldReturnError()= mainCoroutineRule.runBlockingTest{
        //when
        fakeDataSource.setShouldReturnError(true)
        remindersListViewModel.loadReminders()
        //then
        assertEquals("data can't be retrieved",remindersListViewModel.showSnackBar.getOrAwaitValue())
    }

}