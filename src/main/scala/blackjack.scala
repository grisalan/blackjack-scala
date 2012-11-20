import com.codahale.logula.Logging
import org.apache.log4j.Level

object Blackjack {
	def main(args: Array[String]) {
		Logging.configure { log =>
		  //log.registerWithJMX = true

		  log.level = Level.DEBUG
		  //log.loggers("com.myproject.weebits") = Level.OFF

		  log.console.enabled = true
		  log.console.threshold = Level.WARN

		  log.file.enabled = true
		  log.file.filename = "blackjack.log"
		  //log.file.maxSize = 10 * 1024 // KB
		  //log.file.retainedFiles = 5 // keep five old logs around

		  // syslog integration is always via a network socket
		  //log.syslog.enabled = true
		  //log.syslog.host = "syslog-001.internal.example.com"
		  //log.syslog.facility = "local3"
		}
		Runner.run
	}
}

/* As Application Trait - only useful for simple non multi-threaded
scripts that don't need access to command line arguments

object Blackjack extends Application {
	Runner.run
}

*/
