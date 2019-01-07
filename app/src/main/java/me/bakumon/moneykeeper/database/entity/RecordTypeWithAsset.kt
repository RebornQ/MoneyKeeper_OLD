package me.bakumon.moneykeeper.database.entity

import androidx.room.Relation

/**
 * 包含 Asset 的 RecordType
 * 用于关联查询
 *
 * @author Bakumon https://bakumon.me
 */
class RecordTypeWithAsset : RecordType() {
    @Relation(parentColumn = "assets_id", entityColumn = "id", entity = Assets::class)
    var mAssets: List<Assets>? = null
}
