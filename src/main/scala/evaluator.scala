

object Evaluator {

	def score(cards: String) = {
		require(cards != null)
		val (acePresent, handCount) = handEval(cards)
		if (!acePresent || handCount > 11) handCount else handCount + 10 ensuring ( _ <= 31 )
	}


	def context(dealerCard: Char, playerCards: String, nextSplitPosition: Int, doubleAfterSplit: Boolean, 
			maxHands: Int) = {

		require(playerCards != null)
		require(nextSplitPosition >= 2)
		require(maxHands > 0)

		def playerScoreStr() = {

			def stringifiedHandCount(handCount: Int) = if (handCount > 9) "" + handCount else "0" + handCount	

			val (acePresent, handCount) = handEval(playerCards)
			if (!acePresent || handCount > 11) stringifiedHandCount(handCount) + "H"
			else stringifiedHandCount(handCount + 10) + "S"

		}

		val doublePossible = if ((!(nextSplitPosition > 2) || doubleAfterSplit) 
				&& playerCards.size == 2) "Y" else "N"
		val splitPossible = if (playerCards.size == 2 && playerCards(0) == playerCards(1) 
				&& nextSplitPosition <= maxHands) "Y" else "N"
		playerScoreStr + dealerCard + doublePossible + splitPossible ensuring ( _.size == 6 )
	}


	def isSoft(cards: String) = {
		require(cards != null)
		val (acePresent, handCount) = handEval(cards)
		!(!acePresent || handCount > 11)
	}


	def isDoubled(actions: String) = {
		require(actions != null)
		actions.exists( _ == 'D' )
	}


	private def handEval(cards: String) = {
		require(cards != null)
		val acePresent = cards.exists(_ == 'A')
		val handCount = cards.foldLeft(0)(_ + cardCount(_))
		(acePresent, handCount)
	}


	private def cardCount(card: Char) = {
		card match {
			case 'T' => 10
			case 'A' => 1
			case '9' => 9
			case '8' => 8
			case '7' => 7
			case '6' => 6
			case '5' => 5
			case '4' => 4
			case '3' => 3
			case '2' => 2
		}
	}



}