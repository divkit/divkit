import pytest
from sqlalchemy.ext.asyncio import AsyncSession

from app.db.models import Tags
from app.schemas import CreateTagRequest, UpdateTagRequest
from app.utils import tags


class TestUtilsTags:
    def test_normalize_tag_name(self):  # type: ignore
        tag_name = "Button"
        expected_res = "button"
        res = tags.normalize_tag_name(tag_name)

        assert res == expected_res

    @pytest.mark.parametrize(
        "expected_res_lst,tag,page,size",
        [
            [["radiobutton", "green_button", "button"], "button", 0, 3],
            [[], "button", 0, 0],
            [["button", "button_green"], "button", 2, 2],
        ],
    )
    # pylint: disable=R0913
    # type: ignore
    async def test_get_tag_list(
        self, expected_res_lst, tag, page, size, session: AsyncSession
    ):
        tag_lst = [
            "radiobutton",
            "green_button",
            "button",
            "button_green",
            "div",
            "div2",
        ]

        for tag_name in tag_lst:
            tag_schema = CreateTagRequest(tag=tag_name)
            await tags.create_tag(tag_schema, session)

        tag_rows = await tags.get_tag_list(tag, page, size, session)
        res_tag_name_lst = [tag_row[1] for tag_row in tag_rows]

        assert expected_res_lst == res_tag_name_lst

    @pytest.mark.parametrize(
        "tag_name",
        [
            "",
            "button",
            "BuTtOn",
            "button123",
        ],
    )
    async def test_create_tag(self, tag_name, session: AsyncSession):  # type: ignore
        tag_schema = CreateTagRequest(tag=tag_name)

        created_tag_id = await tags.create_tag(tag_schema, session)
        created_tag = await session.get(Tags, str(created_tag_id))

        assert created_tag is not None
        assert created_tag.tag == tag_name.lower()

    async def test_update_tag(self, session: AsyncSession):  # type: ignore
        tag_name = "button"
        tag_schema = CreateTagRequest(tag=tag_name)
        tag_id = await tags.create_tag(tag_schema, session)

        new_tag_name = "button_new"
        new_tag_schema = UpdateTagRequest(id=tag_id, tag=new_tag_name)

        updated_tag_id = await tags.update_tag(new_tag_schema, session)
        updated_tag = await session.get(Tags, str(updated_tag_id))

        assert tag_id == updated_tag_id
        assert updated_tag is not None
        assert updated_tag.tag == new_tag_name
