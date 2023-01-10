# pylint: disable=pointless-string-statement
# pylint: disable=consider-using-with
# pylint: disable=line-too-long
import json
from unittest.mock import AsyncMock, patch

import pytest
from httpx import AsyncClient

from app.schemas import GetTemplatesResponse, UpdateTemplateResponse
from app.utils.custom_exceptions import NotExistingIdsError
from tests.test_data import *

EXISTING_FILE_PATH = "alembic.ini"


@pytest.mark.parametrize(
    "test_data", [template_insert_data, template_insert_data_w_tag_ids]
)
async def test_create_template_201(async_client: AsyncClient, test_data) -> None:  # type: ignore
    """Test create_template with status_code 201."""

    with patch(
        "app.utils.templates.create_template", AsyncMock(return_value=template_uuid)
    ):
        result = await async_client.post(
            "/template",
            data={"template": test_data.json()},
            files={"file": open(EXISTING_FILE_PATH, "rb")},
        )

    assert result.status_code == 201
    assert result.json() == json.loads(template_result_data.json())


async def test_create_template_422(async_client: AsyncClient) -> None:
    """Test create_template with invalid tag_ids."""

    result = await async_client.post(
        "/template",
        data={"template": json.dumps(template_insert_data_not_valid_tag_ids)},
        files={"file": open(EXISTING_FILE_PATH, "rb")},
    )
    assert result.status_code == 422
    assert result.json()["detail"][0]["msg"] == "value is not a valid uuid"


async def test_create_template_400(async_client: AsyncClient) -> None:
    """Test create_template with not existing tag_ids."""

    with patch(
        "app.utils.templates.create_template",
        AsyncMock(side_effect=NotExistingIdsError),
    ):
        result = await async_client.post(
            "/template",
            data={"template": template_insert_data.json()},
            files={"file": open(EXISTING_FILE_PATH, "rb")},
        )

    assert result.status_code == 400
    assert result.json()["detail"] == "Not existing tag_ids"


@pytest.mark.parametrize(
    "get_templates_return_data,get_templates_with_tags_data",
    [
        ([template_data_w_url], [template_data_w_url_wo_tags]),
        ([], []),
        (
            [template_data_w_url, template_data_wo_url],
            [template_data_w_url_wo_tags, template_data_wo_url_w_tags],
        ),
    ],
)
async def test_get_template_list(  # type: ignore
    async_client: AsyncClient, get_templates_return_data, get_templates_with_tags_data
) -> None:
    """Test get_template_list."""

    with patch(
        "app.utils.templates.get_templates",
        AsyncMock(return_value=get_templates_return_data),
    ), patch(
        "app.utils.templates.get_templates_with_tags",
        AsyncMock(return_value=get_templates_with_tags_data),
    ), patch(
        "app.db.storage.templates.get_template_list_total_pages",
        AsyncMock(return_value=10),
    ):
        result = await async_client.get(f"/template?tag_ids={tag_uuid}&page=1&size=12")

    assert result.status_code == 200
    assert result.json() == json.loads(
        GetTemplatesResponse(
            templates=get_templates_with_tags_data, total_pages=10
        ).json()
    )


async def test_get_template_list_invalid_tag_id_422(async_client: AsyncClient) -> None:
    """Test get_template_list with invalid tag_id."""

    with patch("app.utils.templates.get_templates", AsyncMock(side_effect=ValueError)):
        result = await async_client.get("/template?tag_ids=not_valid")
    assert result.status_code == 422
    assert result.json()["detail"] == "Tag in tag_ids is not a valid uuid."


async def test_get_template_list_invalid_page_422(async_client: AsyncClient) -> None:
    """Test get_template_list with invalid page."""

    result = await async_client.get(f"/template?tag_ids={tag_uuid}&page=-1&size=12")
    assert result.status_code == 422
    assert (
        result.json()["detail"]
        == "Not valid pagination params. PAGE should be >= 1. SIZE should be >= 10."
    )


async def test_get_template_list_invalid_size_422(async_client: AsyncClient) -> None:
    """Test get_template_list with invalid size."""

    result = await async_client.get(f"/template?tag_ids={tag_uuid}&page=1&size=5")
    assert result.status_code == 422
    assert (
        result.json()["detail"]
        == "Not valid pagination params. PAGE should be >= 1. SIZE should be >= 10."
    )


async def test_update_template(async_client: AsyncClient) -> None:
    """Test update_template."""

    with patch(
        "app.utils.templates.update_template", AsyncMock(return_value=template_uuid)
    ):
        result = await async_client.put(
            "/template",
            data={"template": template_update_data.json()},
            files={"file": open(EXISTING_FILE_PATH, "rb")},
        )
    assert result.status_code == 200
    assert result.json() == json.loads(UpdateTemplateResponse(id=template_uuid).json())


async def test_update_template_400(async_client: AsyncClient) -> None:
    """Test update_template with not existing tag_ids."""

    with patch(
        "app.utils.templates.update_template",
        AsyncMock(side_effect=NotExistingIdsError),
    ):
        result = await async_client.put(
            "/template",
            data={"template": template_update_data.json()},
            files={"file": open(EXISTING_FILE_PATH, "rb")},
        )
    assert result.status_code == 400
    assert result.json()["detail"] == "Not existing template_id or tag_ids"


