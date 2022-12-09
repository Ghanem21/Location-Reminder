package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    private lateinit var localRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    private val reminder1 = ReminderDTO("Rem1", "Desc1", "Loc1", 1.0, 1.0,"1")
    private val reminder2 = ReminderDTO("Rem2", "Desc2", "loc2", 2.0, 2.0, "2")
    private val reminder3 = ReminderDTO("Rem3", "Desc3", "loc3", 3.0, 3.0, "3")

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    //initialize the database and use it to initialize the repo before each test case
    @Before
    fun setup() {
        /// using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localRepository =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    //close the database after each test
    @After
    fun cleanUp() {
        database.close()
    }
    //save reminder to repo and retrieve and check is it have the same values
    @Test
    fun saveReminder_retrievesReminderById() = runBlocking {
        // GIVEN - save a reminder in repo
        localRepository.saveReminder(reminder1)

        // WHEN - Get the reminder by id from the repo
        val result = localRepository.getReminder(reminder1.id)

        // THEN - The result data contains the expected values
        result as Result.Success
        assertEquals(result.data.title, (reminder1.title))
        assertEquals(result.data.description,(reminder1.description))
        assertEquals(result.data.location,(reminder1.location))
        assertEquals(result.data.latitude,(reminder1.latitude))
        assertEquals(result.data.longitude,(reminder1.longitude))
        assertEquals(result.data.id,(reminder1.id))
    }
    //save three reminders to the repo and get all reminder from repo then check if we have the three reminder had added correctly so the size is three
    @Test
    fun saveReminders_retrievesAllReminders() = runBlocking {
        // GIVEN - save three reminder in repo
        localRepository.saveReminder(reminder1)
        localRepository.saveReminder(reminder2)
        localRepository.saveReminder(reminder3)

        // WHEN - Get the reminder  from the repo
        val result = localRepository.getReminders()

        // THEN - The result data contains the expected number of reminder
        result as Result.Success
        assertEquals(result.data.size,(3))
    }

    //save three reminders and delete a reminder by id and check that the size become 2 and tthat we delete the right reminder
    @Test
    fun saveReminders_deletesOneReminderById() = runBlocking {
        // GIVEN - save three reminder in repo
        localRepository.saveReminder(reminder1)
        localRepository.saveReminder(reminder2)
        localRepository.saveReminder(reminder3)

        // WHEN - Delete a reminder by id and Get the reminder  from the repo
        localRepository.deleteReminder(reminder1.id)
        val result = localRepository.getReminders()

        // THEN - The result data contains the expected value and number of reminder
        result as Result.Success
        assertEquals(result.data.size, (2))
        assertEquals(result.data[0].location, (reminder2.location))
    }

    //save three reminders and delete all and check that the reminder list become empty
    @Test
    fun saveReminders_deletesAllReminders() = runBlocking {
        // GIVEN - save three reminder in repo
        localRepository.saveReminder(reminder1)
        localRepository.saveReminder(reminder2)
        localRepository.saveReminder(reminder3)

        // WHEN - Delete reminders and Get the reminders  from the repo
        localRepository.deleteAllReminders()
        val result = localRepository.getReminders()

        // THEN - The result data contains the expected value and number of reminder
        result as Result.Success
        assertEquals(result.data.size, (0))

    }

    //delete all reminder in repo then check if an reminder is exist in it
    @Test
    fun getReminder_returnsError() = runBlocking {

        // WHEN - Delete reminders and Get the a reminder by id  from the repo
        localRepository.deleteAllReminders()
        val result = localRepository.getReminder(reminder1.id) as Result.Error

        //THEN - will return Error
        assertEquals(result.message, ("Reminder not found!"))
    }

}