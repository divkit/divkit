# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div_download_callbacks


# It defines an action when clicking on an element.
class DivAction(BaseDiv):

    def __init__(
        self, *,
        download_callbacks: typing.Optional[div_download_callbacks.DivDownloadCallbacks] = None,
        log_id: typing.Optional[typing.Union[Expr, str]] = None,
        log_url: typing.Optional[typing.Union[Expr, str]] = None,
        menu_items: typing.Optional[typing.Sequence[DivActionMenuItem]] = None,
        payload: typing.Optional[typing.Dict[str, typing.Any]] = None,
        referer: typing.Optional[typing.Union[Expr, str]] = None,
        target: typing.Optional[typing.Union[Expr, DivActionTarget]] = None,
        url: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            download_callbacks=download_callbacks,
            log_id=log_id,
            log_url=log_url,
            menu_items=menu_items,
            payload=payload,
            referer=referer,
            target=target,
            url=url,
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
    log_url: typing.Optional[typing.Union[Expr, str]] = Field(
        format="uri", 
        description="URL for logging.",
    )
    menu_items: typing.Optional[typing.Sequence[DivActionMenuItem]] = Field(
        min_items=1, 
        description="Context menu.",
    )
    payload: typing.Optional[typing.Dict[str, typing.Any]] = Field(
        description="Additional parameters, passed to the host application.",
    )
    referer: typing.Optional[typing.Union[Expr, str]] = Field(
        format="uri", 
        description="Referer URL for logging.",
    )
    target: typing.Optional[typing.Union[Expr, DivActionTarget]] = Field(
        description="The tab in which the URL must be opened.",
    )
    url: typing.Optional[typing.Union[Expr, str]] = Field(
        format="uri", 
        description=(
            "URL. Possible values: `url` or `div-action://`. To learn "
            "more, see [Interactionwith "
            "elements](../../interaction.dita)."
        ),
    )


class DivActionTarget(str, enum.Enum):
    SELF = "_self"
    BLANK = "_blank"


class DivActionMenuItem(BaseDiv):

    def __init__(
        self, *,
        action: typing.Optional[DivAction] = None,
        actions: typing.Optional[typing.Sequence[DivAction]] = None,
        text: typing.Optional[typing.Union[Expr, str]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            action=action,
            actions=actions,
            text=text,
            **kwargs,
        )

    action: typing.Optional[DivAction] = Field(
        description=(
            "One action when clicking on a menu item. Not used if the "
            "`actions` parameter isset."
        ),
    )
    actions: typing.Optional[typing.Sequence[DivAction]] = Field(
        min_items=1, 
        description="Multiple actions when clicking on a menu item.",
    )
    text: typing.Union[Expr, str] = Field(
        min_length=1, 
        description="Menu item title.",
    )


DivActionMenuItem.update_forward_refs()


DivAction.update_forward_refs()
