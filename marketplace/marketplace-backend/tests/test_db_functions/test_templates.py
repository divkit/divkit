# pylint: disable=redefined-outer-name
# pylint: disable=unused-argument
# pylint: disable=unused-import
import pytest
from sqlalchemy.ext.asyncio import AsyncSession

from app.db.models import Tags, Templates, Templates2Tags
from app.db.storage.templates import (
    delete_template,
    get_template,
    get_template_list,
    insert_template,
    update_template,
)


class TestTemplates:
    async def test_insert_template(
        self, template: Templates, session: AsyncSession
    ) -> None:
        await insert_template(template, session)
        result = await get_template(template.id, session)  # type: ignore
        assert str(result.id) == template.id  # type: ignore

    async def test_get_template(
        self, template: Templates, session: AsyncSession
    ) -> None:
        session.add(template)
        result = await get_template(template.id, session)  # type: ignore
        assert str(result.id) == template.id  # type: ignore

    async def test_get_template_list_id(
        self,
        t2ts: list[Templates, Tags, Templates2Tags],  # type: ignore
        session: AsyncSession,
    ) -> None:
        result = await get_template_list([t2ts[0][1].id], None, 0, 20, session)
        assert str(result[0].id) == t2ts[0][2].id

    async def test_get_template_list_title(
        self,
        t2ts: list[Templates, Tags, Templates2Tags],  # type: ignore
        session: AsyncSession,
    ) -> None:
        result = await get_template_list(None, t2ts[0][2].title, 0, 20, session)
        assert str(result[0].id) == t2ts[0][2].id

    async def test_get_template_list_id_title(
        self,
        t2ts: list[Templates, Tags, Templates2Tags],  # type: ignore
        session: AsyncSession,
    ) -> None:
        result = await get_template_list(
            [t2ts[0][1].id], t2ts[0][2].title, 0, 20, session
        )
        assert str(result[0].id) == t2ts[0][2].id

    async def test_delete_template(
        self, template: Templates, session: AsyncSession
    ) -> None:
        session.add(template)
        result = await delete_template(template.id, session)  # type: ignore
        assert result is None

    async def test_update_template(
        self, templates: list[Templates], session: AsyncSession
    ) -> None:
        session.add(templates[1])  # type: ignore
        templates[1].id = templates[0].id  # type: ignore
        await update_template(templates[1], session)  # type: ignore
        result_2 = await get_template(templates[1].id, session)  # type: ignore
        assert templates[1].title == result_2.title  # type: ignore
