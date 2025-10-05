package info.mx.tracks.room.entity

import androidx.room.DatabaseView

// Source
// https://github.com/android/trackr/blob/9b7c22d6e7b2782959389ed115730efcfcddc790/shared/src/main/java/com/example/android/trackr/data/Views.kt#L19

@DatabaseView(
    """
        select id as id,
        trackId as track_restId ,
            count(*) as ratingcount,
            round(avg(rating),1) as ratingavg
        from Comment
        where approved > -1
        group by track_restId
    """
)
data class RatingSum(
    val id: Long,
    val trackRestId: Long,
    val ratingAvg: Float,
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
