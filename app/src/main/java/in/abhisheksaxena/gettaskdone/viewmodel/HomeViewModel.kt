package `in`.abhisheksaxena.gettaskdone.viewmodel


import `in`.abhisheksaxena.gettaskdone.Event
import `in`.abhisheksaxena.gettaskdone.data.db.local.TaskDao
import `in`.abhisheksaxena.gettaskdone.data.model.NavData
import `in`.abhisheksaxena.gettaskdone.data.model.Task
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 07:06
 */

class HomeViewModel(
    private val dataSource: TaskDao,
    navData: NavData
) : BaseViewModel() {

    private val TAG = javaClass.name

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var tasks = dataSource.getAllTasks()

    var tempTask: Task = Task()
    var currentTask = MediatorLiveData<Task>()

    private var _viewState = MutableLiveData<AddTaskState>()
    val viewState: LiveData<AddTaskState> = _viewState

    private val _openTaskEvent = MutableLiveData<Event<Unit>>()
    val openTaskEvent: LiveData<Event<Unit>> = _openTaskEvent

    private val _taskUpdatedEvent = MutableLiveData<Event<Unit>>()
    val taskUpdatedEvent: LiveData<Event<Unit>> = _taskUpdatedEvent

    private val _isTaskDeleted = MutableLiveData<Boolean>()
    val isTaskDeleted: LiveData<Boolean>  = _isTaskDeleted

    private val _isTaskCreated = MutableLiveData<Boolean>()
    val isTaskCreated: LiveData<Boolean> = _isTaskCreated

    private val _isTaskUpdated = MutableLiveData<Boolean>()
    val isTaskUpdated: LiveData<Boolean> = _isTaskUpdated

    init {
        _viewState.value = navData.state
        if (navData.id != -1L) {
            Log.e(TAG, "id: $navData")
            currentTask.addSource(dataSource.getTaskWithId(navData.id), currentTask::setValue)
        }
        Log.e(TAG, "init current task: ${currentTask.value}")
        Log.e(TAG, "init temp task: $tempTask")
    }

    fun saveTask() {
        if (currentTask.value != tempTask) {
            if (tempTask.details.isEmpty())
                tempTask.details = ""
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    if (_viewState.value == AddTaskState.NEW_TASK_STATE)
                        dataSource.insertTask(tempTask)
                    else
                        dataSource.updateTask(tempTask)
                }
                if (_viewState.value == AddTaskState.NEW_TASK_STATE) {
                    navigateToHomeFragment()
                    onTaskCreated()
                } else if (_viewState.value == AddTaskState.EDIT_STATE) {
                    updateViewState(AddTaskState.VIEW_STATE)
                    onTaskUpdated()
                }
                //Log.e(TAG, "Task updated")
            }
        } else {
            updateViewState(AddTaskState.VIEW_STATE)
            //Log.e(TAG, "No change in the tasks")
        }
    }


    fun deleteItem() {
        if (currentTask.value != null) {
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    dataSource.deleteItem(currentTask.value!!)
                }
                onTaskDeleted()
                navigateToHomeFragment()
            }
        }
    }

    fun updateViewState(state: AddTaskState?) {
        _viewState.value = state
        Log.e(TAG, "updateViewState called, _viewState: ${_viewState.value}")
    }

    fun openTaskEvent() {
        _openTaskEvent.value = Event(Unit)
    }

    private fun navigateToHomeFragment() {
        _taskUpdatedEvent.value = Event(Unit)
    }

    fun onTaskCreated(){
        _isTaskCreated.value = true
    }

    fun doneOnTaskCreated(){
        _isTaskCreated.value = false
    }

    fun onTaskUpdated(){
        _isTaskUpdated.value = true
    }

    fun doneOnTaskUpdated(){
        _isTaskUpdated.value = false
    }

    fun onTaskDeleted(){
        _isTaskDeleted.value = true
    }

    fun doneOnTaskDeleted(){
        _isTaskDeleted.value = false
    }

}

enum class AddTaskState {
    NEW_TASK_STATE, EDIT_STATE, VIEW_STATE
}