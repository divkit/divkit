# pylint: disable=redefined-outer-name
# pylint: disable=unused-argument
# pylint: disable=unused-import
import pytest
from sqlalchemy.ext.asyncio import AsyncSession

from app.db.models import Tags, Templates, Templates2Tags
from app.db.storage.templates2tags import (
    delete_template2tags,
    get_template2tag,
    insert_template2tag,
)


class TestTemplates2Tags:
    async def test_get_t2t(  # type: ignore
        self,
        t2ts: list[Templates2Tags, Templates, Tags],  # type: ignore
        session: AsyncSession,
    ):
        result = await get_template2tag(t2ts[0][1].id, t2ts[0][2].id, session)
        assert str(result) == t2ts[0][0].id

    async def test_insert_template2tag(  # type: ignore
        self, t2t: list[Templates2Tags, str], session: AsyncSession  # type: ignore
    ):
        result = await insert_template2tag(t2t[0], session)  # type: ignore
        assert result is None

    async def test_delete_template2tags(  # type: ignore
        self,
        t2ts: list[Templates2Tags, Templates, Tags],  # type: ignore
        session: AsyncSession,
    ):
        result = await delete_template2tags([t2ts[0][1].id], t2ts[0][2].id, session)  # type: ignore
        assert result is None
