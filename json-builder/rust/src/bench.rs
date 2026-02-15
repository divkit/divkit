use std::time::Instant;

use divkit_json_builder::generated::*;
use divkit_json_builder::entity::Entity;
use divkit_json_builder::value::DivValue;
use divkit_json_builder::{DivData, DivDataState};

fn build_simple_text() -> serde_json::Value {
    let t = DivText::new().text("Hello from pydivkit");
    t.dict()
}

fn build_slider() -> serde_json::Value {
    let slider = DivData {
        log_id: "sample_card".to_string(),
        states: vec![DivDataState {
            state_id: 0,
            div: Box::new(
                DivSlider::new()
                    .width(DivMatchParentSize::new())
                    .max_value(10)
                    .min_value(1)
                    .thumb_style(
                        DivShapeDrawable::new()
                            .color("#00b300")
                            .stroke(DivStroke::new().color("#ffffff").width(3))
                            .shape(
                                DivRoundedRectangleShape::new()
                                    .item_width(DivFixedSize::new().value(32))
                                    .item_height(DivFixedSize::new().value(32))
                                    .corner_radius(DivFixedSize::new().value(100)),
                            ),
                    )
                    .track_active_style(
                        DivShapeDrawable::new().color("#00b300").shape(
                            DivRoundedRectangleShape::new()
                                .item_height(DivFixedSize::new().value(6)),
                        ),
                    )
                    .track_inactive_style(
                        DivShapeDrawable::new().color("#20000000").shape(
                            DivRoundedRectangleShape::new()
                                .item_height(DivFixedSize::new().value(6)),
                        ),
                    ),
            ),
        }],
    };
    slider.dict()
}

fn build_gallery_n(n: usize) -> serde_json::Value {
    let items: Vec<DivValue> = (0..n)
        .map(|i| DivValue::from(DivText::new().text(format!("Item {}", i))))
        .collect();
    let gallery = DivGallery::new().items(items);
    gallery.dict()
}

fn build_nested_containers(depth: usize) -> serde_json::Value {
    let mut inner: DivValue = DivValue::from(DivText::new().text("leaf"));
    for _ in 0..depth {
        inner = DivValue::from(
            DivContainer::new()
                .items(vec![inner])
                .width(DivWrapContentSize::new())
                .paddings(DivEdgeInsets::new().left(4).right(4).top(4).bottom(4))
                .border(DivBorder::new().corner_radius(8))
                .background(vec![DivValue::from(
                    DivSolidBackground::new().color("#f0f0f0"),
                )]),
        );
    }
    match inner {
        DivValue::Entity(e) => e.dict(),
        _ => unreachable!(),
    }
}

fn build_complex_layout() -> serde_json::Value {
    let items: Vec<DivValue> = (0..20)
        .map(|i| {
            DivValue::from(
                DivContainer::new()
                    .width(DivWrapContentSize::new())
                    .orientation(DivContainerOrientation::Horizontal)
                    .content_alignment_vertical(DivAlignmentVertical::Center)
                    .paddings(DivEdgeInsets::new().left(12).right(12).top(10).bottom(10))
                    .border(DivBorder::new().corner_radius(12))
                    .background(vec![DivValue::from(
                        DivSolidBackground::new().color("#f0f0f0"),
                    )])
                    .items(vec![
                        DivValue::from(
                            DivImage::new()
                                .width(DivFixedSize::new().value(20))
                                .height(DivFixedSize::new().value(20))
                                .margins(DivEdgeInsets::new().right(6))
                                .image_url(format!("https://example.com/icon_{}.png", i)),
                        ),
                        DivValue::from(
                            DivText::new()
                                .width(DivWrapContentSize::new())
                                .max_lines(1)
                                .text(format!("Category {}", i)),
                        ),
                    ]),
            )
        })
        .collect();

    let gallery = DivGallery::new().items(items);
    let data = DivData {
        log_id: "complex_card".to_string(),
        states: vec![DivDataState {
            state_id: 0,
            div: Box::new(gallery),
        }],
    };
    data.dict()
}

fn build_schema() -> serde_json::Value {
    let t = DivText::new();
    t.schema(None)
}

struct BenchResult {
    name: String,
    avg_ns: f64,
    med_ns: f64,
    min_ns: u128,
    p95_ns: u128,
}

fn bench<F>(name: &str, func: F, iterations: usize, warmup: usize) -> BenchResult
where
    F: Fn(),
{
    // warmup
    for _ in 0..warmup {
        func();
    }

    let mut times: Vec<u128> = Vec::with_capacity(iterations);
    for _ in 0..iterations {
        let start = Instant::now();
        func();
        let elapsed = start.elapsed().as_nanos();
        times.append(&mut vec![elapsed]);
    }

    times.sort();
    let avg_ns = times.iter().sum::<u128>() as f64 / iterations as f64;
    let med_ns = times[iterations / 2] as f64;
    let min_ns = times[0];
    let p95_ns = times[(iterations as f64 * 0.95) as usize];

    println!(
        "{}:\n  avg: {:.1} us | med: {:.1} us | min: {:.1} us | p95: {:.1} us",
        name,
        avg_ns / 1000.0,
        med_ns / 1000.0,
        min_ns as f64 / 1000.0,
        p95_ns as f64 / 1000.0,
    );

    BenchResult {
        name: name.to_string(),
        avg_ns,
        med_ns,
        min_ns,
        p95_ns,
    }
}

fn main() {
    println!("{}", "=".repeat(60));
    println!("Rust (divkit-json-builder) Benchmark");
    println!("{}", "=".repeat(60));
    println!();

    let mut results = Vec::new();

    results.push(bench("1. Simple DivText", || { build_simple_text(); }, 1000, 100));
    results.push(bench("2. Nested Slider", || { build_slider(); }, 1000, 100));
    results.push(bench("3. Gallery 100 items", || { build_gallery_n(100); }, 500, 100));
    results.push(bench("4. Gallery 1000 items", || { build_gallery_n(1000); }, 100, 10));
    results.push(bench("5. Nested containers (depth=20)", || { build_nested_containers(20); }, 1000, 100));
    results.push(bench("6. Nested containers (depth=50)", || { build_nested_containers(50); }, 500, 100));
    results.push(bench("7. Complex layout (20 cards)", || { build_complex_layout(); }, 500, 100));
    results.push(bench("8. Schema generation", || { build_schema(); }, 1000, 100));

    // JSON serialization
    let data = build_complex_layout();
    results.push(bench("9. json_serialize(complex)", || { serde_json::to_string(&data).unwrap(); }, 1000, 100));

    println!();
    println!("--- Summary (avg us) ---");
    for r in &results {
        println!("  {}: {:.1} us", r.name, r.avg_ns / 1000.0);
    }
}
