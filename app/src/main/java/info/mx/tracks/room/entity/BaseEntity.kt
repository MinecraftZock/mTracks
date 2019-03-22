package info.mx.tracks.room.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
abstract class BaseEntity(
        @field:SerializedName("id") @PrimaryKey(autoGenerate = true) var id: Long?,
        @field:SerializedName("restId") var restId: Long = 0,
        @field:SerializedName("changed") var changed: Long = 0
) {
    @Ignore
    constructor() : this(null, System.currentTimeMillis())

    override fun toString(): String {
        return id.toString() + ":" + changed.toString()
    }
}
