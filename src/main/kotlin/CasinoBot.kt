import com.elbekD.bot.Bot
import com.elbekD.bot.types.InlineKeyboardButton
import com.elbekD.bot.types.InlineKeyboardMarkup
import com.elbekD.bot.types.KeyboardButton
import com.elbekD.bot.types.ReplyKeyboardMarkup
import kotlin.random.Random


fun main() {
    val balance = mutableMapOf<Int, Int>()
    val token = System.getenv("TOKEN")
    println(token)
    val username = "<BOT USERNAME>"
    val bot = Bot.createPolling(username, token) {
        // below is optional parameters
        // limit = 50
        // timeout = 30
        // allowedUpdates = listOf(AllowedUpdate.Message)
        // removeWebhookAutomatically = true
        // period = 1000
    }

    bot.onCommand("/start") { msg, _ ->
        bot.sendMessage(msg.chat.id, "")
    }

    bot.onCommand("/echo") { msg, opts ->
        bot.sendMessage(msg.chat.id, "${msg.text} ${opts ?: ""}")
    }

    bot.onCommand("/balance") { msg, _ ->
        if (msg.from != null) {
            if (balance[msg.from?.id] == null)
                balance.set(msg.from!!.id, 1000)
            bot.sendMessage(msg.chat.id, "Your balance: " + balance[msg.from?.id].toString())
        }
    }

    bot.onCommand("/coin") coin@{msg, _ ->
        if (msg.text != null && msg.from != null && msg.text!!.split(" ").size > 1) {
            if (balance[msg.from?.id] == null)
                balance.set(msg.from!!.id, 1000)
            val bet = msg.text!!.split(" ")[1].toInt()
            if (bet > balance[msg.from?.id]!!) {
                bot.sendMessage(msg.chat.id, "Your balance's to low")
                return@coin
            }
            val result = Random.nextInt(0, 2)
            val user = msg.from!!
            if (result == 1)
                balance[user.id] = balance[user.id]!!.plus(bet)
            else
                balance[user.id] = balance[user.id]!!.minus(bet)
            bot.sendMessage(msg.chat.id, "Your balance: " + balance[msg.from?.id].toString())
        }
    }

    bot.onCommand("/help") { msg, _ ->
        bot.sendMessage(msg.chat.id, "/balance: print your balance\n\n" +
                                          "/coin BET: You toss a coin. Heads - you win BET; TAILS - you lose BET\n\n" +
                                          "/roulette CEILS BET: You play roulette, where you bet CEILS: \n" +
                                          "  even - all even numbers\n" +
                                          "  odd - all odd numbers\n" +
                                          "  1-18 - all numbers from 1 to 18\n" +
                                          "  19-36 - all numbers from 19 to 36\n" +
                                          "  NUM - single numbers")
    }

    bot.onCommand("/roulette") roulette@{ msg, _ ->
        val button = InlineKeyboardButton("FDSFSDF")
        if (msg.text == null || msg.from == null || msg.text!!.split(" ").size <= 2)
            return@roulette
        print(button)
        bot.onCallbackQuery(button.callback_data!!, {})
        val BET = msg.text!!.split(" ")[1]
        val bet = msg.text!!.split(" ")[2].toInt()
        val user = msg.from!!.id
        if (balance[user] == null)
            balance.set(user, 1000)
        val ceils: IntArray
        val multi: Int
        when (BET) {
            "even" -> {
                ceils = IntArray(18) {i -> 2 * i}
                multi = 2
            }
            "odd" -> {
                ceils = IntArray(18) {i -> 2 * i - 1}
                multi = 2
            }
            "1-18" -> {
                ceils = IntArray(18) {i -> i}
                multi = 2
            }
            "19-36" -> {
                ceils = IntArray(18) {i -> i + 18}
                multi = 2
            }
            else   -> {
                ceils = intArrayOf(BET.toInt())
                multi = 35
            }
        }
        val result = Random.nextInt(0, 37)
        if (result in ceils)
            balance.set(user, balance[user]?.plus(multi * bet)!!)
        else
            balance.set(user, balance[user]?.minus(bet)!!)
        bot.sendMessage(msg.chat.id, "Your balance: " + balance[msg.from?.id].toString())
    }

    bot.start()
}
