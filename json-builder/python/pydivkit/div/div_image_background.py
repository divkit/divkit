# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import (
    div_alignment_horizontal, div_alignment_vertical, div_filter,
    div_image_scale,
)


# Background image.
class DivImageBackground(BaseDiv):

    def __init__(
        self, *,
        type: str = "image",
        alpha: typing.Optional[typing.Union[Expr, float]] = None,
        content_alignment_horizontal: typing.Optional[typing.Union[Expr, div_alignment_horizontal.DivAlignmentHorizontal]] = None,
        content_alignment_vertical: typing.Optional[typing.Union[Expr, div_alignment_vertical.DivAlignmentVertical]] = None,
        filters: typing.Optional[typing.Sequence[div_filter.DivFilter]] = None,
        image_url: typing.Optional[typing.Union[Expr, str]] = None,
        preload_required: typing.Optional[typing.Union[Expr, bool]] = None,
        scale: typing.Optional[typing.Union[Expr, div_image_scale.DivImageScale]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            alpha=alpha,
            content_alignment_horizontal=content_alignment_horizontal,
            content_alignment_vertical=content_alignment_vertical,
            filters=filters,
            image_url=image_url,
            preload_required=preload_required,
            scale=scale,
            **kwargs,
        )

    type: str = Field(default="image")
    alpha: typing.Optional[typing.Union[Expr, float]] = Field(
        description="Image transparency.",
    )
    content_alignment_horizontal: typing.Optional[typing.Union[Expr, div_alignment_horizontal.DivAlignmentHorizontal]] = Field(
        description="Horizontal image alignment.",
    )
    content_alignment_vertical: typing.Optional[typing.Union[Expr, div_alignment_vertical.DivAlignmentVertical]] = Field(
        description="Vertical image alignment.",
    )
    filters: typing.Optional[typing.Sequence[div_filter.DivFilter]] = Field(
        min_items=1, 
        description="Image filters.",
    )
    image_url: typing.Union[Expr, str] = Field(
        format="uri", 
        description="Image URL.",
    )
    preload_required: typing.Optional[typing.Union[Expr, bool]] = Field(
        description="Background image must be loaded before the display.",
    )
    scale: typing.Optional[typing.Union[Expr, div_image_scale.DivImageScale]] = Field(
        description="Image scaling.",
    )


DivImageBackground.update_forward_refs()
