# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_download_callbacks


# Actions performed when an element becomes visible.
class DivVisibilityAction(BaseDiv):

    def __init__(
        self, *,
        download_callbacks: typing.Optional[div_download_callbacks.DivDownloadCallbacks] = None,
        log_id: typing.Optional[typing.Union[Expr, str]] = None,
        log_limit: typing.Optional[typing.Union[Expr, int]] = None,
        payload: typing.Optional[typing.Dict[str, typing.Any]] = None,
        referer: typing.Optional[typing.Union[Expr, str]] = None,
        url: typing.Optional[typing.Union[Expr, str]] = None,
        visibility_duration: typing.Optional[typing.Union[Expr, int]] = None,
        visibility_percentage: typing.Optional[typing.Union[Expr, int]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            download_callbacks=download_callbacks,
            log_id=log_id,
            log_limit=log_limit,
            payload=payload,
            referer=referer,
            url=url,
            visibility_duration=visibility_duration,
            visibility_percentage=visibility_percentage,
            **kwargs,
        )

    download_callbacks: typing.Optional[div_download_callbacks.DivDownloadCallbacks] = Field(
        description=(
            "Callbacks that are called after "
            "[dataloading](../../interaction.dita#loading-data)."
        ),
    )
    log_id: typing.Union[Expr, str] = Field(
        min_length=1, 
        description="Logging ID.",
    )
    log_limit: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "Limit on the number of loggings. If `0`, the limit is "
            "removed."
        ),
    )
    payload: typing.Optional[typing.Dict[str, typing.Any]] = Field(
        description="Additional parameters, passed to the host application.",
    )
    referer: typing.Optional[typing.Union[Expr, str]] = Field(
        format="uri", 
        description="Referer URL for logging.",
    )
    url: typing.Optional[typing.Union[Expr, str]] = Field(
        format="uri", 
        description=(
            "URL. Possible values: `url` or `div-action://`. To learn "
            "more, see [Interactionwith "
            "elements](../../interaction.dita)."
        ),
    )
    visibility_duration: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "Time in milliseconds during which an element must be "
            "visible to trigger`visibility-action`."
        ),
    )
    visibility_percentage: typing.Optional[typing.Union[Expr, int]] = Field(
        description=(
            "Percentage of the visible part of an element that triggers "
            "`visibility-action`."
        ),
    )


DivVisibilityAction.update_forward_refs()
