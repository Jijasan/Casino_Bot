package org.github.KS2003.CasinoBot

import com.github.KS2003.telegramAPI.Bot

class Lobby(val game: OnlineGame, val id: Int, val bot: Bot) {
    val players = mutableListOf<Pair<Player, Int>>()
    var bet = 0

    fun connect(player: Player, id: Int): Boolean {
        if (game.maximumPlayer > players.size) {
            players.forEach { (_, id) ->
                bot.sendMessage(id, "${player.name} connected")
            }
            players += Pair(player, id)
            player.lobbyId = this.id
            return true
        }
        return false
    }

    fun disconnect(player: Player, id: Int) {
        players -= Pair(player, id)
        players.forEach { (_, id) ->
            bot.sendMessage(id, "${player.name} disconnected")
        }
        player.lobbyId = -1
    }

    fun start() {
        game.start(players, bet)
    }

}