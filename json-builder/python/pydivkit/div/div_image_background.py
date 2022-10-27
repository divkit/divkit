# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div_alignment_horizontal, div_alignment_vertical, div_image_scale


# Background image.
class DivImageBackground(BaseDiv):

    def __init__(
        self, *,
        image_url: str,
        type: str = "image",
        alpha: typing.Optional[float] = None,
        content_alignment_horizontal: typing.Optional[div_alignment_horizontal.DivAlignmentHorizontal] = None,
        content_alignment_vertical: typing.Optional[div_alignment_vertical.DivAlignmentVertical] = None,
        preload_required: typing.Optional[bool] = None,
        scale: typing.Optional[div_image_scale.DivImageScale] = None,
    ):
        super().__init__(
            type=type,
            alpha=alpha,
            content_alignment_horizontal=content_alignment_horizontal,
            content_alignment_vertical=content_alignment_vertical,
            image_url=image_url,
            preload_required=preload_required,
            scale=scale,
        )

    type: str = Field(default="image")
    alpha: typing.Optional[float] = Field(
        description="Image transparency.",
    )
    content_alignment_horizontal: typing.Optional[div_alignment_horizontal.DivAlignmentHorizontal] = Field(
        description="Horizontal image alignment.",
    )
    content_alignment_vertical: typing.Optional[div_alignment_vertical.DivAlignmentVertical] = Field(
        description="Vertical image alignment.",
    )
    image_url: str = Field(
        format="uri", 
        description="Image URL.",
    )
    preload_required: typing.Optional[bool] = Field(
        description="Background image must be loaded before the display.",
    )
    scale: typing.Optional[div_image_scale.DivImageScale] = Field(
        description="Image scaling.",
    )


DivImageBackground.update_forward_refs()
