.PHONY: fmt clippy lint-py lint test bump stubs check-stubs dist-clean dist-macos-arm64 dist-linux-x86_64 dist-linux-aarch64 dist-all publish

REPO_ROOT ?= $(abspath ../..)
PYTHON_TAG ?= cp312-cp312
MATURIN_IMAGE ?= ghcr.io/pyo3/maturin
TWINE_VERSION ?= 6.1.0

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

stubs:
	UV_CACHE_DIR=/tmp/uv-cache PYTHONPATH="$(REPO_ROOT)/api_generator" uv run --no-project \
		python generate_native_pyi.py -s "$(REPO_ROOT)/schema" -o python/divkit_rs/_native.pyi

check-stubs:
	UV_CACHE_DIR=/tmp/uv-cache PYTHONPATH="$(REPO_ROOT)/api_generator" uv run --no-project \
		python generate_native_pyi.py -s "$(REPO_ROOT)/schema" -o python/divkit_rs/_native.pyi --check

bump:
	@test -n "$(BASE)" || (echo "Usage: make bump BASE=x.y.z U=n" && exit 1)
	@echo "$(BASE)" | grep -Eq '^[0-9]+\.[0-9]+\.[0-9]+$$' || (echo "BASE must match x.y.z" && exit 1)
	@echo "$(U)" | grep -Eq '^[0-9]+$$' || (echo "U must be an integer" && exit 1)
	@echo "Bumping to shared version $(BASE)-post$(U) (PyPI normalized: $(BASE).post$(U))"
	@awk 'BEGIN { done = 0 } /^version = "/ && done == 0 { print "version = \"$(BASE)-post$(U)\""; done = 1; next } { print }' Cargo.toml > Cargo.toml.tmp && mv Cargo.toml.tmp Cargo.toml
	@awk 'BEGIN { done = 0 } /^version = "/ && done == 0 { print "version = \"$(BASE)-post$(U)\""; done = 1; next } { print }' pyproject.toml > pyproject.toml.tmp && mv pyproject.toml.tmp pyproject.toml
	@echo "Updated Cargo.toml and pyproject.toml. Run 'cargo check && uv lock' next."

dist-clean:
	rm -rf dist
	mkdir -p dist

dist-macos-arm64:
	UV_CACHE_DIR=/tmp/uv-cache uv run maturin build --release --interpreter .venv/bin/python -o dist

dist-linux-x86_64:
	docker run --rm --platform linux/amd64 -v "$(CURDIR):/io" $(MATURIN_IMAGE) \
		build --release --manifest-path /io/Cargo.toml --compatibility manylinux2014 \
		--interpreter /opt/python/$(PYTHON_TAG)/bin/python -o /io/dist

dist-linux-aarch64:
	docker run --rm --platform linux/arm64 -v "$(CURDIR):/io" $(MATURIN_IMAGE) \
		build --release --manifest-path /io/Cargo.toml --compatibility manylinux2014 \
		--interpreter /opt/python/$(PYTHON_TAG)/bin/python -o /io/dist

dist-all: dist-clean dist-macos-arm64 dist-linux-x86_64 dist-linux-aarch64

publish:
	@test -n "$(MATURIN_REPOSITORY_URL)" || (echo "Set MATURIN_REPOSITORY_URL" && exit 1)
	@test -n "$(MATURIN_USERNAME)" || (echo "Set MATURIN_USERNAME" && exit 1)
	@test -n "$(MATURIN_PASSWORD)" || (echo "Set MATURIN_PASSWORD" && exit 1)
	@echo "Uploading wheels to $(MATURIN_REPOSITORY_URL) with twine $(TWINE_VERSION)"
	uv run --with 'twine==$(TWINE_VERSION)' twine upload --verbose --non-interactive --disable-progress-bar \
		--repository-url "$(MATURIN_REPOSITORY_URL)" \
		-u "$(MATURIN_USERNAME)" -p "$(MATURIN_PASSWORD)" dist/*.whl
