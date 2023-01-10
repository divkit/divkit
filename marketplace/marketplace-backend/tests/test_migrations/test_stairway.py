"""
Test can find forgotten downgrade methods, undeleted data types in downgrade
methods, typos and many other errors.
Does not require any maintenance - you just add it once to check 80% of typos
and mistakes in migrations forever.
"""
from types import SimpleNamespace

import pytest
from alembic.command import downgrade, upgrade
from alembic.config import Config
from alembic.script import Script, ScriptDirectory

from tests.utils import make_alembic_config


def get_revisions() -> list[Script]:
    # Create Alembic configuration object
    # (we don't need database for getting revisions list)
    options = SimpleNamespace(
        config="", name="alembic", pg_url=None, raiseerr=False, x=None
    )
    config = make_alembic_config(options)

    # Get directory object with Alembic migrations
    revisions_dir = ScriptDirectory.from_config(config)

    # Get & sort migrations, from first to last
    revisions = list(revisions_dir.walk_revisions("base", "heads"))
    revisions.reverse()
    return revisions


@pytest.mark.parametrize("revision", get_revisions())
def test_migrations_stairway(revision: Script, alembic_config: Config) -> None:
    upgrade(alembic_config, revision.revision)
    # We need -1 for downgrading first migration (its down_revision is None)
    downgrade(alembic_config, revision.down_revision or "-1")  # type: ignore
    upgrade(alembic_config, revision.revision)
