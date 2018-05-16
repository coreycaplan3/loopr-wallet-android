package org.loopring.looprwallet.core.extensions

import org.loopring.looprwallet.core.models.realm.TrackedRealmObject
import org.loopring.looprwallet.core.utilities.RealmUtility
import io.realm.*
import java.util.*
import kotlin.reflect.KProperty

/**
 * Created by Corey Caplan on 3/16/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */

/**
 * Upserts data and sets the [TrackedRealmObject.lastUpdated] field if possible
 *
 * @see Realm.insertOrUpdate
 */
fun <T : RealmModel> Realm.upsert(collection: Collection<T>) {
    collection.forEach {
        if (it is TrackedRealmObject) {
            it.lastUpdated = Date()
        }
        this.insertOrUpdate(it)
    }
}

/**
 * Upserts data and sets the [TrackedRealmObject.lastUpdated] field if possible
 *
 * @see Realm.insertOrUpdate
 */
fun <T : RealmModel> Realm.upsert(data: T) {
    (data as? TrackedRealmObject)?.lastUpdated = Date()

    this.insertOrUpdate(data)
}

/**
 * Upserts data and sets the [TrackedRealmObject.lastUpdated] field if possible
 *
 * @see Realm.copyToRealm
 */
fun <T : RealmModel> Realm.upsertCopyToRealm(collection: Collection<T>): Collection<T> {
    collection.forEach {
        if (it !is TrackedRealmObject) return@forEach
        it.lastUpdated = Date()
    }

    return this.copyToRealm(collection)
}

/**
 * Upserts data and sets the [TrackedRealmObject.lastUpdated] field if possible
 *
 * @see Realm.copyToRealm
 */
fun <T : RealmModel> Realm.upsertCopyToRealm(data: T): T {
    (data as? TrackedRealmObject)?.lastUpdated = Date()

    return this.copyToRealm(data)
}

/**
 * Removes all listeners attached this [Realm] and close it, if the [Realm] is not null.
 */
fun Realm?.removeAllListenersAndClose() {
    val realm = this ?: return
    if (!realm.isClosed) {
        realm.removeAllChangeListeners()
        realm.close()
    }
}

/**
 * Equal-to comparison.
 *
 * @param lastField The last field in the nested chain to be used in the query.
 * @param value the value to compare with.
 * @param case The case to use, if the [value] provided is of type string.
 * @return The query object.
 */
fun <E : RealmModel> RealmQuery<E>.like(
        lastField: KProperty<String>,
        value: String,
        case: Case = Case.INSENSITIVE
): RealmQuery<E> {
    return like(listOf(), lastField, value, case)
}

/**
 * Like comparison.
 *
 * @param lastField The last field in the nested chain to be used in the query.
 * @param value the value to compare with.
 * @param case The case to use, if the [value] provided is of type string.
 * @return The query object.
 * @see [RealmQuery.like]
 */
fun <E : RealmModel> RealmQuery<E>.like(
        orderedNestedFields: List<KProperty<*>>,
        lastField: KProperty<String>,
        value: String,
        case: Case = Case.INSENSITIVE
): RealmQuery<E> {
    val formattedFields = RealmUtility.formatNestedFields(orderedNestedFields, lastField)
    return this.like(formattedFields, value, case)
}

/**
 * Equal-to comparison.
 *
 * @param value the value to compare with.
 * @param case The case to use, if the [value] provided is of type string.
 * @return The query object.
 */
inline fun <E : RealmModel, reified T> RealmQuery<E>.equalTo(
        lastField: KProperty<T>,
        value: T?,
        case: Case = Case.SENSITIVE
): RealmQuery<E> {
    return equalTo(listOf(), lastField, value, case)
}

