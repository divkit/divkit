import json
from unittest.mock import AsyncMock, patch

import pytest
from httpx import AsyncClient

from app.schemas.tag import GetTagsResponse
from tests.test_data import *


async def test_create_tag_201(async_client: AsyncClient) -> None:
    """Test create_tag."""

    with patch("app.utils.tags.create_tag", AsyncMock(return_value=tag_uuid)):
        result = await async_client.post("/tag", json=tag_insert_data.dict())
    assert result.status_code == 201
    assert result.json() == json.loads(tag_result_data.json())


@pytest.mark.parametrize("test_data", [[tag_data], [], [tag_data, tag_data_2]])
async def test_get_tag_list_200(async_client: AsyncClient, test_data) -> None:  # type: ignore
    """Test get_tag_list."""

    with patch("app.utils.tags.get_tag_list", AsyncMock(return_value=test_data)):
        result = await async_client.get(f"/tag/{tag_data.tag}")
    assert result.status_code == 200
    assert result.json() == json.loads(GetTagsResponse(tags=test_data).json())


async def test_get_tag_list_invalid_page_422(async_client: AsyncClient) -> None:
    """Test get_tag_list with invalid page."""

    result = await async_client.get(f"/tag/{tag_data.tag}?page=-1&size=20")
    assert result.status_code == 422
    assert (
        result.json()["detail"]
        == "Not valid pagination params. PAGE should be >= 1. SIZE should be >= 10"
    )


async def test_get_tag_list_invalid_size_422(async_client: AsyncClient) -> None:
    """Test get_tag_list with invalid size."""

    result = await async_client.get(f"/tag/{tag_data.tag}?page=2&size=5")
    assert result.status_code == 422
    assert (
        result.json()["detail"]
        == "Not valid pagination params. PAGE should be >= 1. SIZE should be >= 10"
    )


async def test_create_tag_invalid_len_422(
    async_client: AsyncClient,
) -> None:
    """Test create tag with length above 30"""
    result = await async_client.post("/tag", json=tag_invalid_len)
    assert result.status_code == 422
    assert (
        result.json()["detail"][0]["msg"] == "Tag length must be less or equal to 30."
    )
