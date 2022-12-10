package blackjack

import blackjack.domain.Person
import blackjack.domain.util.Parser
import blackjack.view.InputView
import blackjack.view.OutputView

class PockerController {
    fun execute() {
        val nameList = Parser.parse(InputView.readName())
        val people = nameList.map { Person(it) }
        val pockerMachine = PockerMachine()

        people.forEach { person ->
            pockerMachine.initialize(person)
        }
        OutputView.printInitialState(people)

        people.forEach { person ->
            pockerMachine.addCard(retryOrNot(), person)
            OutputView.printCardState(person)
        }
        OutputView.printResult(people)
    }

    private fun retryOrNot() = { person: Person -> InputView.readRetry(person.name) == RETRY }

    companion object {
        private const val RETRY = "y"
    }
}