/**
 * Equal-to comparison.
 *
 * Example calls for [orderedNestedFields] and [lastField] on a *DogOwner* class:
 * - list(), "numberOfDogs" --> "numberOfDogs"
 * - list("dogs"), "breed" --> "dogs.breed"
 * - list("dogs", "animalType"), "mammal" --> "dogs.animalType.mammal"
 * - list("dogs", "animalType", "mammal"), "mammal" --> EXCEPTION for duplicate type
 *
 * @param orderedNestedFields The first list of fields for the query. Adding more than one field
 * maps to this query to be a nested query. Each property is appended onto the each other to create
 * the nest. For example, chaining \["dogs", "color"\] --> to "dogs.color". This field can be left
 * empty and should only be used for nested queries. This list should **NOT** contain the
 * [lastField] value.
 * @param lastField The last field in the nested chain to be used in the query.
 * @param value the value to compare with.
 * @param case The case to use, if the [value] provided is of type string.
 * @return The query object.
 */
inline fun <E : RealmModel, reified T> RealmQuery<E>.equalTo(
        orderedNestedFields: List<KProperty<*>>,
        lastField: KProperty<T>,
        value: T?,
        case: Case = Case.SENSITIVE
): RealmQuery<E> {
    val formattedFields = RealmUtility.formatNestedFields(orderedNestedFields, lastField)
    return when (T::class) {
        String::class -> this.equalTo(formattedFields, (value as? String), case)
        Byte::class -> this.equalTo(formattedFields, (value as? Byte))
        ByteArray::class -> this.equalTo(formattedFields, (value as? ByteArray))
        Short::class -> this.equalTo(formattedFields, (value as? Short))
        Int::class -> this.equalTo(formattedFields, (value as? Int))
        Long::class -> this.equalTo(formattedFields, (value as? Long))
        Double::class -> this.equalTo(formattedFields, (value as? Double))
        Float::class -> this.equalTo(formattedFields, (value as? Float))
        Boolean::class -> this.equalTo(formattedFields, (value as? Boolean))
        Date::class -> this.equalTo(formattedFields, (value as? Date))
        else -> throw IllegalArgumentException("Invalid argument type, found: ")
    }
}

/**
 * Equal-to comparison.
 *
 * Example calls for [orderedNestedFields] and [lastField] on a *DogOwner* class:
 * - list(), "numberOfDogs" --> "numberOfDogs"
 * - list("dogs"), "breed" --> "dogs.breed"
 * - list("dogs", "animalType"), "mammal" --> "dogs.animalType.mammal"
 * - list("dogs", "animalType", "mammal"), "mammal" --> EXCEPTION for duplicate type
 *
 * @param orderedNestedFields The first list of fields for the query. Adding more than one field
 * maps to this query to be a nested query. Each property is appended onto the each other to create
 * the nest. For example, chaining \["dogs", "color"\] --> to "dogs.color". This field can be left
 * empty and should only be used for nested queries. This list should **NOT** contain the
 * [lastField] value.
 * @param lastField The last field in the nested chain to be used in the query.
 * @param value the value to compare with.
 * @param case The case to use, if the [value] provided is of type string.
 * @return The query object.
 */
inline fun <E : RealmModel, reified T> RealmQuery<E>.notEqualTo(
        orderedNestedFields: List<KProperty<*>>,
        lastField: KProperty<T>,
        value: T?,
        case: Case = Case.SENSITIVE
): RealmQuery<E> {
    val formattedFields = RealmUtility.formatNestedFields(orderedNestedFields, lastField)
    return when (T::class) {
        String::class -> this.notEqualTo(formattedFields, (value as? String), case)
        Byte::class -> this.notEqualTo(formattedFields, (value as? Byte))
        ByteArray::class -> this.notEqualTo(formattedFields, (value as? ByteArray))
        Short::class -> this.notEqualTo(formattedFields, (value as? Short))
        Int::class -> this.notEqualTo(formattedFields, (value as? Int))
        Long::class -> this.notEqualTo(formattedFields, (value as? Long))
        Double::class -> this.notEqualTo(formattedFields, (value as? Double))
        Float::class -> this.notEqualTo(formattedFields, (value as? Float))
        Boolean::class -> this.notEqualTo(formattedFields, (value as? Boolean))
        Date::class -> this.notEqualTo(formattedFields, (value as? Date))
        else -> throw IllegalArgumentException("Invalid argument type, found: ")
    }
}

