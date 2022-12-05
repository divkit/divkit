ifeq ($(shell test -e '.env' && echo -n yes),yes)
	include .env
endif

APPLICATION_NAME = app
TEST = poetry run python -m pytest --verbosity=2 --showlocals --log-level=DEBUG
CODE = $(APPLICATION_NAME) tests

HELP_FUN = \
	%help; while(<>){push@{$$help{$$2//'options'}},[$$1,$$3] \
	if/^([\w-_]+)\s*:.*\#\#(?:@(\w+))?\s(.*)$$/}; \
    print"$$_:\n", map"  $$_->[0]".(" "x(20-length($$_->[0])))."$$_->[1]\n",\
    @{$$help{$$_}},"\n" for keys %help; \

# Commands
env:  ##@Environment Create .env file with variables
	@$(eval SHELL:=/bin/bash)
	@cp .env.sample .env

help: ##@Help Show this help
	@echo -e "Usage: make [target] ...\n"
	@perl -e '$(HELP_FUN)' $(MAKEFILE_LIST)

up:  ##@Database Create database with docker-compose
	docker-compose -f docker-compose.yml up -d --remove-orphans

lint:  ##@Code Check code with pylint
	poetry run python3 -m pylint $(CODE)
	poetry run python3 -m mypy $(CODE)

format:  ##@Code Reformat code with isort and black
	poetry run python3 -m isort $(CODE)
	poetry run python3 -m black $(CODE)

migrate:  ##@Database Do all migrations in database
	docker-compose exec app alembic upgrade $(args)

run:  ##@Application Run application server
	poetry run python3 -m app

revision:  ##@Database Create new revision file automatically with prefix (ex. 2022_01_01_14cs34f_message.py)
	docker-compose exec app alembic revision --autogenerate

open_db:  ##@Database Open database inside docker-image
	docker exec -it divkit_db psql -d $(POSTGRES_DB) -U $(POSTGRES_USERNAME)

test:  ##@Testing Test application with pytest
	$(TEST)

test-cov:  ##@Testing Test application with pytest and create coverage report
	$(TEST) --cov=$(APPLICATION_NAME) --cov-report html --cov-fail-under=70

clean:  ##@Code Clean directory from garbage files
	rm -fr *.egg-info dist
