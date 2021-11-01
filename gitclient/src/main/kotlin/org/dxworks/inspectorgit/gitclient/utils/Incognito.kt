package org.dxworks.inspectorgit.gitclient.utils

const val consonants = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ"
const val vowels = "aeiouAEIOU"

fun encrypt(car: Char): Char {

    if (car == 'u') return 'a'
    if (car == 'U') return 'A'
    if (car == 'z') return 'b'
    if (car == 'Z') return 'B'

    var indexOfCar: Int = consonants.indexOf(car)

    if (indexOfCar == consonants.length - 1) return consonants[0]

    if (indexOfCar >= 0) return consonants[indexOfCar + 1]

    indexOfCar = vowels.indexOf(car)
    if (indexOfCar == vowels.length - 1) return vowels[0]

    return if (indexOfCar >= 0) vowels[indexOfCar + 1]
    else car
}

fun maskString(name: String): String =
    name.toCharArray().map { encrypt(it) }.joinToString("")
