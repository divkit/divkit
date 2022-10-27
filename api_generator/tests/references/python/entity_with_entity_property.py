# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import entity


class EntityWithEntityProperty(BaseDiv):

    def __init__(
        self, *,
        type: str = 'entity_with_entity_property',
        entity: typing.Optional[entity.Entity] = None,
    ):
        super().__init__(
            type=type,
            entity=entity,
        )

    type: str = Field(default='entity_with_entity_property')
    entity: typing.Optional[entity.Entity] = Field(
    )


EntityWithEntityProperty.update_forward_refs()
