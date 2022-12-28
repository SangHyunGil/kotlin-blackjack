package blackjack.domain

import blackjack.domain.card.CardDeck
import blackjack.domain.dto.BettingMoneyResult
import blackjack.domain.dto.GameResult
import blackjack.domain.dto.ParticipantMoneyResult
import blackjack.domain.dto.ParticipantResult
import blackjack.domain.money.BlackJackMoney
import blackjack.domain.money.LoseMoney
import blackjack.domain.money.WinMoney
import blackjack.domain.person.Dealer
import blackjack.domain.person.Participant
import blackjack.domain.person.Player

class BlackJackMachine(
    private val cardDeck: CardDeck = CardDeck(),
    private val dealer: Dealer,
    private val players: List<Player>
) {
    fun initialize() {
        List(BASIC_CARD_COUNT) {
            players.map { player -> player.addCard(dealer.pickCard(cardDeck)) }
        }
    }

    fun addCard(
        retryFunc: (person: Player) -> Boolean,
        printFunc: (person: Player) -> Unit
    ) {
        players.forEach { player ->
            when (player) {
                is Dealer -> player.pickIfRequired(cardDeck)
                is Participant -> pickOrNot(player, retryFunc, printFunc)
            }
        }
    }

    fun getBettingResult(): BettingMoneyResult {
        val participants = players.filterIsInstance<Participant>()
        return BettingMoneyResult(
            dealerName = dealer.name,
            participantMoneyResult = participants.map { participant -> calculateBetting(participant) }
        )
    }

    fun getGameResult(): GameResult {
        return GameResult(
            dealerName = dealer.name,
            participantResult = players.filterIsInstance<Participant>().map { ParticipantResult(it.name, it.getGameResult(dealer)) }
        )
    }

    private tailrec fun pickOrNot(
        player: Player,
        retryFunc: (player: Player) -> Boolean,
        printFunc: (player: Player) -> Unit
    ) {
        // 최고 점수보다 많을시 종료
        if (player.isBurst()) {
            return
        }

        // 사용자가 종료할시 종료
        if (retryFunc(player).not()) {
            printFunc(player)
            return
        }

        val card = dealer.pickCard(cardDeck)
        player.addCard(card)
        printFunc(player)

        pickOrNot(player, retryFunc, printFunc)
    }

    private fun calculateBetting(participant: Participant): ParticipantMoneyResult {
        if (dealer.isSameWithMaximumScore() && participant.isSameWithMaximumScore()) {
            return ParticipantMoneyResult(participant.name, WinMoney(participant.money).get())
        }

        if (participant.countCards() == BASIC_CARD_COUNT && participant.isSameWithMaximumScore()) {
            return ParticipantMoneyResult(participant.name, BlackJackMoney(participant.money).get())
        }

        if (dealer.isBurst()) {
            return ParticipantMoneyResult(participant.name, WinMoney(participant.money).get())
        }

        if (participant.getScore() in dealer.getScore()..MAXIMUM_SCORE) {
            return ParticipantMoneyResult(participant.name, WinMoney(participant.money).get())
        }

        return ParticipantMoneyResult(participant.name, LoseMoney(participant.money).get())
    }

    companion object {
        private const val BASIC_CARD_COUNT = 2
        private const val MAXIMUM_SCORE = 21
    }
}
