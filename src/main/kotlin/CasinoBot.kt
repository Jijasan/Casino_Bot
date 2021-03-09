package org.github.KS2003.CasinoBot

import com.github.KS2003.telegramAPI.Bot
import com.github.KS2003.telegramAPI.KeyboardButton
import com.github.KS2003.telegramAPI.Message
import com.github.KS2003.telegramAPI.ReplyKeyboardMarkup
import java.io.File
import org.github.KS2003.CasinoBot.games.Dice
import kotlin.math.min

val data = mutableMapOf<Int, Player>()
var lobbies = mutableMapOf<Int, Lobby>()
val db = "db.txt"

fun runCoin(bot: Bot, msg: Message) {
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
    bot.onCommand(bet.toString()+"₽") {msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        if (data[msg.from!!.id]!!.balance < bet)
            bot.sendMessage(msg.chat.id, "Your balance is to low", reply_markup = ReplyKeyboardMarkup(listOf(
                listOf(KeyboardButton("/menu"))))
            )
        else {
            if (lobbies.contains(data[msg.from!!.id]!!.lobbyId)) {
                lobbies[data[msg.from!!.id]!!.lobbyId]!!.bet = bet
                bot.sendMessage(msg.chat.id, "Lobby created", reply_markup = hostMarkup())
            }
            else when (data[msg.from!!.id]!!.game.type) {
                Game.Type.COIN -> {
                    data [msg.from!!.id]!!.game = Game(Game.Type.COIN, bet = bet)
                    runCoin(bot, msg)
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

fun createRoulette(bot: Bot) {
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
    createField(List<Int>(4){i -> i     }, "Basket", bot, 8)

    bot.onCommand("Single") {msg, _ ->
        bot.sendMessage(msg.chat.id, "Choose field", reply_markup = singleMarkup())
    }
    for (i in 0..36) {
        bot.onCommand(i.toString() + ".") {msg, _ ->
            if (msg.from == null)
                return@onCommand
            initPlayer(msg)
            data[msg.from!!.id]!!.game.numbers = ((if (data[msg.from!!.id]!!.game.numbers == null)
                mutableListOf<Pair<Pair<Int, Int>, List<Int>>>()
            else
                data[msg.from!!.id]!!.game.numbers!!)
                    + Pair(Pair(35, 0), listOf(i))).toMutableList()
            bot.sendMessage(msg.chat.id, "Choose your bet", reply_markup = betMarkup())
        }
    }

    bot.onCommand("Split") {msg, _ ->
        bot.sendMessage(msg.chat.id, "Choose split type", reply_markup = splitMarkup())
    }
    bot.onCommand("Vertical Split") {msg, _ ->
        bot.sendMessage(msg.chat.id, "Choose field", reply_markup = verticalSplitMarkup())
    }
    bot.onCommand("Horizontal Split") {msg, _ ->
        bot.sendMessage(msg.chat.id, "Choose field", reply_markup = horizontalSplitMarkup())
    }
    for (i in 1..33) {
        bot.onCommand(i.toString() + "|" + (i + 3).toString()) {msg, _ ->
            if (msg.from == null)
                return@onCommand
            initPlayer(msg)
            data[msg.from!!.id]!!.game.numbers = ((if (data[msg.from!!.id]!!.game.numbers == null)
                mutableListOf<Pair<Pair<Int, Int>, List<Int>>>()
            else
                data[msg.from!!.id]!!.game.numbers!!)
                    + Pair(Pair(17, 0), listOf(i, i + 3))).toMutableList()
            bot.sendMessage(msg.chat.id, "Choose your bet", reply_markup = betMarkup())
        }
    }
    for (i in 1..35) {
        if (i % 3 == 0)
            continue
        bot.onCommand(i.toString() + "-" + (i + 1).toString()) {msg, _ ->
            if (msg.from == null)
                return@onCommand
            initPlayer(msg)
            data[msg.from!!.id]!!.game.numbers = ((if (data[msg.from!!.id]!!.game.numbers == null)
                mutableListOf<Pair<Pair<Int, Int>, List<Int>>>()
            else
                data[msg.from!!.id]!!.game.numbers!!)
                    + Pair(Pair(17, 0), listOf(i, i + 1))).toMutableList()
            bot.sendMessage(msg.chat.id, "Choose your bet", reply_markup = betMarkup())
        }
    }

    bot.onCommand("Dozen") {msg, _ ->
        bot.sendMessage(msg.chat.id, "Choose dozen", reply_markup = dozenMarkup())
    }
    bot.onCommand("1st dozen") {msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        data[msg.from!!.id]!!.game.numbers = ((if (data[msg.from!!.id]!!.game.numbers == null)
            mutableListOf<Pair<Pair<Int, Int>, List<Int>>>()
        else
            data[msg.from!!.id]!!.game.numbers!!)
                + Pair(Pair(2, 0), (1..12).toList())).toMutableList()
        bot.sendMessage(msg.chat.id, "Choose your bet", reply_markup = betMarkup())
    }
    bot.onCommand("2nd dozen") {msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        data[msg.from!!.id]!!.game.numbers = ((if (data[msg.from!!.id]!!.game.numbers == null)
            mutableListOf<Pair<Pair<Int, Int>, List<Int>>>()
        else
            data[msg.from!!.id]!!.game.numbers!!)
                + Pair(Pair(2, 0), (13..24).toList())).toMutableList()
        bot.sendMessage(msg.chat.id, "Choose your bet", reply_markup = betMarkup())
    }
    bot.onCommand("3rd dozen") {msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        data[msg.from!!.id]!!.game.numbers = ((if (data[msg.from!!.id]!!.game.numbers == null)
            mutableListOf<Pair<Pair<Int, Int>, List<Int>>>()
        else
            data[msg.from!!.id]!!.game.numbers!!)
                + Pair(Pair(2, 0), (25..36).toList())).toMutableList()
        bot.sendMessage(msg.chat.id, "Choose your bet", reply_markup = betMarkup())
    }

    bot.onCommand("Column") {msg, _ ->
        bot.sendMessage(msg.chat.id, "Choose column", reply_markup = columnMarkup())
    }
    bot.onCommand("1st column") {msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        data[msg.from!!.id]!!.game.numbers = ((if (data[msg.from!!.id]!!.game.numbers == null)
            mutableListOf<Pair<Pair<Int, Int>, List<Int>>>()
        else
            data[msg.from!!.id]!!.game.numbers!!)
                + Pair(Pair(2, 0), (1..34 step 3).toList())).toMutableList()
        bot.sendMessage(msg.chat.id, "Choose your bet", reply_markup = betMarkup())
    }
    bot.onCommand("2nd column") {msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        data[msg.from!!.id]!!.game.numbers = ((if (data[msg.from!!.id]!!.game.numbers == null)
            mutableListOf<Pair<Pair<Int, Int>, List<Int>>>()
        else
            data[msg.from!!.id]!!.game.numbers!!)
                + Pair(Pair(2, 0), (2..35 step 3).toList())).toMutableList()
        bot.sendMessage(msg.chat.id, "Choose your bet", reply_markup = betMarkup())
    }
    bot.onCommand("3rd column") {msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        data[msg.from!!.id]!!.game.numbers = ((if (data[msg.from!!.id]!!.game.numbers == null)
            mutableListOf<Pair<Pair<Int, Int>, List<Int>>>()
        else
            data[msg.from!!.id]!!.game.numbers!!)
                + Pair(Pair(2, 0), (3..36 step 3).toList())).toMutableList()
        bot.sendMessage(msg.chat.id, "Choose your bet", reply_markup = betMarkup())
    }

    bot.onCommand("Street") {msg, _ ->
        bot.sendMessage(msg.chat.id, "Choose line", reply_markup = streetMarkup())
    }
    for (i in 1..34 step 3) {
        bot.onCommand(i.toString() + "-" + (i + 2).toString()) {msg, _ ->
            if (msg.from == null)
                return@onCommand
            initPlayer(msg)
            data[msg.from!!.id]!!.game.numbers = ((if (data[msg.from!!.id]!!.game.numbers == null)
                mutableListOf<Pair<Pair<Int, Int>, List<Int>>>()
            else
                data[msg.from!!.id]!!.game.numbers!!)
                    + Pair(Pair(11, 0), listOf(i, i + 1, i + 2))).toMutableList()
            bot.sendMessage(msg.chat.id, "Choose your bet", reply_markup = betMarkup())
        }
    }

    bot.onCommand("SixLine") {msg, _ ->
        bot.sendMessage(msg.chat.id, "Choose line", reply_markup = lineMarkup())
    }
    for (i in 1..31 step 3) {
        bot.onCommand(i.toString() + "^" + (i + 5).toString()) {msg, _ ->
            if (msg.from == null)
                return@onCommand
            initPlayer(msg)
            data[msg.from!!.id]!!.game.numbers = ((if (data[msg.from!!.id]!!.game.numbers == null)
                mutableListOf<Pair<Pair<Int, Int>, List<Int>>>()
            else
                data[msg.from!!.id]!!.game.numbers!!)
                    + Pair(Pair(5, 0), (i..i+5).toList())).toMutableList()
            bot.sendMessage(msg.chat.id, "Choose your bet", reply_markup = betMarkup())
        }
    }

    bot.onCommand("Corner") {msg, _ ->
        bot.sendMessage(msg.chat.id, "Choose corner", reply_markup = cornerMarkup())
    }
    for (i in 1..31 step 3) {
        bot.onCommand(i.toString() + "^" + (i + 4).toString()) {msg, _ ->
            if (msg.from == null)
                return@onCommand
            initPlayer(msg)
            data[msg.from!!.id]!!.game.numbers = ((if (data[msg.from!!.id]!!.game.numbers == null)
                mutableListOf()
            else
                data[msg.from!!.id]!!.game.numbers!!)
                    + Pair(Pair(8, 0), listOf(i, i + 1, i + 3, i + 4))).toMutableList()
            bot.sendMessage(msg.chat.id, "Choose your bet", reply_markup = betMarkup())
        }
        bot.onCommand((i + 1).toString() + "^" + (i + 5).toString()) {msg, _ ->
            if (msg.from == null)
                return@onCommand
            initPlayer(msg)
            data[msg.from!!.id]!!.game.numbers = ((if (data[msg.from!!.id]!!.game.numbers == null)
                mutableListOf()
            else
                data[msg.from!!.id]!!.game.numbers!!)
                    + Pair(Pair(8, 0), listOf(i + 1, i + 2, i + 4, i + 5))).toMutableList()
            bot.sendMessage(msg.chat.id, "Choose your bet", reply_markup = betMarkup())
        }
    }

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

    createBet(10, bot)
    createBet(25, bot)
    createBet(50, bot)
    createBet(100, bot)
    createBet(250, bot)
    createBet(500, bot)
    createBet(1000, bot)
    createBet(2500, bot)
    createBet(5000, bot)
}

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
        bot.sendMessage(msg.chat.id, data[msg.from!!.id]!!.balance.toString() + "₽")
    }

    bot.onCommand("/setBalance") { msg, _ ->
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
        bot.sendMessage(msg.chat.id, "Choose your bet", reply_markup = betMarkup())
    }

    createRoulette(bot)

    bot.onCommand("Create lobby") { msg, _ ->
        bot.sendMessage(msg.chat.id, "Choose game", reply_markup = gameTypeMarkup())
    }

    bot.onCommand("Dice") { msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        val id = (Math.random() * 1000000000).toInt()
        lobbies.put(id, Lobby(Dice(bot), id, bot))
        lobbies[id]!!.connect(data[msg.from!!.id]!!, msg.chat.id)
        bot.sendMessage(msg.chat.id, "Choose bet", reply_markup = betMarkup())
    }

    bot.onCommand("Start") { msg, _ ->
        if (msg.from == null || !data.contains(msg.from!!.id) || !lobbies.contains(data[msg.from!!.id]!!.lobbyId))
            return@onCommand
        lobbies[data[msg.from!!.id]!!.lobbyId]!!.start()
        lobbies = lobbies.minus(data[msg.from!!.id]!!.lobbyId).toMutableMap()
    }

    bot.onCommand("Leave and delete lobby") { msg, _ ->
        if (msg.from == null || !data.contains(msg.from!!.id))
            return@onCommand
        val index = data[msg.from!!.id]!!.lobbyId
        if (!lobbies.contains(index))
            return@onCommand
        val lobby = lobbies[index]!!
        lobby.players.forEach { (player, id) ->
            player.lobbyId = -1
            bot.sendMessage(id, "Lobby deleted", reply_markup = menuMarkup())
        }
        lobbies = lobbies.filter { it.key != lobby.id }.toMutableMap()
    }

    bot.onCommand("Get list of lobbies") { msg, _ ->
        var message = "List of lobbies:\n\n"
        lobbies.forEach { entry: Map.Entry<Int, Lobby> ->
            val index = entry.key
            val lobby = entry.value
            message += "Lobby $index \nGame: ${lobby.game.title} \nBet: ${lobby.bet}\nNumber of players: ${lobby.players.size}/${lobby.game.maximumPlayer}\n\n"
        }
        bot.sendMessage(msg.chat.id, message, reply_markup = lobbyMarkup())
    }

    bot.onCommand("Join lobby") { msg, _ ->
        val lobbiesMarkup = ReplyKeyboardMarkup(lobbies.map { it -> listOf(KeyboardButton("id: ${it.key}")) })
        bot.sendMessage(msg.chat.id, "Choose lobby", reply_markup = lobbiesMarkup)
    }

    bot.onCommand("id: ") { msg, _ ->
        if (msg.from == null)
            return@onCommand
        initPlayer(msg)
        val id = msg.text!!.split(" ").last().toInt()
        if (!lobbies.contains(id))
            return@onCommand
        if (lobbies[id]!!.connect(data[msg.from!!.id]!!, msg.chat.id))
            bot.sendMessage(
                msg.chat.id, "Successfully connected\nPlease wait, until game starts",
                reply_markup = ReplyKeyboardMarkup(listOf(listOf(KeyboardButton("Leave lobby"))))
            )
        else
            bot.sendMessage(msg.chat.id, "Lobby is full", reply_markup = menuMarkup())
    }

    bot.onCommand("Leave lobby") {msg, _ ->
        if (msg.from == null || !data.contains(msg.from!!.id))
            return@onCommand
        val player = data[msg.from!!.id]!!
        if (!lobbies.contains(player.lobbyId))
            return@onCommand
        lobbies[player.lobbyId]!!.disconnect(player, msg.chat.id)
        bot.sendMessage(msg.chat.id, "Successfully left", reply_markup = menuMarkup())
    }

    bot.onCommand("Return to menu") { msg, _ ->
        bot.sendMessage(msg.chat.id, "Choose game", reply_markup = menuMarkup())
    }

    bot.onCommand("Top") { msg, _ ->
        var message = ""
        var top = data.values.toList().sorted()
        top = top.subList(0, min(10, top.size))
        top.forEach { player -> message += player.name + " " + player.balance + "₽\n" }
        bot.sendMessage(msg.chat.id, message, reply_markup = menuMarkup())
    }

    while (true) {
        writeData()
        println("Data saved")
        Thread.sleep(1000)
    }
}