/**
 * Equal-to comparison.
 *
 * Example calls for [orderedNestedFields] and [lastField] on a *DogOwner* class:
 * - list(), "numberOfDogs" --> "numberOfDogs"
 * - list("dogs"), "breed" --> "dogs.breed"
 * - list("dogs", "animalType"), "mammal" --> "dogs.animalType.mammal"
 * - list("dogs", "animalType", "mammal"), "mammal" --> EXCEPTION for duplicate type
 *
 * @param orderedNestedFields The first list of fields for the query. Adding more than one field
 * maps to this query to be a nested query. Each property is appended onto the each other to create
 * the nest. For example, chaining \["dogs", "color"\] --> to "dogs.color". This field can be left
 * empty and should only be used for nested queries. This list should **NOT** contain the
 * [lastField] value.
 * @param lastField The last field in the nested chain to be used in the query.
 * @param value the value to compare with.
 * @param case The case to use, if the [value] provided is of type string.
 * @return The query object.
 */
inline fun <E : RealmModel, reified T> RealmQuery<E>.notEqualTo(
        lastField: KProperty<T>,
        value: T?,
        case: Case = Case.SENSITIVE
): RealmQuery<E> {
    return notEqualTo(listOf(), lastField, value, case)
}

/**
 * Sorting
 *
 * Example calls for [orderedNestedFields] and [lastField] on a *DogOwner* class:
 * - list(), "numberOfDogs" --> "numberOfDogs"
 * - list("dogs"), "breed" --> "dogs.breed"
 * - list("dogs", "animalType"), "mammal" --> "dogs.animalType.mammal"
 * - list("dogs", "animalType", "mammal"), "mammal" --> EXCEPTION for duplicate type
 *
 * @param orderedNestedFields The first list of fields for the query. Adding more than one field
 * maps to this query to be a nested query. Each property is appended onto the each other to create
 * the nest. For example, chaining \["dogs", "color"\] --> to "dogs.color". This field can be left
 * empty and should only be used for nested queries. This list should **NOT** contain the
 * [lastField] value.
 * @param lastField The last field in the nested chain to be used in the query.
 * @param value the value to compare with.
 * @param case The case to use, if the [value] provided is of type string.
 * @return The query object.
 */
inline fun <E : RealmModel, reified T> RealmQuery<E>.sort(
        orderedNestedFields: List<KProperty<*>>,
        lastField: KProperty<T>,
        sortOrder: Sort = Sort.ASCENDING
): RealmQuery<E> {
    val formattedFields: String = RealmUtility.formatNestedFields(orderedNestedFields, lastField)
    return when (T::class) {
        String::class -> this.sort(formattedFields, sortOrder)
        Byte::class -> this.sort(formattedFields)
        ByteArray::class -> this.sort(formattedFields)
        Short::class -> this.sort(formattedFields)
        Int::class -> this.sort(formattedFields)
        Long::class -> this.sort(formattedFields)
        Double::class -> this.sort(formattedFields)
        Float::class -> this.sort(formattedFields)
        Boolean::class -> this.sort(formattedFields)
        Date::class -> this.sort(formattedFields)
        else -> throw IllegalArgumentException("Invalid argument type, found: ")
    }
}

/**
 * @param sortField The field on which to sort.
 * @param sortOrder Either [Sort.DESCENDING] or [Sort.ASCENDING]
 */
fun <E : RealmModel> RealmQuery<E>.sort(
        sortField: KProperty<*>,
        sortOrder: Sort = Sort.ASCENDING
): RealmQuery<E> {
    return this.sort(listOf(), sortField, sortOrder)
}