import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers



class EvaluatorSpec extends FlatSpec with ShouldMatchers {

	"Evaluator" should "give correct scores" in {
		Evaluator.score("A43") should be (18)
	}

	it should "throw an Exception" in {
		evaluating {
				Evaluator.score(null)
			} should produce [Exception]
	}

	it should "give correct context" in {
		Evaluator.context('T', "AT", 2, true, 4) should be ("21STYN")
	}

}

