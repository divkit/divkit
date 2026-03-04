.PHONY: fmt clippy lint fmt-py lint-py mypy lint-all

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

mypy:
	uv run mypy python/ tests/

lint-all: lint lint-py mypy
