# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div_absolute_edge_insets


# Image in nine patch format.
# https://developer.android.com/studio/write/draw9patch.
class DivNinePatchBackground(BaseDiv):

    def __init__(
        self, *,
        image_url: str,
        insets: div_absolute_edge_insets.DivAbsoluteEdgeInsets,
        type: str = "nine_patch_image",
    ):
        super().__init__(
            type=type,
            image_url=image_url,
            insets=insets,
        )

    type: str = Field(default="nine_patch_image")
    image_url: str = Field(
        format="uri", 
        description="Image URL.",
    )
    insets: div_absolute_edge_insets.DivAbsoluteEdgeInsets = Field(
        description=(
            "Margins that break images into parts, according to the "
            "rule, are similar "
            "toborder-image-slice.(https://developer.mozilla.org/en-US/d"
            "ocs/Web/CSS/border-image-slice)"
        ),
    )


DivNinePatchBackground.update_forward_refs()
