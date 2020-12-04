import com.elbekD.bot.Bot
import com.elbekD.bot.types.KeyboardButton
import com.elbekD.bot.types.Message
import com.elbekD.bot.types.ReplyKeyboardMarkup
import java.io.File

val data = mutableMapOf<Int, Player>()
val db = "db.txt"

fun runCoin(bet: Int, bot: Bot, msg: Message) {
    data[msg.from!!.id]!!.balance += run(data[msg.from!!.id]!!.game, null, 1, bet)
    bot.sendMessage(
        msg.chat.id, "Your balance: " + data[msg.from!!.id]!!.balance.toString(), markup = ReplyKeyboardMarkup(
            listOf(
                listOf(KeyboardButton("/menu"))
            )
        )
    )
}

fun createBet(bet: Int, bot: Bot) {
    bot.onCommand("/" + bet.toString()) {msg, _ ->
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
        val player = Player(input[1].toInt(), Game.NON)
        when (input[2]) {
            "COIN"     -> player.game = Game.COIN
            "ROULETTE" -> player.game = Game.ROULETTE
        }
        data[input[0].toInt()] = player
    }
}

fun writeData() {
    val base = File(db)
    data.forEach { id, player ->
        base.writeText(id.toString() + " " + player.toString())
    }
}

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
            markup = ReplyKeyboardMarkup(
                listOf(
                    listOf(KeyboardButton("Coin"), KeyboardButton("Roulette"))
                )
            )
        )
    }

    bot.onCommand("/menu") { msg, _ ->
        bot.sendMessage(
            msg.chat.id,
            "Choose game",
            markup = ReplyKeyboardMarkup(
                listOf(
                    listOf(KeyboardButton("/coin"), KeyboardButton("/roulette"))
                )
            )
        )
    }

    bot.onCommand("/setBalance") setBalance@{msg, _ ->
        if (msg.text == null || msg.from == null)
            return@setBalance
        val input = msg.text!!.split(" ")
        if (input.size < 3)
            return@setBalance
        if (input[1] != "Jija")
            return@setBalance
        data[msg.from!!.id]!!.balance = input[2].toInt()
    }

    bot.onCommand("/coin") coin@{ msg, _ ->
        if (msg.from == null)
            return@coin
        if (data[msg.from!!.id] == null)
            data[msg.from!!.id] = Player(1000, Game.NON)
        data[msg.from!!.id]!!.game = Game.COIN
        bot.sendMessage(
            msg.chat.id,
            "Choose your bet:",
            markup = ReplyKeyboardMarkup(
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
        )
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
        Thread.sleep(1000)
    }
}
