package org.github.KS2003.CasinoBot

import com.github.KS2003.telegramAPI.KeyboardButton
import com.github.KS2003.telegramAPI.ReplyKeyboardMarkup

fun rouletteMarkup() = ReplyKeyboardMarkup(
    listOf(
        listOf(KeyboardButton("Even"), KeyboardButton("Odd")),
        listOf(KeyboardButton("Red"), KeyboardButton("Black")),
        listOf(KeyboardButton("1to18"), KeyboardButton("19to36")),
        listOf(KeyboardButton("Single"), KeyboardButton("Split")),
        listOf(KeyboardButton("Street"), KeyboardButton("Basket")),
        listOf(KeyboardButton("Corner"), KeyboardButton("SixLine")),
        listOf(KeyboardButton("Dozen"), KeyboardButton("Column"))
    )
)

fun roulette1Markup() = ReplyKeyboardMarkup(
    listOf(
        listOf(KeyboardButton("Next bet"), KeyboardButton("Start roulette"))
    )
)

fun betMarkup() = ReplyKeyboardMarkup(
    listOf(
        listOf(
            KeyboardButton("10₽"),
            KeyboardButton("25₽"),
            KeyboardButton("50₽")
        ),
        listOf(
            KeyboardButton("100₽"),
            KeyboardButton("250₽"),
            KeyboardButton("500₽")
        ),
        listOf(
            KeyboardButton("1000₽"),
            KeyboardButton("2500₽"),
            KeyboardButton("5000₽")
        )
    )
)

fun singleMarkup() = ReplyKeyboardMarkup(
    listOf(
        listOf(
            KeyboardButton("0.")
        ),
        listOf(
            KeyboardButton("1."),
            KeyboardButton("2."),
            KeyboardButton("3.")
        ),
        listOf(
            KeyboardButton("4."),
            KeyboardButton("5."),
            KeyboardButton("6.")
        ),
        listOf(
            KeyboardButton("7."),
            KeyboardButton("8."),
            KeyboardButton("9.")
        ),
        listOf(
            KeyboardButton("10."),
            KeyboardButton("11."),
            KeyboardButton("12.")
        ),
        listOf(
            KeyboardButton("13."),
            KeyboardButton("14."),
            KeyboardButton("15.")
        ),
        listOf(
            KeyboardButton("16."),
            KeyboardButton("17."),
            KeyboardButton("18.")
        ),
        listOf(
            KeyboardButton("19."),
            KeyboardButton("20."),
            KeyboardButton("21.")
        ),
        listOf(
            KeyboardButton("22."),
            KeyboardButton("23."),
            KeyboardButton("24.")
        ),
        listOf(
            KeyboardButton("25."),
            KeyboardButton("26."),
            KeyboardButton("27.")
        ),
        listOf(
            KeyboardButton("28."),
            KeyboardButton("29."),
            KeyboardButton("30.")
        ),
        listOf(
            KeyboardButton("31."),
            KeyboardButton("32."),
            KeyboardButton("33.")
        ),
        listOf(
            KeyboardButton("34."),
            KeyboardButton("35."),
            KeyboardButton("36.")
        )
    )
)

fun splitMarkup() = ReplyKeyboardMarkup(
    listOf(
        listOf(
            KeyboardButton("Vertical Split"),
            KeyboardButton("Horizontal Split")
        )
    )
)

fun verticalSplitMarkup() = ReplyKeyboardMarkup(
    List(11) {i ->
        listOf(
            KeyboardButton((3 * i + 1).toString() + "|" + (3 * i + 4).toString()),
            KeyboardButton((3 * i + 2).toString() + "|" + (3 * i + 5).toString()),
            KeyboardButton((3 * i + 3).toString() + "|" + (3 * i + 6).toString())
        )
    }
)

fun horizontalSplitMarkup() = ReplyKeyboardMarkup(
    List(12) {i -> listOf(
        KeyboardButton((3 * i + 1).toString() + "-" + (3 * i + 2).toString()),
        KeyboardButton((3 * i + 2).toString() + "-" + (3 * i + 3).toString())
    )}
)

fun dozenMarkup() = ReplyKeyboardMarkup(
    listOf(
        listOf(
            KeyboardButton("1st dozen")
        ),
        listOf(
            KeyboardButton("2nd dozen")
        ),
        listOf(
            KeyboardButton("3rd dozen")
        )
    )
)

fun columnMarkup() = ReplyKeyboardMarkup(
    listOf(
        listOf(
            KeyboardButton("1st column"),
            KeyboardButton("2nd column"),
            KeyboardButton("3rd column")
        )
    )
)

fun streetMarkup() = ReplyKeyboardMarkup(
    List(12) {i -> listOf(
        KeyboardButton((3 * i + 1).toString() + "-" + (3 * i + 3).toString())
    )}
)

fun lineMarkup() = ReplyKeyboardMarkup(
    List(11) { i -> listOf(
        KeyboardButton((3 * i + 1).toString() + "^" + (3 * i + 6).toString())
    )}
)

fun cornerMarkup() = ReplyKeyboardMarkup(
    List(11) { i -> listOf(
        KeyboardButton((3 * i + 1).toString() + "^" + (3 * i + 5).toString()),
        KeyboardButton((3 * i + 2).toString() + "^" + (3 * i + 6).toString())
    )}
)

fun lobbyMarkup() = ReplyKeyboardMarkup(
    listOf(
        listOf(KeyboardButton("Join lobby")),
        listOf(KeyboardButton("Create lobby")),
        listOf(KeyboardButton("Return to menu"))
    )
)

fun gameTypeMarkup() = ReplyKeyboardMarkup(
    listOf(
        listOf(KeyboardButton("Dice"))
    )
)

fun hostMarkup() = ReplyKeyboardMarkup(
    listOf(
        listOf(KeyboardButton("Start")),
        listOf(KeyboardButton("Leave and delete lobby"))
    )
)

fun menuMarkup() = ReplyKeyboardMarkup(
    listOf(
        listOf(KeyboardButton("Coin"), KeyboardButton("Roulette")),
        listOf(KeyboardButton("Balance"), KeyboardButton("Top")),
        listOf(KeyboardButton("Create lobby"), KeyboardButton("Get list of lobbies"))
    )
)
