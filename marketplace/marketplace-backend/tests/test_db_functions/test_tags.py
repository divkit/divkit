# pylint: disable=redefined-outer-name
# pylint: disable=unused-argument
# pylint: disable=unused-import
import pytest
from sqlalchemy.ext.asyncio import AsyncSession

from app.db.models import Tags, Templates2Tags
from app.db.storage import (
    get_tag,
    get_tag_by_id,
    get_tag_list,
    get_tag_list_by_template,
    insert_tag,
    update_tag,
)


class TestTags:
    async def test_insert_get_tag(self, tag: Tags, session: AsyncSession):  # type: ignore
        result = await insert_tag(tag, session)  # type: ignore
        assert result is None

        result = await get_tag(tag.tag, session)  # type: ignore
        assert str(result) == tag.id

    async def test_get_tag_by_id(self, tag: Tags, session: AsyncSession):  # type: ignore
        session.add(tag)
        result = await get_tag_by_id(tag.id, session)  # type: ignore
        assert str(result) == tag.id

    async def test_get_tag(self, tag: Tags, session: AsyncSession):  # type: ignore
        session.add(tag)
        result = await get_tag(tag.tag, session)  # type: ignore
        assert str(result) == tag.id

    async def test_update_tag(self, tag: Tags, session: AsyncSession):  # type: ignore
        await insert_tag(tag, session)
        await update_tag(tag, session)
        result = await get_tag(tag.tag, session)  # type: ignore
        assert tag.id == str(result)

    async def test_get_tag_list(  # type: ignore
        self, t2t: list[Templates2Tags, str], session: AsyncSession  # type: ignore
    ):  # type: ignore
        session.add(t2t[0])
        result_1 = await get_tag_list(t2t[1], 0, 20, session)
        result_2 = await get_tag(t2t[1], session)
        assert result_1[0].id == result_2

    async def test_get_tag_list_by_template(  # type: ignore
        self, t2t: list[Templates2Tags, str], session: AsyncSession  # type: ignore
    ):
        session.add(t2t[0])
        result = await get_tag_list_by_template(t2t[2], session)
        assert result[0].tag == t2t[1]
