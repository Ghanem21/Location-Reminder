package com.udacity.project4.locationreminders.data


import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO> = mutableListOf()) :
    ReminderDataSource {
    // it boolean variable use to indicate the instance result class if it true error else success
    private var shouldReturnError = false

    //assign the parameter to shouldReturnError
    fun setShouldReturnError( value:Boolean){
        shouldReturnError = value
    }

    //get all  reminder and if anything want wrong sent message data can't be retrieved
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return try {
            if (shouldReturnError)
                throw Exception("data can't be retrieved")
            Result.Success(ArrayList(reminders))
        }catch (ex:Exception) {
            Result.Error(ex.localizedMessage)
        }
    }
    //add the passed reminder in parameter to the reminders
    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }
    //retrieve a reminder by the id , if anything want wrong sent message data can't be retrieved and if not fond sent the message Reminder not found
    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return try {
            if (shouldReturnError) {
                throw Exception("data can't be retrieved")
            }
            val reminder = reminders.find {
                it.id == id
            }
            if (reminder != null) {
                Result.Success(reminder)
            } else {
                Result.Error("Reminder not found!")
            }
        }catch (ex:Exception){
            Result.Error(ex.localizedMessage)
        }
    }
    //delete all reminder in the fake data source
    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
    //delete a specific reminder in fake data source by the id
    override suspend fun deleteReminder(id: String) {
        val reminder = reminders.find {
            it.id == id
        }
        reminders.remove(reminder)
    }


}