package org.github.KS2003.CasinoBot

class Player (var balance: Int, var game: Game, var name: String) : Comparable<Player> {

    override fun toString() = balance.toString() + " " + game.toString() + " " + name

    override fun compareTo(other: Player): Int {
        if (balance > other.balance)
            return -1
        if (balance == other.balance)
            return 0
        return 1
    }

}