package util

fun <T> List<T>.getRolling(index: Int): T {
    return get(index % size)
}