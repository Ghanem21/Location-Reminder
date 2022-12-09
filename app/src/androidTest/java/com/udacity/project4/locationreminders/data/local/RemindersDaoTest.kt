package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.runner.RunWith

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    private lateinit var database: RemindersDatabase

    private val reminder1 = ReminderDTO("Rem1", "Desc1", "Loc1", 1.0, 1.0,"1")
    private val reminder2 = ReminderDTO("Rem2", "Desc2", "loc2", 2.0, 2.0, "2")
    private val reminder3 = ReminderDTO("Rem3", "Desc3", "loc3", 3.0, 3.0, "3")


    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    //initialize the database value before each test case
    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    //close the database after each test case
    @After
    fun closeDb() = database.close()

    //save three reminders and load all data from database and check is the size of the loaded data equal three
    @Test
    fun insertRemindersAndGetAll() = runBlockingTest {
        // GIVEN - insert three reminder
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        // WHEN - Get the reminders from the database
        val loaded = database.reminderDao().getReminders()

        // THEN - The loaded data contains the expected size
        assertEquals(loaded.size, 3)

    }

    //save one reminder and try to get it by id and check that all his data is the same with the reminder
    @Test
    fun insertReminderAndGetById() = runBlockingTest {

        // GIVEN - insert a reminder
        database.reminderDao().saveReminder(reminder1)

        // WHEN - Get the reminder by id from the database
        val loaded = database.reminderDao().getReminderById(reminder1.id)

        // THEN - The loaded data contains the expected values
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertEquals(loaded.title,(reminder1.title))
        assertEquals(loaded.description,(reminder1.description))
        assertEquals(loaded.location, (reminder1.location))
        assertEquals(loaded.latitude, (reminder1.latitude))
        assertEquals(loaded.longitude, (reminder1.longitude))
        assertEquals(loaded.id, (reminder1.id))

    }

    //save three reminder then delete all reminder in the database then check is reminder list is empty
    @Test
    fun insertRemindersAndDeleteAll()= runBlockingTest{
        // GIVEN - insert three reminder
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        // WHEN - Delete the reminders and Get the reminders from the database
        database.reminderDao().deleteAllReminders()
        val loaded = database.reminderDao().getReminders()

        // THEN - The loaded data is Empty
        assertEquals(loaded.size, (0))

    }
    //save three reminders and delete a reminder by id and check that the size become 2 and tthat we delete the right reminder
    @Test
    fun insertRemindersAndDeleteReminderById()= runBlockingTest{
        // GIVEN - insert three reminder
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        // WHEN - Delete the one reminder and Get the reminders from the database
        database.reminderDao().deleteReminderById(reminder1.id)
        val loaded = database.reminderDao().getReminders()

        // THEN - The loaded data contains the expected values
        assertEquals(loaded.size, (2))
        assertEquals(loaded[0].id, (reminder2.id))

    }
    //save three reminder and try to load non existing reminder
    @Test
    fun insertReminderAndGetById_differentId_returnNull()= runBlockingTest{
        // GIVEN - insert three reminder
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        // WHEN - Get the reminder by id from the database
        val randomId = "54321"
        val loaded = database.reminderDao().getReminderById(randomId)

        assertNull(loaded)

    }


}