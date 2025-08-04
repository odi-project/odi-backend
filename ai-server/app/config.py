import os
from typing import Optional

from pydantic_settings import BaseSettings, SettingsConfigDict

BASE_DIR = os.path.dirname(os.path.abspath(__file__))

class Settings(BaseSettings):
    # 클로바스튜디오 설정
    clova_studio_api_key: Optional[str] = None
    clova_studio_api_gateway_key: Optional[str] = None
    clova_studio_request_id: Optional[str] = None
    clova_studio_model_name: str = "HCX-005"
    clova_studio_base_url: str = "https://clovastudio.stream.ntruss.com"

    request_id_greeting: Optional[str] = None
    request_id_function_call: Optional[str] = None
    request_id_general_chat: Optional[str] = None

    # 서버 설정
    host: str = "0.0.0.0"    # 보안
    secret_key: Optional[str] = None
    port: int = 8000
    debug: bool = False
    log_level: str = "INFO"

    model_config = SettingsConfigDict(env_file=os.path.join(BASE_DIR, "../.env"))

settings = Settings()