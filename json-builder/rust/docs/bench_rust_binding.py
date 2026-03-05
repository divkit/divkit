"""Benchmark divkit_rs (Rust Python bindings) performance."""

import time
import json
import statistics

import divkit_rs as dk


def build_simple_text():
    t = dk.DivText(text="Hello from divkit_rs")
    return t.dict()


def build_slider():
    slider = dk.DivData(
        "sample_card",
        [dk.DivDataState(
            0,
            dk.DivSlider(
                width=dk.DivMatchParentSize(),
                max_value=10,
                min_value=1,
                thumb_style=dk.DivShapeDrawable(
                    color="#00b300",
                    stroke=dk.DivStroke(color="#ffffff", width=3),
                    shape=dk.DivRoundedRectangleShape(
                        item_width=dk.DivFixedSize(value=32),
                        item_height=dk.DivFixedSize(value=32),
                        corner_radius=dk.DivFixedSize(value=100),
                    ),
                ),
                track_active_style=dk.DivShapeDrawable(
                    color="#00b300",
                    shape=dk.DivRoundedRectangleShape(
                        item_height=dk.DivFixedSize(value=6),
                    ),
                ),
                track_inactive_style=dk.DivShapeDrawable(
                    color="#20000000",
                    shape=dk.DivRoundedRectangleShape(
                        item_height=dk.DivFixedSize(value=6),
                    ),
                ),
            ),
        )],
    )
    return slider.dict()


def build_gallery_n(n):
    gallery = dk.DivGallery(
        items=[dk.DivText(text=f"Item {i}") for i in range(n)]
    )
    return gallery.dict()


def build_nested_containers(depth):
    inner = dk.DivText(text="leaf")
    for i in range(depth):
        inner = dk.DivContainer(
            items=[inner],
            width=dk.DivWrapContentSize(),
            paddings=dk.DivEdgeInsets(left=4, right=4, top=4, bottom=4),
            border=dk.DivBorder(corner_radius=8),
            background=[dk.DivSolidBackground(color="#f0f0f0")],
        )
    return inner.dict()


def build_complex_layout():
    items = []
    for i in range(20):
        items.append(
            dk.DivContainer(
                width=dk.DivWrapContentSize(),
                orientation=dk.DivContainerOrientation.HORIZONTAL,
                content_alignment_vertical=dk.DivAlignmentVertical.CENTER,
                paddings=dk.DivEdgeInsets(left=12, right=12, top=10, bottom=10),
                border=dk.DivBorder(corner_radius=12),
                background=[dk.DivSolidBackground(color="#f0f0f0")],
                items=[
                    dk.DivImage(
                        width=dk.DivFixedSize(value=20),
                        height=dk.DivFixedSize(value=20),
                        margins=dk.DivEdgeInsets(right=6),
                        image_url=f"https://example.com/icon_{i}.png",
                    ),
                    dk.DivText(
                        width=dk.DivWrapContentSize(),
                        max_lines=1,
                        text=f"Category {i}",
                    ),
                ],
            )
        )
    gallery = dk.DivGallery(items=items)
    data = dk.DivData("complex_card", [dk.DivDataState(0, gallery)])
    return data.dict()


def build_schema():
    return dk.DivText().schema()


def bench_dump_expr():
    from divkit_rs.core.fields import Expr
    from divkit_rs._native import compat_dump

    expr = Expr("@{my_var}")
    return compat_dump(expr)


def bench_dump_nested():
    from divkit_rs._native import compat_dump

    container = dk.DivContainer(
        items=[dk.DivText(text=f"Item {i}") for i in range(10)],
        paddings=dk.DivEdgeInsets(left=4, right=4, top=4, bottom=4),
    )
    return compat_dump(container)


def bench(name, func, iterations=1000, warmup=100):
    for _ in range(warmup):
        func()

    times = []
    for _ in range(iterations):
        start = time.perf_counter_ns()
        func()
        elapsed = time.perf_counter_ns() - start
        times.append(elapsed)

    avg_ns = statistics.mean(times)
    med_ns = statistics.median(times)
    min_ns = min(times)
    p95_ns = sorted(times)[int(len(times) * 0.95)]

    print(f"{name}:")
    print(f"  avg: {avg_ns/1000:.1f} us | med: {med_ns/1000:.1f} us | min: {min_ns/1000:.1f} us | p95: {p95_ns/1000:.1f} us")
    return avg_ns


def main():
    print("=" * 60)
    print("Rust Python Bindings (divkit_rs) Benchmark")
    print("=" * 60)
    print()

    results = {}

    results["simple_text"] = bench("1. Simple DivText", build_simple_text)
    results["slider"] = bench("2. Nested Slider", build_slider)
    results["gallery_100"] = bench("3. Gallery 100 items", lambda: build_gallery_n(100), iterations=500)
    results["gallery_1000"] = bench("4. Gallery 1000 items", lambda: build_gallery_n(1000), iterations=100)
    results["nested_20"] = bench("5. Nested containers (depth=20)", lambda: build_nested_containers(20))
    results["nested_50"] = bench("6. Nested containers (depth=50)", lambda: build_nested_containers(50), iterations=500)
    results["complex_layout"] = bench("7. Complex layout (20 cards)", build_complex_layout, iterations=500)
    results["schema"] = bench("8. Schema generation", build_schema)

    data = build_complex_layout()
    results["json_serialize"] = bench("9. json.dumps(complex)", lambda: json.dumps(data))
    results["dump_expr"] = bench("10. compat_dump(Expr)", bench_dump_expr)
    results["dump_nested"] = bench("11. compat_dump(nested)", bench_dump_nested, iterations=500)

    print()
    print("--- Summary (avg us) ---")
    for k, v in results.items():
        print(f"  {k}: {v/1000:.1f} us")


if __name__ == "__main__":
    main()
