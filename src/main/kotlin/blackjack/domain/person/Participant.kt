package blackjack.domain.person

import blackjack.domain.card.Cards
import blackjack.domain.enums.WinOrLose

class Participant(
    name: String,
    money: Long,
    cards: Cards = Cards()
) : Player(name, money, cards) {
    fun getGameResult(dealer: Dealer): WinOrLose {
        if (dealer.isBurst() || this.getScore() in dealer.getScore()..MAXIMUM_SCORE) {
            return WinOrLose.WIN
        }
        return WinOrLose.LOSE
    }

    fun getBlackJackMoney(): Long {
        return (money * BLACKJACK_DIVIDEND_RATE).toLong()
    }

    fun getWinMoney(): Long {
        return money
    }

    fun getLoseMoney(): Long {
        return (money * LOSE_DIVIDEND_RATE).toLong()
    }

    companion object {
        private const val MAXIMUM_SCORE = 21
        private const val BLACKJACK_DIVIDEND_RATE = 1.5
        private const val LOSE_DIVIDEND_RATE = -1.0
    }
}
