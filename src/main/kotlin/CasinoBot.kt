import com.elbekD.bot.Bot
import com.elbekD.bot.types.KeyboardButton
import com.elbekD.bot.types.Message
import com.elbekD.bot.types.ReplyKeyboardMarkup
import java.io.File
import kotlin.math.min

val data = mutableMapOf<Int, Player>()
val db = "db.txt"

fun runCoin(bet: Int, bot: Bot, msg: Message) {
    val delta = run(data[msg.from!!.id]!!.game, null, 1, bet)
    data[msg.from!!.id]!!.balance += delta
    bot.sendMessage(
        msg.chat.id,  (if (delta > 0) "You win! " else "You lose! ") + "Your balance: " + data[msg.from!!.id]!!.balance.toString(), markup = ReplyKeyboardMarkup(
            listOf(
                listOf(KeyboardButton("/menu"))
            )
        )
    )
}

fun createBet(bet: Int, bot: Bot) {
    bot.onCommand("/" + bet.toString()) {msg, _ ->
        if (msg.from == null)
            return@onCommand
        if (data[msg.from!!.id] == null) {
            val player = Player(
                1000, Game.NON, msg.from!!.first_name +
                        if (msg.from!!.last_name == null) "" else (" " + msg.from!!.last_name!!))
            data[msg.from!!.id] = player
        }
        if (data[msg.from!!.id]!!.balance < bet)
            bot.sendMessage(msg.chat.id, "Your balance is to low", markup = ReplyKeyboardMarkup(listOf(
                listOf(KeyboardButton("/menu")))))
        else {
            when (data[msg.from!!.id]!!.game) {
                Game.COIN -> runCoin(bet, bot, msg)
                else -> data [msg.from!!.id]!!.game = Game.NON
            }
        }
    }
}

fun readData() {
    val base = File(db)
    base.forEachLine { line ->
        val input = line.split(" ")
        val player = Player(input[1].toInt(), Game.NON, input[3] + if (input.size > 4) (" " + input[4]) else "")
        when (input[2]) {
            "COIN"     -> player.game = Game.COIN
            "ROULETTE" -> player.game = Game.ROULETTE
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

fun menuMarkup() = ReplyKeyboardMarkup(
                       listOf(
                           listOf(KeyboardButton("/coin"), KeyboardButton("/roulette")),
                           listOf(KeyboardButton("/balance"), KeyboardButton("/top"))
                       )
                   )

fun betMarkup() = ReplyKeyboardMarkup(
                      listOf(
                          listOf(
                              KeyboardButton("/10"),
                              KeyboardButton("/25"),
                              KeyboardButton("/50")
                          ),
                          listOf(
                              KeyboardButton("/100"),
                              KeyboardButton("/250"),
                              KeyboardButton("/500")
                          ),
                          listOf(
                              KeyboardButton("/1000"),
                              KeyboardButton("/2500"),
                              KeyboardButton("/5000")
                          )
                      )
                  )

fun main() {
    readData()
    val token = System.getenv("TOKEN")
    println(token)
    val username = "<BOT USERNAME>"
    val bot = Bot.createPolling(username, token) {
        // below is optional parameters
        // limit = 50
        timeout = 30
        // allowedUpdates = listOf(AllowedUpdate.Message)
        // removeWebhookAutomatically = true
        // period = 1000
    }

    bot.onCommand("/start") { msg, _ ->
        bot.sendMessage(
            msg.chat.id,
            "Choose game",
            markup = menuMarkup()
        )
    }

    bot.onCommand("/menu") { msg, _ ->
        bot.sendMessage(
            msg.chat.id,
            "Choose game",
            markup = menuMarkup()
        )
    }

    bot.onCommand("/balance") { msg, _ ->
        if (msg.from == null)
            return@onCommand
        if (data[msg.from!!.id] == null) {
            val player = Player(
                1000, Game.NON, msg.from!!.first_name +
                        if (msg.from!!.last_name == null) "" else (" " + msg.from!!.last_name!!))
            data[msg.from!!.id] = player
        }
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

    bot.onCommand("/coin") { msg, _ ->
        if (msg.from == null)
            return@onCommand
        if (data[msg.from!!.id] == null) {
            val player = Player(
                1000, Game.NON, msg.from!!.first_name +
                        if (msg.from!!.last_name == null) "" else (" " + msg.from!!.last_name!!))
            data[msg.from!!.id] = player
        }
        data[msg.from!!.id]!!.game = Game.COIN
        bot.sendMessage(
            msg.chat.id,
            "Choose your bet:",
            markup = betMarkup()
        )
    }

    bot.onCommand("/top") { msg, _ ->
        var message = ""
        var top = data.values.toList().sorted()
        top = top.subList(0, min(10, top.size))
        top.forEach {player ->  message += player.name + " " + player.balance + "\n"}
        bot.sendMessage(msg.chat.id, message, markup = menuMarkup())
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

    bot.start()
    while (true) {
        writeData()
        println("Data saved")
        Thread.sleep(1000)
    }
}
