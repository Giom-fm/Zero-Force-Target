#!/bin/bash

benchmarks=(
    "alu4"
    "apex2"
    "apex4"
    "bigkey"
    "clma"
    "des"
    "diffeq"
    "dsip"
    "elliptic"
    "ex5p"
    "ex1010"
    "frisc"
    "misex3"
    "pdc"
    "s298"
    "s38417"
    "s38584"
    "seq"
    "spla"
    "tseng"
)

for benchmark in ${benchmarks[@]}; do
    bin/vpr Benchmarks/net/${benchmark}.net Benchmarks/arch/4lut_sanitized.arch out/place/${benchmark}.place  out/route/${benchmark}.route  -route_only -nodisp > out/results/${benchmark}.result
done
