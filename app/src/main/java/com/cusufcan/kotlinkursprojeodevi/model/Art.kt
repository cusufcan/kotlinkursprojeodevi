package com.cusufcan.kotlinkursprojeodevi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Art(
    @ColumnInfo(name = "artName")
    var artName: String?,

    @ColumnInfo(name = "artistName")
    var artistName: String?,

    @ColumnInfo(name = "artYear")
    var artYear: String?,

    @ColumnInfo(name = "image")
    var image: ByteArray?
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}