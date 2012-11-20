import scala.io.Source
import scala.util.matching.Regex
import com.codahale.logula.Logging


object Runner extends Logging {

  	def run = {

  		log.debug("Beginning")

  		// Properties
  		val playerStrategyFilename = "PlayerStrategyBasic.txt"
  		val shoeFilename = "SixDeck.txt"
  		val standon17 = true
  		val doubleAfterSplit = true
  		val shufflePoint = 0.6
  		val baseWager = 10
  		val maxHands = 4
  		val numberOfDeals = 10000

  		// Set up the player strategy
		val strategies = for { 
								line <- Source.fromFile(playerStrategyFilename).getLines()
								if line.size > 0
								tokens = line.trim.split(",").toList
								} yield (tokens.head.r, tokens.tail.head)
		val ps = new PlayerStrategy(strategies.toList)

		// Set up the shoe
		val cards = for {
							line <- Source.fromFile(shoeFilename).getLines()
							if line.size > 0
							} yield line.trim
		val initialShoe = new Shoe(cards.mkString(""))

		// Set up the house rules
		val hr = HouseRules(standon17, doubleAfterSplit, shufflePoint, baseWager, maxHands)

		// Set up the blackjack engine
		val engine = new Engine(ps, hr)

		// Run the app
		val trial = new Trial("trial001", engine, initialShoe, hr.shufflePoint, numberOfDeals)
		trial.run

		log.debug("ending")

	}

}
