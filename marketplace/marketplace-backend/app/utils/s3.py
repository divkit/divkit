from datetime import timedelta
from typing import BinaryIO
from uuid import UUID

from miniopy_async import Minio

from app.config import get_s3_settings

S3_SETTINGS = get_s3_settings()


def get_client() -> Minio:
    client = Minio(
        S3_SETTINGS.base_url,
        access_key=S3_SETTINGS.access_key,
        secret_key=S3_SETTINGS.secret_key,
        secure=False,
    )
    return client


async def get_presigned_url(
    client: Minio, bucket_name: str, template_id: UUID, file_name: str
) -> str:
    object_name = f"{template_id}___{file_name}"
    url = await client.get_presigned_url(
        "GET",
        bucket_name,
        object_name,
        expires=timedelta(days=7),
    )
    return url


async def put_object(  # pylint: disable=too-many-arguments
    client: Minio,
    template_id: UUID,
    bucket_name: str,
    file_data: BinaryIO,
    file_name: str,
    content_type: str,
) -> None:
    object_name = f"{template_id}___{file_name}"

    await client.put_object(
        bucket_name=bucket_name,
        object_name=object_name,
        data=file_data,
        content_type=content_type,
        length=-1,
        part_size=10 * 1024 * 1024,
    )


async def remove_object(
    client: Minio, bucket_name: str, template_id: UUID, file_name: str
) -> None:
    object_name = f"{template_id}___{file_name}"
    await client.remove_object(
        bucket_name,
        object_name,
    )
