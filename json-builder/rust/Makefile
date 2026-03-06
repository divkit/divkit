.PHONY: fmt clippy lint-py lint test bump

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

bump:
	@test -n "$(BASE)" || (echo "Usage: make bump BASE=x.y.z U=n" && exit 1)
	@echo "$(BASE)" | grep -Eq '^[0-9]+\.[0-9]+\.[0-9]+$$' || (echo "BASE must match x.y.z" && exit 1)
	@echo "$(U)" | grep -Eq '^[0-9]+$$' || (echo "U must be an integer" && exit 1)
	@echo "Bumping to shared version $(BASE)-post$(U) (PyPI normalized: $(BASE).post$(U))"
	@awk 'BEGIN { done = 0 } /^version = "/ && done == 0 { print "version = \"$(BASE)-post$(U)\""; done = 1; next } { print }' Cargo.toml > Cargo.toml.tmp && mv Cargo.toml.tmp Cargo.toml
	@awk 'BEGIN { done = 0 } /^version = "/ && done == 0 { print "version = \"$(BASE)-post$(U)\""; done = 1; next } { print }' pyproject.toml > pyproject.toml.tmp && mv pyproject.toml.tmp pyproject.toml
	@echo "Updated Cargo.toml and pyproject.toml. Run 'cargo check && uv lock' next."
