from pydantic import BaseSettings


class S3Settings(BaseSettings):
    """S3 settings."""

    host: str
    port: str
    access_key: str
    secret_key: str

    class Config:
        env_prefix = "S3_"
        env_file = ".env"

    @property
    def base_url(self) -> str:
        """Get base server URL."""
        return f"{self.host}:{self.port}"


def get_s3_settings() -> S3Settings:
    """Return an instance S3Settings."""
    return S3Settings()
