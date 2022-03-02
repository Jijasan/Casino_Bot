package org.github.KS2003.CasinoBot.games

import com.github.KS2003.telegramAPI.Bot
import org.github.KS2003.CasinoBot.OnlineGame
import org.github.KS2003.CasinoBot.menuMarkup

class Dice(val bot: Bot): OnlineGame(bot) {
    override val title = "Dice"
    override val maximumPlayer = 2
    var result = mutableListOf<Int>()

    override fun init() {
        result = MutableList(players.size, { _ -> 0 })
    }

    override fun turn(): Boolean {
        for (i in 0..result.lastIndex)
            result[i] = (Math.random() * 6).toInt() + 1
        return false
    }

    override fun finish() {
        players.forEachIndexed { index, (player, id) ->
            if (result[index] == result.maxOrNull()) {
                val delta = (bet * players.size) / result.count { it == result.maxOrNull()} - bet
                player.balance += delta
                bot.sendMessage(id, "You won $delta₽", reply_markup = menuMarkup())
            }
            else {
                val delta = -bet
                player.balance += delta
                bot.sendMessage(id, "You lose $delta₽", reply_markup = menuMarkup())
            }
        }
    }
}