@pytest.mark.parametrize(
    "test_data",
    [template_update_data_not_valid_template_id, template_update_data_not_valid_tag_id],
)
async def test_update_template_422(async_client: AsyncClient, test_data) -> None:  # type: ignore
    """Test update_template with invalid template_id or tag_id."""

    result = await async_client.put(
        "/template",
        data={"template": json.dumps(test_data)},
        files={"file": open(EXISTING_FILE_PATH, "rb")},
    )
    assert result.status_code == 422
    assert result.json()["detail"][0]["msg"] == "value is not a valid uuid"


@pytest.mark.parametrize(
    "get_template_return_data,get_template_w_tags_return_data,expected_return",
    [
        (
            template_data_w_url,
            template_return_data_wo_tags,
            json.loads(template_return_data_wo_tags.json()),
        ),
        (
            template_data_wo_url,
            template_return_data_w_tags,
            json.loads(template_return_data_w_tags.json()),
        ),
    ],
)
async def test_get_template_200(  # type: ignore
    async_client: AsyncClient,
    get_template_return_data,
    get_template_w_tags_return_data,
    expected_return,
) -> None:
    """Test get_template."""
    with patch(
        "app.db.storage.templates.get_template",
        AsyncMock(return_value=get_template_return_data),
    ), patch(
        "app.utils.templates.get_one_template_with_tags",
        AsyncMock(return_value=get_template_w_tags_return_data),
    ):
        result = await async_client.get(f"/template/{template_uuid}")
    assert result.status_code == 200
    assert result.json() == expected_return


async def test_get_template_404(async_client: AsyncClient) -> None:
    """Test get_template when template not found."""

    with patch("app.db.storage.templates.get_template", AsyncMock(return_value=None)):
        result = await async_client.get(f"/template/{template_uuid}")
    assert result.status_code == 404
    assert result.json()["detail"] == "Template not found"


async def test_get_template_422(async_client: AsyncClient) -> None:
    """Test get_template with invalid template_id."""

    result = await async_client.get("/template/not_valid_id")
    assert result.status_code == 422
    assert result.json()["detail"][0]["msg"] == "value is not a valid uuid"


async def test_delete_template_204(async_client: AsyncClient) -> None:
    """Test delete_template."""

    with patch(
        "app.db.storage.templates.get_template",
        AsyncMock(return_value=template_data_w_url),
    ), patch("app.db.storage.templates.delete_template", AsyncMock()):
        result = await async_client.delete(f"/template/{template_uuid}")
    assert result.status_code == 204


async def test_delete_template_404(async_client: AsyncClient) -> None:
    """Test delete_template when template not found."""

    with patch("app.db.storage.templates.get_template", AsyncMock(return_value=None)):
        result = await async_client.delete(f"/template/{template_uuid}")
    assert result.status_code == 404
    assert result.json()["detail"] == "Template not found"


async def test_delete_template_422(async_client: AsyncClient) -> None:
    """Test delete_template with invalid template_id."""

    result = await async_client.delete("/template/not_valid_id")
    assert result.status_code == 422
    assert result.json()["detail"][0]["msg"] == "value is not a valid uuid"


async def test_len_description_template_create(  # type: ignore
    async_client: AsyncClient,
) -> None:  # type: ignore
    """Test create_template with description length>300"""

    result = await async_client.post(
        "/template",
        data={"template": json.dumps(template_insert_data_invalid_len_description)},
        files={"file": open(EXISTING_FILE_PATH, "rb")},
    )
    assert result.status_code == 422
    assert (
        result.json()["detail"][0]["msg"]
        == "Description length must be less or equal to 300."
    )


async def test_len_title_template_create(
    async_client: AsyncClient,
) -> None:  # type: ignore
    """Test create_template with titile length>50"""

    result = await async_client.post(
        "/template",
        data={"template": json.dumps(template_insert_data_invalid_len_title)},
        files={"file": open(EXISTING_FILE_PATH, "rb")},
    )
    assert result.status_code == 422
    assert (
        result.json()["detail"][0]["msg"] == "Title length must be less or equal to 50."
    )


async def test_len_title_template_update(
    async_client: AsyncClient,
) -> None:  # type: ignore
    """Test create_template with titile length>50"""

    result = await async_client.post(
        "/template",
        data={"template": json.dumps(template_update_invalid_len_title)},
        files={"file": open(EXISTING_FILE_PATH, "rb")},
    )
    assert result.status_code == 422
    assert (
        result.json()["detail"][0]["msg"] == "Title length must be less or equal to 50."
    )


async def test_len_desc_template_upd(
    async_client: AsyncClient,
) -> None:  # type: ignore
    """Test create_template with description len>300"""

    result = await async_client.post(
        "/template",
        data={"template": json.dumps(template_insert_data_invalid_len_description)},
        files={"file": open(EXISTING_FILE_PATH, "rb")},
    )
    assert result.status_code == 422
    assert (
        result.json()["detail"][0]["msg"]
        == "Description length must be less or equal to 300."
    )
