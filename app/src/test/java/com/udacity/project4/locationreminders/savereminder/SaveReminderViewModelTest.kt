package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

//Api target 29
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    //inject fake data to viewModel
    private lateinit var fakeDataSource: FakeDataSource

    private val reminder = ReminderDataItem("Rem1", "Desc1", "Loc1", 1.0, 1.0,"1")
    private val noTitleReminder = ReminderDataItem("", "Desc2", "loc", 2.0, 2.0, "2")
    private val noLocationReminder = ReminderDataItem("Rem3", "Desc3", "", 3.0, 3.0, "3")

    // make each task executed synchronously.
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
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    //set data of value of reminder test that clear value change all values to null
    @Test
    fun onClear_clearsReminderLiveData(){
        //Given - set values of reminder
        saveReminderViewModel.reminderTitle.value = reminder.title
        saveReminderViewModel.reminderDescription.value = reminder.description
        saveReminderViewModel.reminderSelectedLocationStr.value = reminder.location
        saveReminderViewModel.latitude.value = reminder.latitude
        saveReminderViewModel.longitude.value = reminder.longitude
        saveReminderViewModel.reminderId.value = reminder.id

        //when - clear the viewModel
        saveReminderViewModel.onClear()

        //then - all values become null
        assertThat(saveReminderViewModel.reminderTitle.getOrAwaitValue(), `is` (nullValue()))
        assertThat(saveReminderViewModel.reminderDescription.getOrAwaitValue(), `is`(nullValue()))
        assertThat(saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(), `is`(nullValue()))
        assertThat(saveReminderViewModel.latitude.getOrAwaitValue(), `is`(nullValue()))
        assertThat(saveReminderViewModel.longitude.getOrAwaitValue(), `is`(nullValue()))
        assertThat(saveReminderViewModel.reminderId.getOrAwaitValue(), `is`(nullValue()))

    }

    //test that can change data of existing data successfully
    @Test
    fun editReminder_setsLiveDataOfReminderToBeEdited(){

        //when - edit exist reminder
        saveReminderViewModel.editReminder(reminder)

        //then - it's value changed
        assertThat(saveReminderViewModel.reminderTitle.getOrAwaitValue(), `is` (reminder.title))
        assertThat(saveReminderViewModel.reminderDescription.getOrAwaitValue(), `is`(reminder.description))
        assertThat(saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(), `is`(reminder.location))
        assertThat(saveReminderViewModel.latitude.getOrAwaitValue(), `is`(reminder.latitude))
        assertThat(saveReminderViewModel.longitude.getOrAwaitValue(), `is`(reminder.longitude))
        assertThat(saveReminderViewModel.reminderId.getOrAwaitValue(), `is`(reminder.id))
    }

    //save a reminder and check that its datata save successfully
    @Test
    fun saveReminder_addsReminderToDataSource() = mainCoroutineRule.runBlockingTest{

        //given: save reminder
        saveReminderViewModel.saveReminder(reminder)
        //when: get reminder by id
        val checkReminder = fakeDataSource.getReminder("1") as Result.Success

        //then: data value is as expected
        assertEquals(checkReminder.data.title, (reminder.title))
        assertEquals(checkReminder.data.description, (reminder.description))
        assertEquals(checkReminder.data.location,  (reminder.location))
        assertEquals(checkReminder.data.latitude,  (reminder.latitude))
        assertEquals(checkReminder.data.longitude,(reminder.longitude))
        assertEquals(checkReminder.data.id,(reminder.id))

    }
    //save reminder and test that the loading is displayed and disappear after resume coroutine and validate return null
    @Test
    fun saveReminder_checkLoading()= mainCoroutineRule.runBlockingTest{
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        //given: save reminder
        saveReminderViewModel.saveReminder(reminder)

        // Then loading indicator is shown
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))

    }
    //check that the snack-bar display the right message
    @Test
    fun validateData_missingTitle_showSnackbarAndReturnFalse(){

        val validate = saveReminderViewModel.validateEnteredData(noTitleReminder)

        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is` (R.string.err_enter_title))
        assertThat(validate, `is` (false))
    }

    //test that the message appear wen you try to save reminder while and location is null and validate return null
    @Test
    fun validateData_missingLocation_showSnackbarAndReturnFalse(){

        //when
        val validate = saveReminderViewModel.validateEnteredData(noLocationReminder)

        //then
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is` (R.string.err_select_location))
        assertThat(validate, `is` (false))
    }




}