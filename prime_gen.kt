import java.util.concurrent.atomic.AtomicLong
import kotlin.math.pow
import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.random.nextULong

fun mulMod(a: ULong, b: ULong, m: ULong): ULong {
    var res = 0uL
    var x = a % m
    var y = b
    while (y > 0u) {
        if (y % 2u == 1u) {
            res = if (m - res > x) res + x else res + x - m
        }
        x = if (m - x > x) x + x else x + x - m
        y /= 2u
    }
    return res
}

fun powMod(base: ULong, exp: ULong, mod: ULong): ULong {
    var res = 1uL
    var b = base % mod
    var e = exp
    while (e > 0u) {
        if (e % 2u == 1u) res = mulMod(res, b, mod)
        b = mulMod(b, b, mod)
        e /= 2u
    }
    return res
}

fun isPrime(n: ULong): Boolean {
    if (n < 2u) return false
    if (n == 2u || n == 3u) return true
    if (n % 2u == 0u || n % 3u == 0u) return false

    val smallPrimes = ulongArrayOf(5u, 7u, 11u, 13u, 17u, 19u, 23u, 29u, 31u, 37u)
    for (p in smallPrimes) {
        if (n == p) return true
        if (n % p == 0u) return false
    }

    var d = n - 1u
    var s = 0
    while (d % 2u == 0u) {
        d /= 2u
        s++
    }

    val bases = ulongArrayOf(2u, 3u, 5u, 7u, 11u, 13u, 17u, 19u, 23u, 29u, 31u, 37u)
    for (a in bases) {
        if (n <= a) break
        var x = powMod(a, d, n)
        if (x == 1uL || x == n - 1uL) continue
        
        var composite = true
        for (r in 1 until s) {
            x = mulMod(x, x, n)
            if (x == n - 1uL) {
                composite = false
                break
            }
        }
        if (composite) return false
    }
    return true
}

fun main(args: Array<String>) = runBlocking {
    if (args.isEmpty()) return@runBlocking
    
    val length = args[0].toInt().coerceIn(1, 19)
    val minVal = 10.0.pow(length - 1).toLong().toULong()
    val maxVal = if (length == 19) ULong.MAX_VALUE else 10.0.pow(length).toLong().toULong() - 1u
    
    val result = AtomicLong(0)
    val parentJob = Job()
    val coreCount = Runtime.getRuntime().availableProcessors()
    
    val workers = List(coreCount) {
        launch(Dispatchers.Default + parentJob) {
            while (isActive) {
                val candidate = Random.nextULong(minVal..maxVal) or 1u
                if (isPrime(candidate)) {
                    if (result.compareAndSet(0, candidate.toLong())) {
                        parentJob.cancel()
                    }
                }
            }
        }
    }

    workers.forEach { it.join() }
    
    val primeFound = result.get().toULong()
    if (primeFound != 0uL) {
        println(primeFound)
    }
}
