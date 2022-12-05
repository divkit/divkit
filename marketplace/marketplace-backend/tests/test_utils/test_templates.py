from uuid import UUID, uuid4

import pytest
from sqlalchemy.ext.asyncio import AsyncSession

from app.db.storage import tags as tags_storage
from app.db.storage import templates as temps_storage
from app.db.storage import templates2tags as t2t
from app.schemas.template import UpdateTemplateRequest
from app.utils import templates as temps_utils
from app.utils.custom_exceptions import NotExistingIdsError
from app.utils.s3 import get_client
from tests.test_utils import utils as test_utils
from tests.test_utils.template_sample import template_samples


class TestUtilsTemplates:
    async def test_get_templates(self, session: AsyncSession):  # type: ignore
        tag_ids = [
            str(await test_utils.create_tag(tag_name, session))
            for tag_name in ["button", "gallery"]
        ]

        templates = [
            await test_utils.create_template(template_sample, session, tag_ids)
            for template_sample in template_samples
        ]

        templates_ids = [temp.id for temp in templates]  # type: ignore

        received_templates = await temps_utils.get_templates(
            title="title", tag_ids=tag_ids, page=0, size=10, session=session
        )
        received_templates_ids = [temp.id for temp in received_templates]

        assert set(received_templates_ids) == set(templates_ids)

    @pytest.mark.parametrize(
        "tag_names_to_create,tag_names_to_process,update_flag",
        [
            [["view"], ["button", "gallery"], True],
            [["button"], ["gallery"], False],
            [[], [], False],
            [[], [], True],
        ],
    )
    # type: ignore
    async def test_tag_ids_processing(
        self,
        tag_names_to_create,
        tag_names_to_process,
        update_flag,
        session: AsyncSession,
    ):
        created_tag_ids = [
            await test_utils.create_tag(tag_name, session)
            for tag_name in tag_names_to_create
        ]
        tag_ids_to_process = [
            await test_utils.create_tag(tag_name, session)
            for tag_name in tag_names_to_process
        ]
        template = await test_utils.create_template(
            template_samples[0], session, tag_ids=created_tag_ids
        )

        await temps_utils.tag_ids_processing(
            created_tag_ids + tag_ids_to_process, template.id, update_flag, session  # type: ignore
        )

        tag_ids_to_check = tag_ids_to_process
        if not update_flag:
            tag_ids_to_check += created_tag_ids

        checked_tag_ids = [
            await t2t.get_template2tag(tag_id, template.id, session)  # type: ignore
            for tag_id in tag_ids_to_check
        ]

        assert all(checked_tag_ids)

    @pytest.mark.parametrize(
        "tag_names,tag_names_to_check,is_error",
        [
            [[], [], False],
            [["button", "gallery"], ["button", "gallery"], False],
            [["alpha", "background", "border"], ["alpha", "ground", "border"], True],
        ],
    )
    # type: ignore
    async def test_check_tag_ids(
        self, tag_names, tag_names_to_check, is_error, session: AsyncSession
    ):
        tags = {tag: await test_utils.create_tag(tag, session) for tag in tag_names}
        tags_to_check = []
        for tag_name_to_check in tag_names_to_check:
            if tag_name_to_check in tag_names:
                tags_to_check.append(tags[tag_name_to_check])
            else:
                tags_to_check.append(uuid4())

        try:
            await temps_utils.check_tag_ids(tags_to_check, session)
        except NotExistingIdsError:
            assert is_error, "Непредвиденная ошибка NotExistingIdsError"

    async def test_create_template(self, session: AsyncSession):  # type: ignore
        template = await test_utils.create_template(template_samples[0], session)  # type: ignore
        assert template.template == template_samples[0]  # type: ignore

    async def test_update_template(self, session: AsyncSession):  # type: ignore
        async def tag_ids_from_template(
            template_id: UUID, session: AsyncSession
        ) -> list[UUID]:
            tag_ids = [
                tag_id
                for tag_id, tag_name in await tags_storage.get_tag_list_by_template(
                    template_id, session
                )
            ]
            return tag_ids

        template = await test_utils.create_template(template_samples[0], session)

        new_tag = await test_utils.create_tag("second test tag", session)
        old_tags = await tag_ids_from_template(template.id, session)  # type: ignore
        new_tags = old_tags + [str(new_tag)]

        updated_template_schema = UpdateTemplateRequest(
            id=template.id,  # type: ignore
            template=template_samples[1],
            description="updated test description",
            title="updated test title",
            tag_ids=new_tags,
        )
        updated_template_id = await temps_utils.update_template(
            updated_template_schema, None, get_client(), session
        )
        updated_template = await temps_storage.get_template(
            updated_template_id, session
        )
        updated_new_tags = await tag_ids_from_template(updated_template_id, session)

        assert updated_template.id == template.id  # type: ignore
        assert updated_template.template == template_samples[1]  # type: ignore
        assert updated_template.description == "updated test description"  # type: ignore
        assert updated_template.title == "updated test title"  # type: ignore
        assert updated_new_tags == new_tags
