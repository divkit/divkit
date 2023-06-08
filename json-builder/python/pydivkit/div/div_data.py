# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Expr, Field

from . import div, div_timer, div_transition_selector, div_trigger, div_variable


# Root structure.
class DivData(BaseDiv):

    def __init__(
        self, *,
        log_id: typing.Optional[typing.Union[Expr, str]] = None,
        states: typing.Optional[typing.Sequence[DivDataState]] = None,
        timers: typing.Optional[typing.Sequence[div_timer.DivTimer]] = None,
        transition_animation_selector: typing.Optional[typing.Union[Expr, div_transition_selector.DivTransitionSelector]] = None,
        variable_triggers: typing.Optional[typing.Sequence[div_trigger.DivTrigger]] = None,
        variables: typing.Optional[typing.Sequence[div_variable.DivVariable]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            log_id=log_id,
            states=states,
            timers=timers,
            transition_animation_selector=transition_animation_selector,
            variable_triggers=variable_triggers,
            variables=variables,
            **kwargs,
        )

    log_id: typing.Union[Expr, str] = Field(
        min_length=1, 
        description="Logging ID.",
    )
    states: typing.Sequence[DivDataState] = Field(
        min_items=1, 
        description=(
            "A set of visual element states. Each element can have a few "
            "states with adifferent layout. The states are displayed "
            "strictly one by one and switched "
            "using[action](div-action.md)."
        ),
    )
    timers: typing.Optional[typing.Sequence[div_timer.DivTimer]] = Field(
        min_items=1, 
        description="List of timers.",
    )
    transition_animation_selector: typing.Optional[typing.Union[Expr, div_transition_selector.DivTransitionSelector]] = Field(
        description="Events that trigger transition animations. @deprecated",
    )
    variable_triggers: typing.Optional[typing.Sequence[div_trigger.DivTrigger]] = Field(
        min_items=1, 
        description="Triggers for changing variables.",
    )
    variables: typing.Optional[typing.Sequence[div_variable.DivVariable]] = Field(
        min_items=1, 
        description="Declaration of variables that can be used in an element.",
    )


class DivDataState(BaseDiv):

    def __init__(
        self, *,
        div: typing.Optional[div.Div] = None,
        state_id: typing.Optional[typing.Union[Expr, int]] = None,
        **kwargs: typing.Any,
    ):
        super().__init__(
            div=div,
            state_id=state_id,
            **kwargs,
        )

    div: div.Div = Field(
        description="Contents.",
    )
    state_id: typing.Union[Expr, int] = Field(
        description="State ID.",
    )


DivDataState.update_forward_refs()


DivData.update_forward_refs()
