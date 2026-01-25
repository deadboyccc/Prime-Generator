# Prime Generator (Miller-Rabin)

A compact C++ tool to generate random prime numbers of a specified digit length.

## Compilation
    g++ -O3 -std=c++17 prime_gen.cpp -o prime_gen

## Usage
    ./prime_gen <length> <probability_iterations>

    * length: Number of digits (max 19 for 64-bit unsigned integers).
    * probability_iterations: Number of Miller-Rabin tests (e.g., 5-10 for casual, 20+ for cryptographic certainty).

## Features
* Uses __int128 for overflow-safe modular arithmetic.
* Validates primes using the probabilistic Miller-Rabin test.
* Auto-terminates based on a timeout derived from system hardware concurrency logic.
* Efficient bitwise optimizations (checks odd numbers only).
