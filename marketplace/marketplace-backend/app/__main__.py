import uvicorn
from fastapi import FastAPI

from app.api.endpoints_v1 import list_of_routes
from app.config import get_api_settings

settings = get_api_settings()


def bind_routes(application: FastAPI) -> None:
    """
    Bind all routes to application.
    """
    for route in list_of_routes:
        application.include_router(route)


def get_application() -> FastAPI:

    application = FastAPI(**settings.dict())
    bind_routes(application)

    return application


app = get_application()

if __name__ == "__main__":
    uvicorn.run(app, host=settings.host, port=int(settings.port))
