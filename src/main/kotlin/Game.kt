package org.github.KS2003.CasinoBot

import kotlin.random.Random

class Game(
    val type: Type,
    var numbers: MutableList<Pair<Pair<Int, Int>, List<Int>>>? = null,
    val bet: Int? = null,
    var win: Int? = null
) {
    enum class Type {
        COIN,
        ROULETTE,
        NON
    }

    override fun toString() = type.toString()
}

fun run(game: Game): Int {
    when (game.type) {
        Game.Type.COIN -> {
            if (Random.nextBoolean())
                return@run game.bet!!
            else
                return@run -game.bet!!
        }
        Game.Type.ROULETTE -> {
            val result = Random.nextInt(0, 38)
            game.win = result
            var win = 0
            game.numbers!!.forEach { (bet, fields) ->
                if (result in fields)
                    win += bet.first * bet.second
                else
                    win -= bet.second
            }
            return win
        }
    }
    return 0
}