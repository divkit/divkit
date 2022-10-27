# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div


# Edits the element.
class DivPatch(BaseDiv):

    def __init__(
        self, *,
        changes: typing.List[DivPatchChange],
        mode: typing.Optional[DivPatchMode] = None,
    ):
        super().__init__(
            changes=changes,
            mode=mode,
        )

    changes: typing.List[DivPatchChange] = Field(
        min_items=1, 
        description="Element changes.",
    )
    mode: typing.Optional[DivPatchMode] = Field(
        description=(
            "Procedure for applying changes:`transactional` — if an "
            "error occurs duringapplication of at least one element, the "
            "changes aren\'t applied.`partial` — allpossible changes are "
            "applied. If there are errors, they are reported."
        ),
    )


class DivPatchMode(str, enum.Enum):
    TRANSACTIONAL = "transactional"
    PARTIAL = "partial"


class DivPatchChange(BaseDiv):

    def __init__(
        self, *,
        id: str,
        items: typing.Optional[typing.List[div.Div]] = None,
    ):
        super().__init__(
            id=id,
            items=items,
        )

    id: str = Field(
        description="ID of an element to be replaced or removed.",
    )
    items: typing.Optional[typing.List[div.Div]] = Field(
        min_items=1, 
        description=(
            "Elements to be inserted. If the parameter isn\'t specified, "
            "the element will beremoved."
        ),
    )


DivPatchChange.update_forward_refs()


DivPatch.update_forward_refs()
