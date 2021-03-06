package tech.developingdeveloper.gettaskdone.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import tech.developingdeveloper.gettaskdone.util.getCurrentTimeInMilli


/**
 * @author Abhishek Saxena
 * @since 24-06-2020 07:49
 */

@Entity(tableName = "Tasks")
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    var title: String = "",
    var details: String = "",
    var priority: String = "",
    @ColumnInfo(name = "created_on") val createdOn: Long = getCurrentTimeInMilli(),
    @ColumnInfo(name = "updated_on") var updatedOn: Long = getCurrentTimeInMilli()
) : Parcelable {
    constructor(task: Task) : this(
        task.id,
        task.title,
        task.details,
        task.priority,
        task.createdOn,
        task.updatedOn
    )

    fun hasDetails(): Boolean = details.isNotEmpty()

    fun getPriorityValue(): Int {
        return when (priority) {
            TaskPriority.LOW -> 1
            TaskPriority.MEDIUM -> 2
            else -> 3
        }
    }

    override fun toString(): String {
        return "{Task:" +
                " id: $id, " +
                " title: $title," +
                " details: $details," +
                " priority: $priority" +
                " created_on: $createdOn" +
                " updated_on: $updatedOn" +
                "}"
    }

    interface TaskPriority {
        companion object {
            const val LOW = "Low"
            const val MEDIUM = "Medium"
            const val HIGH = "High"
        }
    }
}