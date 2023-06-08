# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field


class DivVideoSource(BaseDiv):

    def __init__(
        self, *,
        type: str = "video_source",
        bitrate: typing.Optional[typing.Union[Expr, int]] = None,
        mime_type: typing.Optional[typing.Union[Expr, str]] = None,
        resolution: typing.Optional[DivVideoSourceResolution] = None,
        url: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            bitrate=bitrate,
            mime_type=mime_type,
            resolution=resolution,
            url=url,
            **kwargs,
        )

    type: str = Field(default="video_source")
    bitrate: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "Properties of the media file that determines the data "
            "transfer rate in the videostream. Bitrate is measured in "
            "kilobits per second (kbps) and indicates how muchdata is "
            "transmitted per unit of time."
        ),
    )
    mime_type: typing.Union[Expr, str] = Field(
        description=(
            "The property defines the MIME type (Multipurpose Internet "
            "Mail Extensions) forthe media file. A MIME type is a string "
            "that indicates the type of file contentand is used to "
            "determine the type of file and its correct processing."
        ),
    )
    resolution: typing.Optional[DivVideoSourceResolution] = Field(
        description="Media file Resolution.",
    )
    url: typing.Union[Expr, str] = Field(
        format="uri", 
        description=(
            "The property contains a link to a media file available for "
            "playback or download."
        ),
    )


# Media file Resolution.
class DivVideoSourceResolution(BaseDiv):

    def __init__(
        self, *,
        type: str = "resolution",
        height: typing.Optional[typing.Union[Expr, int]] = None,
        width: typing.Optional[typing.Union[Expr, int]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            type=type,
            height=height,
            width=width,
            **kwargs,
        )

    type: str = Field(default="resolution")
    height: typing.Union[Expr, int] = Field(
        description=(
            "Contains information about the resolution height of the "
            "Media file."
        ),
    )
    width: typing.Union[Expr, int] = Field(
        description=(
            "Contains information about the resolution width of the "
            "video file."
        ),
    )


DivVideoSourceResolution.update_forward_refs()


DivVideoSource.update_forward_refs()
