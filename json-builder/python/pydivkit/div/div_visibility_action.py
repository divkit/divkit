# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field
import enum
import typing

from . import div_download_callbacks


# Actions performed when an element becomes visible.
class DivVisibilityAction(BaseDiv):

    def __init__(
        self, *,
        log_id: str,
        download_callbacks: typing.Optional[div_download_callbacks.DivDownloadCallbacks] = None,
        log_limit: typing.Optional[int] = None,
        payload: typing.Optional[typing.Dict[str, typing.Any]] = None,
        referer: typing.Optional[str] = None,
        url: typing.Optional[str] = None,
        visibility_duration: typing.Optional[int] = None,
        visibility_percentage: typing.Optional[int] = None,
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
        )

    download_callbacks: typing.Optional[div_download_callbacks.DivDownloadCallbacks] = Field(description='Callbacks that are called after [dataloading](../../interaction.dita#loading-data).')
    log_id: str = Field(min_length=1, description='Logging ID.')
    log_limit: typing.Optional[int] = Field(description='Limit on the number of loggings. If `0`, the limit is removed.')
    payload: typing.Optional[typing.Dict[str, typing.Any]] = Field(description='Additional parameters, passed to the host application.')
    referer: typing.Optional[str] = Field(format="uri", description='Referer URL for logging.')
    url: typing.Optional[str] = Field(format="uri", description='URL. Possible values: `url` or `div-action://`. To learn more, see [Interactionwith elements](../../interaction.dita).')
    visibility_duration: typing.Optional[int] = Field(description='Time in milliseconds during which an element must be visible to trigger`visibility-action`.')
    visibility_percentage: typing.Optional[int] = Field(description='Percentage of the visible part of an element that triggers `visibility-action`.')


DivVisibilityAction.update_forward_refs()
