#include <iostream>
#include <random>
#include <vector>
#include <string>
#include <chrono>
#include <thread>
#include <cmath>
#include <atomic>

using u64 = unsigned long long;
using u128 = __int128;

u64 power(u64 base, u64 exp, u64 mod) {
    u64 res = 1;
    base %= mod;
    while (exp > 0) {
        if (exp % 2 == 1) res = (u128)res * base % mod;
        base = (u128)base * base % mod;
        exp /= 2;
    }
    return res;
}

bool miller_rabin(u64 n, int k) {
    if (n < 2) return false;
    if (n == 2 || n == 3) return true;
    if (n % 2 == 0) return false;

    u64 d = n - 1;
    int s = 0;
    while (d % 2 == 0) {
        d /= 2;
        s++;
    }

    std::mt19937_64 rng(std::random_device{}());
    std::uniform_int_distribution<u64> dist(2, n - 2);

    for (int i = 0; i < k; i++) {
        u64 a = dist(rng);
        u64 x = power(a, d, n);
        if (x == 1 || x == n - 1) continue;
        bool composite = true;
        for (int r = 1; r < s; r++) {
            x = (u128)x * x % n;
            if (x == n - 1) {
                composite = false;
                break;
            }
        }
        if (composite) return false;
    }
    return true;
}

int main(int argc, char* argv[]) {
    if (argc != 3) return 1;

    int length = std::stoi(argv[1]);
    int k = std::stoi(argv[2]);
    
    if (length > 19) length = 19; 

    u64 min_val = std::pow(10, length - 1);
    u64 max_val = (length == 19) ? 0xFFFFFFFFFFFFFFFF : std::pow(10, length) - 1;

    std::mt19937_64 rng(std::random_device{}());
    std::uniform_int_distribution<u64> dist(min_val, max_val);

    auto start_time = std::chrono::steady_clock::now();
    const int core_factor = std::thread::hardware_concurrency();
    const int timeout_sec = (core_factor > 0) ? 10 : 5; 

    while (true) {
        if (std::chrono::duration_cast<std::chrono::seconds>(
                std::chrono::steady_clock::now() - start_time).count() >= timeout_sec) {
            std::cout << "Timeout reached (" << timeout_sec << "s)" << std::endl;
            break;
        }

        u64 candidate = dist(rng) | 1; 
        if (miller_rabin(candidate, k)) {
            std::cout << candidate << std::endl;
            return 0;
        }
    }
    return 1;
}
