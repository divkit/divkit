from pydantic import BaseSettings


class APISettings(BaseSettings):
    """API settings."""

    title: str
    host: str
    port: str

    class Config:
        env_prefix = "API_"
        env_file = ".env"

    @property
    def base_url(self) -> str:
        """Get base server URL."""
        return f"http://{self.host}:{self.port}"


def get_api_settings() -> APISettings:
    """Return an instance APISettings."""
    return APISettings()
