# Advent of Code 2022 in Kotlin

(With a handy template)

## Description
I decided to stay plain JVM, with all the code for whole month prepared and ready to be modified day by day. 
There’s main `main()` in project, that recreates remaining daily `.kt` files if at any point I’ll decide to make change to the daily template and also runs the solutions for the days that have already downloaded puzzle inputs.

It also measures time.

In each day, there's their own `main()` that can be used for working on given day only. 
It's also possible to extend them with calls like `part1("Example data")` to test solution on exaples in puzzles.

## Setup

- create `local/cookie` file with content like
```properties
session=5361......bb4c6
```
(grab the cookie from DevTools after logging to AoC)

- each day at 6AM (CET) run
```bash
DAY=`date "+%d"` ; curl -v -b `cat local/cookie` https://adventofcode.com/2022/day/${DAY}/input -o local/day${DAY}_input.txt
```
