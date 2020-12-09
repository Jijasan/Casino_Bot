package org.github.KS2003.CasinoBot

import java.io.File
import com.github.KS2003.telegramAPI.*
import kotlin.math.min

val data = mutableMapOf<Int, Player>()
val db = "db.txt"

fun runCoin(bet: Int, bot: Bot, msg: Message) {
    val delta = run(data[msg.from!!.id]!!.game)
    data[msg.from!!.id]!!.balance += delta
    bot.sendMessage(
        msg.chat.id,  (if (delta > 0) "You win! " else "You lose! ") + "Your balance: " + data[msg.from!!.id]!!.balance.toString(),
        reply_markup = menuMarkup()
    )
}

fun initPlayer(msg: Message) {
    if (data[msg.from!!.id] == null) {
        val player = Player(
            1000, Game(Game.Type.NON), msg.from!!.first_name +
                    if (msg.from!!.last_name == null) "" else (" " + msg.from!!.last_name!!))
        data[msg.from!!.id] = player
    }
}

fun createBet(bet: Int, bot: Bot) {
    bot.onCommand(bet.toString()) {msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        if (data[msg.from!!.id]!!.balance < bet)
            bot.sendMessage(msg.chat.id, "Your balance is to low", reply_markup = ReplyKeyboardMarkup(listOf(
                listOf(KeyboardButton("/menu")))))
        else {
            when (data[msg.from!!.id]!!.game.type) {
                Game.Type.COIN -> {
                    data [msg.from!!.id]!!.game = Game(Game.Type.COIN, bet = bet)
                    runCoin(bet, bot, msg)
                    data [msg.from!!.id]!!.game = Game(Game.Type.NON)
                }
                Game.Type.ROULETTE -> {
                    if (data[msg.from!!.id]!!.game.numbers != null)
                        data[msg.from!!.id]!!.game.numbers!![data[msg.from!!.id]!!.game.numbers!!.lastIndex] =
                            Pair(Pair(data[msg.from!!.id]!!.game.numbers!!.last().first.first, bet),
                                data[msg.from!!.id]!!.game.numbers!!.last().second)
                    bot.sendMessage(msg.chat.id, "Another one bet?", reply_markup = roulette1Markup())
                }
                else -> data [msg.from!!.id]!!.game = Game(Game.Type.NON)
            }
        }
    }
}

fun createField(numbers: List<Int>, command: String, bot: Bot, multi: Int) {
    bot.onCommand(command) {msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        if (data[msg.from!!.id]!!.game.numbers == null)
            data[msg.from!!.id]!!.game.numbers = mutableListOf(Pair(Pair(multi, 0), numbers.toMutableList()))
        else
            data[msg.from!!.id]!!.game.numbers = (data[msg.from!!.id]!!.game.numbers!! + (Pair(Pair(multi, 0), numbers))).toMutableList()
        bot.sendMessage(msg.chat.id, "Choose your bet: ", reply_markup = betMarkup())
    }
}

fun readData() {
    val base = File(db)
    base.forEachLine { line ->
        val input = line.split(" ")
        val player = Player(input[1].toInt(), Game(Game.Type.NON), input.subList(3, input.lastIndex + 1).joinToString(" "))
        when (input[2]) {
            "COIN"     -> player.game = Game(Game.Type.COIN)
            "ROULETTE" -> player.game = Game(Game.Type.ROULETTE)
        }
        data[input[0].toInt()] = player
    }
}

fun writeData() {
    val base = File(db)
    var tmp = ""
    data.forEach { id, player ->
        tmp += id.toString() + " " + player.toString() + "\n"
    }
    base.writeText(tmp)
}

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

fun menuMarkup() = ReplyKeyboardMarkup(
                       listOf(
                           listOf(KeyboardButton("Coin"), KeyboardButton("Roulette")),
                           listOf(KeyboardButton("Balance"), KeyboardButton("Top"))
                       )
                   )

fun betMarkup() = ReplyKeyboardMarkup(
                      listOf(
                          listOf(
                              KeyboardButton("10"),
                              KeyboardButton("25"),
                              KeyboardButton("50")
                          ),
                          listOf(
                              KeyboardButton("100"),
                              KeyboardButton("250"),
                              KeyboardButton("500")
                          ),
                          listOf(
                              KeyboardButton("1000"),
                              KeyboardButton("2500"),
                              KeyboardButton("5000")
                          )
                      )
                  )

fun main() {
    readData()
    val token = System.getenv("TOKEN")
    println(token)
    val bot = Bot(token)

    bot.onCommand("/start") { msg, _ ->
        bot.sendMessage(
            msg.chat.id,
            "Choose game",
            reply_markup = menuMarkup()
        )
    }

    bot.onCommand("/menu") { msg, _ ->
        bot.sendMessage(
            msg.chat.id,
            "Choose game",
            reply_markup = menuMarkup()
        )
    }

    bot.onCommand("Balance") { msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        bot.sendMessage(msg.chat.id, data[msg.from!!.id]!!.balance.toString())
    }

    bot.onCommand("/setBalance") {msg, _ ->
        if (msg.text == null || msg.from == null)
            return@onCommand
        val input = msg.text!!.split(" ")
        if (input.size < 3)
            return@onCommand
        if (input[1] != "jija")
            return@onCommand
        data[msg.from!!.id]!!.balance = input[2].toInt()
    }

    bot.onCommand("Coin") { msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        data[msg.from!!.id]!!.game = Game(Game.Type.COIN)
        bot.sendMessage(
            msg.chat.id,
            "Choose your bet",
            reply_markup = betMarkup()
        )
    }

    bot.onCommand("Roulette") { msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        data[msg.from!!.id]!!.game = Game(Game.Type.ROULETTE)
        bot.sendMessage(msg.chat.id, "Choose numbers", reply_markup = rouletteMarkup())
    }

    createField(List<Int>(18){i -> 2 * (i + 1)}, "Even", bot, 1)
    createField(List<Int>(18){i -> 2 * i + 1  }, "Odd", bot, 1)
    createField(listOf(1, 3, 5, 7,  9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36), "Red", bot, 2)
    createField(listOf(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35), "Black", bot, 2)
    createField(List<Int>(18){i -> i + 1      }, "1to18", bot, 1)
    createField(List<Int>(18){i -> 19 + i     }, "19to36", bot, 1)

    bot.onCommand("Next bet") {msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        bot.sendMessage(msg.chat.id, "Choose numbers", reply_markup = rouletteMarkup())
    }

    bot.onCommand("Start roulette") {msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        val delta = run(data[msg.from!!.id]!!.game)
        data[msg.from!!.id]!!.balance += delta
        bot.sendMessage(msg.chat.id, (data[msg.from!!.id]!!.game.win!!.toString() + ". "
                                        +  if (delta > 0) "You win " + delta.toString()
                                           else "You lose " + (-delta).toString()) + ". Your balance: "
            + data[msg.from!!.id]!!.balance, reply_markup = menuMarkup())
    }

    bot.onCommand("Top") { msg, _ ->
        var message = ""
        var top = data.values.toList().sorted()
        top = top.subList(0, min(10, top.size))
        top.forEach {player ->  message += player.name + " " + player.balance + "\n"}
        bot.sendMessage(msg.chat.id, message, reply_markup = menuMarkup())
    }

    createBet(10, bot)
    createBet(25, bot)
    createBet(50, bot)
    createBet(100, bot)
    createBet(250, bot)
    createBet(500, bot)
    createBet(1000, bot)
    createBet(2500, bot)
    createBet(5000, bot)

    while (true) {
        writeData()
        println("Data saved")
        Thread.sleep(1000)
    }
}
