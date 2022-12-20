package blackjack.domain.card

data class Card(
    val cardShape: CardShape,
    val cardNumber: CardNumber,
) {
    override fun toString(): String {
        return cardNumber.description + cardShape.description
    }

    fun calculate(score: Int): Int {
        return cardNumber.calc(score)
    }

    fun isAce(): Boolean {
        return cardNumber == CardNumber.ACE
    }
}
