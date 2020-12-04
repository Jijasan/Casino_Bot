import com.elbekD.bot.Bot
import com.elbekD.bot.types.KeyboardButton
import com.elbekD.bot.types.ReplyKeyboardMarkup

fun main() {
    val data = mutableMapOf<Int, Player>()
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

    bot.onCommand("/10") {msg, _ ->
        if (data[msg.from!!.id]!!.balance < 10)
            bot.sendMessage(msg.chat.id, "Your balance is to low", markup = ReplyKeyboardMarkup(listOf(
                listOf(KeyboardButton("/menu")))))
        else {
            data[msg.from!!.id]!!.balance += run(data[msg.from!!.id]!!.game, null, 1, 10)
            bot.sendMessage(
                msg.chat.id, "Your balance: " + data[msg.from!!.id]!!.balance.toString(), markup = ReplyKeyboardMarkup(
                    listOf(
                        listOf(KeyboardButton("/menu"))
                    )
                )
            )
        }
    }

    bot.onCommand("/25") {msg, _ ->
        if (data[msg.from!!.id]!!.balance < 25)
            bot.sendMessage(msg.chat.id, "Your balance is to low", markup = ReplyKeyboardMarkup(listOf(
                listOf(KeyboardButton("/menu")))))
        else {
            data[msg.from!!.id]!!.balance += run(data[msg.from!!.id]!!.game, null, 1, 25)
            bot.sendMessage(
                msg.chat.id, "Your balance: " + data[msg.from!!.id]!!.balance.toString(), markup = ReplyKeyboardMarkup(
                    listOf(
                        listOf(KeyboardButton("/menu"))
                    )
                )
            )
        }
    }

    bot.onCommand("/50") {msg, _ ->
        if (data[msg.from!!.id]!!.balance < 50)
            bot.sendMessage(msg.chat.id, "Your balance is to low", markup = ReplyKeyboardMarkup(listOf(
                listOf(KeyboardButton("/menu")))))
        else {
            data[msg.from!!.id]!!.balance += run(data[msg.from!!.id]!!.game, null, 1, 50)
            bot.sendMessage(
                msg.chat.id, "Your balance: " + data[msg.from!!.id]!!.balance.toString(), markup = ReplyKeyboardMarkup(
                    listOf(
                        listOf(KeyboardButton("/menu"))
                    )
                )
            )
        }
    }

    bot.onCommand("/100") {msg, _ ->
        if (data[msg.from!!.id]!!.balance < 100)
            bot.sendMessage(msg.chat.id, "Your balance is to low", markup = ReplyKeyboardMarkup(listOf(
                listOf(KeyboardButton("/menu")))))
        else {
            data[msg.from!!.id]!!.balance += run(data[msg.from!!.id]!!.game, null, 1, 100)
            bot.sendMessage(
                msg.chat.id, "Your balance: " + data[msg.from!!.id]!!.balance.toString(), markup = ReplyKeyboardMarkup(
                    listOf(
                        listOf(KeyboardButton("/menu"))
                    )
                )
            )
        }
    }

    bot.onCommand("/250") {msg, _ ->
        if (data[msg.from!!.id]!!.balance < 250)
            bot.sendMessage(msg.chat.id, "Your balance is to low", markup = ReplyKeyboardMarkup(listOf(
                listOf(KeyboardButton("/menu")))))
        else {
            data[msg.from!!.id]!!.balance += run(data[msg.from!!.id]!!.game, null, 1, 250)
            bot.sendMessage(
                msg.chat.id, "Your balance: " + data[msg.from!!.id]!!.balance.toString(), markup = ReplyKeyboardMarkup(
                    listOf(
                        listOf(KeyboardButton("/menu"))
                    )
                )
            )
        }
    }

    bot.onCommand("/500") {msg, _ ->
        if (data[msg.from!!.id]!!.balance < 500)
            bot.sendMessage(msg.chat.id, "Your balance is to low", markup = ReplyKeyboardMarkup(listOf(
                listOf(KeyboardButton("/menu")))))
        else {
            data[msg.from!!.id]!!.balance += run(data[msg.from!!.id]!!.game, null, 1, 500)
            bot.sendMessage(
                msg.chat.id, "Your balance: " + data[msg.from!!.id]!!.balance.toString(), markup = ReplyKeyboardMarkup(
                    listOf(
                        listOf(KeyboardButton("/menu"))
                    )
                )
            )
        }
    }

    bot.onCommand("/1000") {msg, _ ->
        if (data[msg.from!!.id]!!.balance < 1000)
            bot.sendMessage(msg.chat.id, "Your balance is to low", markup = ReplyKeyboardMarkup(listOf(
                listOf(KeyboardButton("/menu")))))
        else {
            data[msg.from!!.id]!!.balance += run(data[msg.from!!.id]!!.game, null, 1, 1000)
            bot.sendMessage(
                msg.chat.id, "Your balance: " + data[msg.from!!.id]!!.balance.toString(), markup = ReplyKeyboardMarkup(
                    listOf(
                        listOf(KeyboardButton("/menu"))
                    )
                )
            )
        }
    }

    bot.onCommand("/2500") {msg, _ ->
        if (data[msg.from!!.id]!!.balance < 2500)
            bot.sendMessage(msg.chat.id, "Your balance is to low", markup = ReplyKeyboardMarkup(listOf(
                listOf(KeyboardButton("/menu")))))
        else {
            data[msg.from!!.id]!!.balance += run(data[msg.from!!.id]!!.game, null, 1, 2500)
            bot.sendMessage(
                msg.chat.id, "Your balance: " + data[msg.from!!.id]!!.balance.toString(), markup = ReplyKeyboardMarkup(
                    listOf(
                        listOf(KeyboardButton("/menu"))
                    )
                )
            )
        }
    }

    bot.onCommand("/5000") {msg, _ ->
        if (data[msg.from!!.id]!!.balance < 5000)
            bot.sendMessage(msg.chat.id, "Your balance is to low", markup = ReplyKeyboardMarkup(listOf(
                listOf(KeyboardButton("/menu")))))
        else {
            data[msg.from!!.id]!!.balance += run(data[msg.from!!.id]!!.game, null, 1, 5000)
            bot.sendMessage(
                msg.chat.id, "Your balance: " + data[msg.from!!.id]!!.balance.toString(), markup = ReplyKeyboardMarkup(
                    listOf(
                        listOf(KeyboardButton("/menu"))
                    )
                )
            )
        }
    }

    bot.start()
}
