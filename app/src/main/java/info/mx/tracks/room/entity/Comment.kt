package info.mx.tracks.room.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import com.google.gson.annotations.SerializedName
import info.mx.tracks.room.DBMigrationUtil

@Entity(tableName = DBMigrationUtil.TABLE_COMMENT, indices = [Index(value = ["trackId"], name = "CommentTrackIdIndex")])
data class Comment(
    @field:SerializedName("trackId") var trackId: Long = 0,
    @field:SerializedName("rating") var rating: Int = 2,
    @field:SerializedName("username") var username: String = "",
    @field:SerializedName("note") var note: String = "",
    @field:SerializedName("country") var country: String = "",
    @field:SerializedName("deleted") var deleted: Int = 0, // TODO boolean
    @field:SerializedName("approved") var approved: Int = 0, // TODO boolean
    @field:SerializedName("androidid") var androidid: String = ""
) : BaseEntity() {
    @Ignore
    constructor() : this(0, 2, "", "", "", 0, 0, "")

    @Ignore
    internal constructor(id: Long, trackId: Long, username: String) : this() {
        this.id = id
        this.trackId = trackId
        this.username = username
    }

    override fun toString(): String {
        return "$id:$changed"
    }
}
