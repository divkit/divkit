# Bench Task Index

This folder tracks benchmark-driven performance tasks for the current divkit-rs optimization wave.

- `B-01_make_card_compat_rust_helper.md`: move compat `make_card` parsing/dumping path into Rust helper.
- `B-02_template_name_collection_rust.md`: remove Python recursive template-name scan from hot path.
- `B-03_hotspot_microbench_suite.md`: add stable hotspot microbench suite and baselines.
- `B-04_zero_copy_pydiventity_dict.md`: remove clone-heavy `to_rust_entity()` dependency in `dict()`.
- `B-05_overlay_merge_and_alloc_cleanup.md`: JSON-level overlay merge and allocation cleanup in dict path.
- `B-06_template_dependency_closure_rust.md`: move template dependency closure expansion from Python to Rust.
- `B-07_constructor_value_related_templates_rust.md`: move constructor-value related template scanning to Rust.
- `B-08_dict_trampoline_removal_and_alloc_cleanup.md`: remove no-op `dict()` trampoline and trim related allocations.

Primary gate for this batch: microbench improvements with strict pydivkit compatibility parity.

## Bench Command

- Build/install extension: `source .venv/bin/activate && maturin develop --release`
- Run hotspot suite: `python docs/bench_hotspots.py --iterations 350 --warmup 100`
