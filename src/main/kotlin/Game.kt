import kotlin.random.Random

enum class Game{
    COIN,
    ROULETTE,
    NON
}

class Field(field: Int)

fun run(game: Game, fields: List<Field>?, multi: Int, bet: Int): Int {
    when (game) {
        Game.COIN -> {
            if (Random.nextBoolean())
                return@run bet
            else
                return@run -bet
        }
        Game.ROULETTE -> {

        }
    }
    return 0
}