# Cuboid

Implementation of Problem 212 Project Euler in Zig, as a learning experience. 
Language: https://ziglang.org/
Problem: https://projecteuler.net/problem=212

Preferred build:
```sh
zig build -Drelease-fast
time ./zig-out/bin/cuboid
```

Tested on Pop Os on an old 2012 desktop. 

master is at commit: `a12abc6d6c8b89a09befdcbd9019247ccc3bd641`

0.10.dev = `0.10.0-dev.3590+a12abc6d6`
| version  | zig build       | Time taken | File Size |
|----------|-----------------|------------|-----------|
| master   |                 | 1m 56s     | 677,976   |
|          | -Drelease-small | 22s        | 133,888   |
|          | -Drelease-safe  | 6.44 s     | 737,352   |
|          | -Drelease-fast  | 5.96 s     | 158,656   |
| 0.10.dev |                 | 2m         | 678,560   |
|          | -Drelease-small | 21 s       | 134,464   |
|          | -Drelease-safe  | 6.4 s      | 737,936   |
|          | -Drelease-fast  | 6.2 s      | 159,240   |
 |           |
| 0.9.1    |                 | 1m 58s     | 629264    |
|          | -Drelease-small | 21 s       | 124,248   |
|          | -Drelease-safe  | 7.1 s      | 639,688   |
|          | -Drelease-fast  | 7.3 s      | 150,216   |
|          |                 |            |           |