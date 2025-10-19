package info.mx.tracks.room.entity

import androidx.room.DatabaseView

// Source
// https://github.com/android/trackr/blob/9b7c22d6e7b2782959389ed115730efcfcddc790/shared/src/main/java/com/example/android/trackr/data/Views.kt#L19

@DatabaseView(
    """
        select id as _id, 
            trackRestId as track_restId,
            count(*) as eventcount
        from Event
        where approved > -1 and eventDate > strftime ( '%s' , 'now' )
        group by track_restId
    """
)
data class EventSum(
    val id: Long,
    val trackRestId: Long,
    val eventCount: Int,
//    @Relation(
//        parentColumn = "id",
//        entity = Tag::class,
//        entityColumn = "id",
//        associateBy = Junction(
//            value = TaskTag::class,
//            parentColumn = "taskId",
//            entityColumn = "tagId"
//        )
//    )
)
