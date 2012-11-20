
class Shoe(cards: String) {

	require(cards != null)

	def numberOfCards = cards.size

	def nextCard(shoeLocation: Int) = {
		cards(shoeLocation - 1)
	}

	def shuffle() = new Shoe(Shuffler.fisherYatesShuffle(cards.toArray).mkString(""))

}


object Shuffler {

	def fisherYatesShuffle[T](xs: Array[T]) = {
		for (i <- xs.indices.reverse)
			swap(xs, i, rand.nextInt(i + 1))
		xs
	}

	private val rand = new scala.util.Random();

	private def swap[T](xs: Array[T], i: Int, j: Int) = {
		val t = xs(i)
		xs(i) = xs(j)
		xs(j) = t
	}

}
