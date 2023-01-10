# pylint: disable=duplicate-code
from datetime import datetime
from io import BytesIO
from typing import Optional
from uuid import UUID, uuid4

from fastapi import UploadFile
from miniopy_async import Minio
from pydantic import UUID4
from sqlalchemy.engine import Row
from sqlalchemy.exc import IntegrityError
from sqlalchemy.ext.asyncio import AsyncSession

from app.config import DefaultSettings
from app.db.models.templates import Templates
from app.db.models.templates_to_tags import Templates2Tags
from app.db.storage import tags as tags_storage
from app.db.storage import templates as templates_storage
from app.db.storage import templates2tags as templates2tags_storage
from app.schemas import CreateTemplateRequest, TemplateSchema, UpdateTemplateRequest
from app.utils.custom_exceptions import NotExistingIdsError
from app.utils.s3 import get_presigned_url, put_object, remove_object

settings = DefaultSettings()


async def get_templates(
    tag_ids: Optional[list],
    title: Optional[str],
    page: int,
    size: int,
    session: AsyncSession,
) -> list[Row]:
    if tag_ids:
        for tag in tag_ids:
            try:
                UUID4(tag)
            except Exception as exc:
                raise ValueError from exc
    template_list = await templates_storage.get_template_list(
        tag_ids, title, page, size, session
    )
    return template_list


async def get_one_template_with_tags(
    template: Row,
    client: Minio,
    session: AsyncSession,
) -> TemplateSchema:
    tag_list = await tags_storage.get_tag_list_by_template(template.id, session)
    url = (
        await get_presigned_url(
            client,
            bucket_name="preview",
            template_id=template.id,
            file_name=template.preview_filename,
        )
        if template.preview_filename
        else None
    )
    return TemplateSchema(
        id=template.id,
        template=template.template,
        description=template.description,
        title=template.title,
        dt_updated=template.dt_updated,
        preview_filename=template.preview_filename,
        preview_url=url,
        tags=tag_list,
    )


async def get_templates_with_tags(
    template_list: list[Row],
    client: Minio,
    session: AsyncSession,
) -> list[TemplateSchema]:
    template_list_with_tags = []
    for template in template_list:
        template_list_with_tags.append(
            await get_one_template_with_tags(template, client, session)
        )
    return template_list_with_tags


async def tag_ids_processing(
    tag_ids: Optional[list[UUID]],
    template_id: UUID,
    update_flag: bool,
    session: AsyncSession,
) -> None:
    if tag_ids:
        for tag_id in tag_ids:
            if update_flag:
                template2tag_id = await templates2tags_storage.get_template2tag(
                    tag_id, template_id, session
                )
                if template2tag_id:
                    continue
            t2t_id = uuid4()
            new_template2tags = Templates2Tags(
                id=str(t2t_id),
                tag_id=str(tag_id),
                template_id=str(template_id),
            )
            try:
                await templates2tags_storage.insert_template2tag(
                    new_template2tags, session
                )
            except IntegrityError as exc:
                raise NotExistingIdsError() from exc
    if update_flag:
        await templates2tags_storage.delete_template2tags(tag_ids, template_id, session)


async def check_tag_ids(tag_ids: list[UUID], session: AsyncSession) -> None:
    for tag_uuid in tag_ids:
        tag_id = await tags_storage.get_tag_by_id(tag_uuid, session)
        if tag_id:
            continue
        raise NotExistingIdsError("Not existing tag_ids")


async def put_preview(
    file: UploadFile, file_name: str, template_id: UUID4, client: Minio
) -> None:
    data = file.file.read()
    await put_object(
        client,
        template_id=template_id,
        bucket_name="preview",
        file_name=file_name,
        file_data=BytesIO(data),
        content_type="image/png",
    )


async def create_template(
    template: CreateTemplateRequest,
    file: Optional[UploadFile],
    client: Minio,
    session: AsyncSession,
) -> UUID:
    if template.tag_ids:
        await check_tag_ids(template.tag_ids, session)
    new_id = uuid4()
    file_name = " ".join(file.filename.strip().split()) if file else ""
    new_template = Templates(
        id=str(new_id),
        template=template.template,
        description=template.description,
        title=template.title,
        preview_filename=file_name if len(file_name) > 0 else template.preview_filename,
    )
    await templates_storage.insert_template(new_template, session)
    if template.tag_ids:
        await tag_ids_processing(template.tag_ids, new_id, False, session)
    await session.commit()
    if file:
        await put_preview(file, file_name, new_id, client)
    return new_id


async def update_template(
    template: UpdateTemplateRequest,
    file: Optional[UploadFile],
    client: Minio,
    session: AsyncSession,
) -> UUID:
    if template.tag_ids:
        await check_tag_ids(template.tag_ids, session)
    file_name = " ".join(file.filename.strip().split()) if file else ""
    if file:
        prev_template = await templates_storage.get_template(template.id, session)
        if prev_template and prev_template.preview_filename != file_name:
            await remove_object(
                client,
                bucket_name="preview",
                template_id=template.id,
                file_name=prev_template.preview_filename,
            )
    elif (
        file is None
        and template.preview_filename == ''
    ):
        prev_template = await templates_storage.get_template(template.id, session)
        if (
            prev_template
            and prev_template.preview_filename
            and len(prev_template.preview_filename) > 0
        ):
            await remove_object(
                client,
                bucket_name="preview",
                template_id=template.id,
                file_name=prev_template.preview_filename,
            )
    old_template = Templates(
        id=str(template.id),
        template=template.template,
        description=template.description,
        title=template.title,
        dt_updated=datetime.now(),
        preview_filename=file_name if len(file_name) > 0 else template.preview_filename,
    )
    await templates_storage.update_template(old_template, session)
    if template.tag_ids:
        await tag_ids_processing(template.tag_ids, template.id, True, session)
    await session.commit()
    if file:
        await put_preview(file, file_name, template.id, client)
    return template.id
