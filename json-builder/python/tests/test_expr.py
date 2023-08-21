import pytest
from pydivkit.core import Expr


@pytest.mark.parametrize("input,res", (
    ("@{string}", Expr("@{string}")),

))
def test_expr(input, res):
    assert str(Expr(input)) == str(res)


@pytest.mark.parametrize("input", (
    "@{before",
    "{before",
    "after}",
    "notcontains"

))
def test_expr_fail(input):
    with pytest.raises(ValueError):
        Expr(input)
