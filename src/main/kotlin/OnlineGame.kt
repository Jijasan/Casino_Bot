package org.github.KS2003.CasinoBot

import com.github.KS2003.telegramAPI.Bot

abstract class OnlineGame(bot: Bot) {
    abstract val title: String
    abstract val maximumPlayer: Int
    var players: MutableList<Pair<Player, Int>> = mutableListOf()
    var bet: Int = 0

    fun start(players: MutableList<Pair<Player, Int>>, bet: Int) {
        this.players = players
        this.bet = bet
        init()
        while(turn());
        finish()
    }

    abstract fun init()

    abstract fun turn(): Boolean

    abstract fun finish()
}