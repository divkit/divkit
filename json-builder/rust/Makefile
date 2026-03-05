.PHONY: fmt clippy lint-py lint test

fmt:
	cargo fmt
	uv run ruff format python/ tests/
	uv run ruff check --fix python/ tests/

clippy:
	cargo clippy -- -D warnings

lint-py:
	uv run ruff format --check python/ tests/
	uv run ruff check python/ tests/

lint: fmt clippy lint-py

test:
	cargo test --lib
