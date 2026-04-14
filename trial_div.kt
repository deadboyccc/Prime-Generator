fun isPrime(n: Long): Boolean {
    if (n < 2) return false
    if (n == 2L || n == 3L) return true
    if (n % 2 == 0L || n % 3 == 0L) return false
    
    var i = 5L
    while (i * i <= n) {
        if (n % i == 0L || n % (i + 2) == 0L) return false
        i += 6
    }
    return true
}

fun generatePrimes(count: Int): List<Long> {
    return generateSequence(2L) { it + 1 }
        .filter { isPrime(it) }
        .take(count)
        .toList()
}

fun main() {
    val primes = generatePrimes(10)
    println(primes) // [2, 3, 5, 7, 11, 13, 17, 19, 23, 29]
}
