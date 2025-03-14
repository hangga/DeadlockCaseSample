package id.web.hangga

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.lang.management.ManagementFactory
import kotlin.concurrent.thread

class DeadlockUnitTest {
    @Test
    fun `example of deadlock`() {
        val account1 = Account("Hangga", 1000)
        val account2 = Account("John", 1000)
        val account3 = Account("Alice", 2000)

        // Transfer from account1 to account2
        thread {
            account1.transfer(account2, 100)
        }.join(10) // as a simulation of the time required

        // Transfer from account2 to account1
        thread {
            account2.transfer(account1, 200)
        }.join(20)

        // Transfer from account3 to account1
        thread {
            account3.transfer(account1, 1000)
        }.join(500)

    }

    val list = ArrayList<Int>()

    @Test
    fun `example thread-unsafe using HashMap`() {
        val map = HashMap<Int, Int>()

        val threads = List(10) { index ->
            thread {
                for (i in 0 until 1000) {
                    map[i] = index
                }
            }
        }

        threads.forEach { it.join() }
    }

    @AfterEach
    fun detectDeadlock() {
        val threadMXBean = ManagementFactory.getThreadMXBean()
        threadMXBean.findDeadlockedThreads()?.forEach { id ->
            val threadInfo = threadMXBean.getThreadInfo(id, Int.MAX_VALUE)
            println("WARNING: Deadlock detected: [id:$id, name:${threadInfo.threadName}, owner:${threadInfo.lockOwnerName}]")
            val stackTrace = threadInfo.stackTrace.joinToString("\n") { it.toString() }
            println(stackTrace)
        }
    }
}