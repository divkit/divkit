"""Hotspot microbenchmarks for divkit_rs pydivkit-compat paths.

Baseline capture protocol:
1. Build/install current extension:
   source .venv/bin/activate && maturin develop --release
2. Run and save baseline:
   python docs/bench_hotspots.py --iterations 350 --warmup 100 > /tmp/hotspots_before.txt
3. Apply changes and rebuild extension.
4. Re-run with the same flags and compare metric medians.
"""

from __future__ import annotations

import argparse
import statistics
import time

import divkit_rs as dk
from divkit_rs import BaseDiv, DivContainer, DivState, DivStateState, DivTabs, DivTabsItem, DivText
from divkit_rs.core import Field, Ref


class BenchLeaf(DivText):
    font_size = 14


class BenchHeader(DivContainer):
    title: str = Field()
    items = [BenchLeaf(text=Ref(title))]


class BenchNestedCard(DivContainer):
    items = [BenchLeaf(text="nested")]


class BenchTabsTemplate(DivTabs):
    items = [
        DivTabsItem(
            title="tab",
            div=DivState(
                states=(
                    DivStateState(state_id="default", div=BenchNestedCard()),
                    DivStateState(state_id="alt", div=DivText(text="fallback")),
                )
            ),
        )
    ]


class BenchVarData(BaseDiv):
    variables: list[dict[str, object]] | None = Field(default=None)
    variable_triggers: list[dict[str, object]] | None = Field(default=None)
    timers: list[dict[str, object]] | None = Field(default=None)


def _bench_metric(name: str, fn, iterations: int, warmup: int) -> tuple[float, float]:
    for _ in range(warmup):
        fn()

    samples_us: list[float] = []
    for _ in range(iterations):
        started = time.perf_counter_ns()
        fn()
        elapsed_ns = time.perf_counter_ns() - started
        samples_us.append(elapsed_ns / 1000.0)

    avg_us = statistics.mean(samples_us)
    median_us = statistics.median(samples_us)
    print(f"{name}: avg_us={avg_us:.2f} median_us={median_us:.2f}")
    return avg_us, median_us


def dict_fresh() -> dict[str, object]:
    return DivContainer(
        items=[
            DivText(text="headline", max_lines=1),
            DivContainer(items=[DivText(text=f"row-{idx}") for idx in range(6)]),
        ],
        orientation=dk.DivContainerOrientation.VERTICAL,
    ).dict()


def related_templates_fresh() -> set[type[dk.PyDivEntity]]:
    root = DivContainer(
        items=[
            BenchHeader(title="hello"),
            {"type": BenchTabsTemplate.template_name},
            DivText(text="tail"),
        ]
    )
    return root.related_templates()


def make_card_fresh() -> dict[str, object]:
    return dk.make_card("bench_card", DivText(text="first"), DivText(text="second")).dict()


def make_card_with_vars() -> dict[str, object]:
    var_data = BenchVarData(
        variables=[{"name": "x", "type": "string", "value": "1"}],
        variable_triggers=[{"actions": [{"log_id": "set_x"}]}],
        timers=[{"id": "t0"}],
    )
    return dk.make_card(
        "bench_card",
        divs=[DivText(text="first"), DivText(text="second")],
        variables=var_data,
    ).dict()


def make_div_fresh() -> dict[str, object]:
    root = DivContainer(items=[BenchHeader(title="hello"), BenchTabsTemplate()])
    return dk.make_div(root)


def main() -> None:
    parser = argparse.ArgumentParser(description="divkit_rs hotspot microbenchmarks")
    parser.add_argument("--iterations", type=int, default=350)
    parser.add_argument("--warmup", type=int, default=100)
    args = parser.parse_args()

    metrics = [
        ("dict_fresh", dict_fresh),
        ("related_templates_fresh", related_templates_fresh),
        ("make_card_fresh", make_card_fresh),
        ("make_card_with_vars", make_card_with_vars),
        ("make_div_fresh", make_div_fresh),
    ]

    for name, fn in metrics:
        _bench_metric(name, fn, iterations=args.iterations, warmup=args.warmup)


if __name__ == "__main__":
    main()
