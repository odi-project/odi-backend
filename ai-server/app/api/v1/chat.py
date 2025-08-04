from fastapi import APIRouter, HTTPException, Depends
from fastapi.responses import StreamingResponse
from typing import AsyncGenerator
import json

from app.services.clova_studio import clova_service
from app.schemas.chat import (
    ChatCompletionRequest,
    ChatCompletionResponse,
    SimpleChatRequest,
    SimpleChatResponse
)
from app.schemas.common import APIResponse, ErrorResponse
import structlog

logger = structlog.get_logger()
router = APIRouter(prefix="/chat", tags=["Chat"])


@router.post("/simple", response_model=SimpleChatResponse)
async def simple_chat(request: SimpleChatRequest):
    """
    간단한 채팅 API (Function Calling 없이)
    - 오디 서비스 메인 페이지에서 사용
    """
    try:
        logger.info("Simple chat request received", message_length=len(request.message))

        response_text = await clova_service.simple_chat(
            message=request.message,
            max_tokens=request.max_tokens or 1024,
            temperature=request.temperature or 0.5
        )

        return SimpleChatResponse(response=response_text)

    except Exception as e:
        logger.error("Simple chat failed", error=str(e))
        raise HTTPException(status_code=500, detail=str(e))