

class Engine(ps: PlayerStrategy, hr: HouseRules) {

	require(ps != null && hr != null)

	def nextDeal(shoe: Shoe, startingShoeLocation: Int) = {

		def play(deal: (Symbol, List[String], List[String], Int, Char)): (List[String], List[String], Int) = {
			deal match {
				case ('start, Nil, Nil, 0, '\0') => {
					val playerCards = "" + shoe.nextCard(startingShoeLocation) + shoe.nextCard(startingShoeLocation + 2)
					val dealerCards = "" + shoe.nextCard(startingShoeLocation + 1) + shoe.nextCard(startingShoeLocation + 3)
					if (Evaluator.score(dealerCards) == 21)
						play('finished, playerCards :: dealerCards :: Nil, Nil, 4, dealerCards(0))
					else
						play('playerAction, playerCards :: dealerCards :: Nil, "" :: Nil, 4, dealerCards(0))
				}
				case ('playerAction, hands, actions, cardsDealt, dealerShow) => {
					val pendingHands = hands.size - actions.size - 1
					if (Evaluator.score(hands.drop(pendingHands).head) > 21) {
						play('nextHand, hands, actions, cardsDealt, dealerShow)
					}
					else {
						val context = Evaluator.context(dealerShow, hands.drop(pendingHands).head, 
								hands.size, hr.doubleAfterSplit, hr.maxHands)
						val action = ps.act(context)
						val actionSet = actions.head + action
						action match {
							case "S" => play('nextHand, hands, actionSet :: actions.tail, cardsDealt, dealerShow)
							case "D" => {
								val playerCards = hands.drop(pendingHands).head +
											shoe.nextCard(startingShoeLocation + cardsDealt)
								play('nextHand, hands.take(pendingHands) ::: (playerCards :: hands.drop(pendingHands).tail), 
											actionSet :: actions.tail, cardsDealt + 1, dealerShow)
							}
							case "P" => {
								val playerCards = "" + hands.drop(pendingHands).head(0) + 
														shoe.nextCard(startingShoeLocation + cardsDealt)
								val nextCards = "" + hands.drop(pendingHands).head(1) + 
														shoe.nextCard(startingShoeLocation + cardsDealt + 1)
								play('playerAction, (nextCards :: hands.take(pendingHands)) ::: (playerCards :: hands.drop(pendingHands).tail),
											actionSet :: actions.tail, cardsDealt + 2, dealerShow)
							}
							case "H" => {
								val playerCards = hands.drop(pendingHands).head + shoe.nextCard(startingShoeLocation + cardsDealt)
								play('playerAction, hands.take(pendingHands) ::: (playerCards :: hands.drop(pendingHands).tail),
											actionSet :: actions.tail, cardsDealt + 1, dealerShow)
							}
						}
					}
				}
				case ('nextHand, hands, actions, cardsDealt, dealerShow) => {
					if (hands.size - 1 == actions.size) play('dealerDraw, hands, actions, cardsDealt, dealerShow)
					else play('playerAction, hands, "" :: actions, cardsDealt, dealerShow)
				}
				case ('dealerDraw, hands, actions, cardsDealt, dealerShow) => {
					val dealerScore = Evaluator.score(hands.last)
					val dealerNeedsCard = dealerScore < 17 || 
								(dealerScore == 17 && !hr.standon17 && Evaluator.isSoft(hands.last))
					if (dealerNeedsCard) {
						val dealerCards = hands.last + shoe.nextCard(startingShoeLocation + cardsDealt)
						play('dealerDraw, hands.init ::: (dealerCards :: Nil), actions, cardsDealt + 1, dealerShow)
					} else {
						play('finished, hands, actions, cardsDealt, dealerShow)
					}

				}
				case ('finished, hands, actions, cardsDealt, dealerShow) => {
					(hands, actions, cardsDealt)
				}
			}
		}

		val result = play('start, Nil, Nil, 0, '\0')



		def dealResult(hands: List[String], actions: List[String],
			wagers: List[Int], payouts: List[Int]) = 
						DealData(hands, actions, wagers, payouts)


		val hands = result._1.reverse
		val actions = result._2.reverse
		val cardsDealt = result._3
		if (actions == Nil) {
			if (Evaluator.score(hands.last) == 21) {
				(dealResult(hands, actions, hr.baseWager :: Nil, hr.baseWager :: Nil), cardsDealt)
			} else {
				(dealResult(hands, actions, hr.baseWager :: Nil, 0 :: Nil), cardsDealt)
			}
		}
		else {
			if (hands.size == 2 && Evaluator.score(hands.last) == 21 && hands.last.size == 2) {
				(dealResult(hands, actions, hr.baseWager :: Nil, (hr.baseWager * 5) / 2 :: Nil), cardsDealt)
			} else {
				val dealerScore = Evaluator.score(hands.head)
				def money(hands: List[String], actions: List[String], 
									transfers: (List[Int], List[Int])): (List[Int], List[Int]) = {
					(hands, actions, transfers) match {
						case (Nil, _, accum) => accum
						case (hands, actions, accum) => {
							val playerScore = Evaluator.score(hands.head)
							val isDouble = Evaluator.isDoubled(actions.head)
							val wager = if (isDouble) hr.baseWager * 2 else hr.baseWager
							if (playerScore > 21) {
								money(hands.tail, actions.tail, (wager :: accum._1, 0 :: accum._2))
							} else if (dealerScore > 21) {
								money(hands.tail, actions.tail, (wager :: accum._1, 2 * wager :: accum._2))
							} else if (playerScore > dealerScore) {
								money(hands.tail, actions.tail, (wager :: accum._1, 2 * wager :: accum._2))
							} else if (dealerScore > playerScore) {
								money(hands.tail, actions.tail, (wager :: accum._1, 0 :: accum._2))
							} else { // dealerScore == playerScore
								money(hands.tail, actions.tail, (wager :: accum._1, wager :: accum._2))
							}
						}
					}
				}
				val outcome = money(hands.tail, actions, (Nil, Nil))
				(dealResult(hands, actions, outcome._1.reverse, outcome._2.reverse), cardsDealt)
			}
		}

	}

}