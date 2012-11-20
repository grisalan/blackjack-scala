import scala.util.matching.Regex


class PlayerStrategy(strategies : List[(Regex, String)]) {

	require(strategies != null)

	def act(context: String) = {
		def getAction(strategies : List[(Regex, String)], context: String): String = {
			val (regexp, action) = strategies.head
			regexp.findFirstIn(context) match {
				case Some(_) => action
				case None => getAction(strategies.tail, context)
			}
		}
		getAction(strategies, context)
	}

}
