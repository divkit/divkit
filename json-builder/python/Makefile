SCHEMA_DIR = ../../schema
API_GEN_DIR = ../../api_generator
OUTPUT_DIR = pydivkit/div

.PHONY: codegen format lint test bench

codegen:
	cd $(API_GEN_DIR) && python3 -m api_generator -c $(CURDIR)/codegen_config.json -s $(CURDIR)/$(SCHEMA_DIR) -o $(CURDIR)/$(OUTPUT_DIR)
	uv run ruff check --fix $(OUTPUT_DIR)
	uv run ruff format $(OUTPUT_DIR)

format:
	uv run ruff check --fix .
	uv run ruff format .

lint:
	uv run ruff check .
	uv run ruff format --check .
	uv run mypy pydivkit/core

test:
	uv run pytest tests/

bench:
	uv run python perf/bench_cast_value_type.py
