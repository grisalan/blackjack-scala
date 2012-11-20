
class Trial(trialName: String, engine: Engine, initialShoe: Shoe, shufflePoint: Double, numberOfDeals: Int) {

	require(trialName != null && engine != null && initialShoe != null)
	require(shufflePoint >= 0.0 && shufflePoint <= 1.0)
	require(numberOfDeals > 0)

	def run {

		def resultString(dealResults: DealData) = {
			val result = new StringBuilder
			for (i <- 0 until dealResults.hands.size) {
				result.append(dealResults.hands(i))
				if (i < dealResults.hands.size - 1) result.append(",")
			}
			result.append(";")
			for (i <- 0 until dealResults.actions.size) {
				result.append(dealResults.actions(i))
				if (i < dealResults.actions.size - 1) result.append(",")
			}
			result.append(";")
			for (i <- 0 until dealResults.wagers.size) {
				result.append(dealResults.wagers(i))
				if (i < dealResults.wagers.size - 1) result.append(",")
			}
			result.append(";")
			for (i <- 0 until dealResults.payouts.size) {
				result.append(dealResults.payouts(i))
				if (i < dealResults.payouts.size - 1) result.append(",")
			}
			result.toString
		}


		def deal(shoe: Shoe, shoeLocation: Int, dealNumber: Int): (Symbol, Int) = {
			if (dealNumber <= numberOfDeals) {
				val results = engine.nextDeal(shoe, shoeLocation)
				val dealResults = results._1
				println("" + dealNumber + ": " + resultString(dealResults))
				val cardsDealt = results._2
				val newShoeLocation = shoeLocation + cardsDealt
				if ((newShoeLocation - 0.0) / shoe.numberOfCards >= shufflePoint) {
					deal(shoe.shuffle(), 1, dealNumber + 1)
				} else {
					deal(shoe, newShoeLocation, dealNumber + 1)
				}
			} else {
				('finished, dealNumber - 1)
			}
		}

		val result = deal(initialShoe.shuffle, 1, 1)
		println("Deals: " + result._2 + "; Status: " + result._1)

	}

}