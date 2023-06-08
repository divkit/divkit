# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_appearance_transition


# A set of animations to be applied simultaneously.
class DivAppearanceSetTransition(BaseDiv):

    def __init__(
        self, *,
        type: str = "set",
        items: typing.Optional[typing.Sequence[div_appearance_transition.DivAppearanceTransition]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            items=items,
            **kwargs,
        )

    type: str = Field(default="set")
    items: typing.Sequence[div_appearance_transition.DivAppearanceTransition] = Field(
        min_items=1, 
        description="An array of animations.",
    )


DivAppearanceSetTransition.update_forward_refs()
