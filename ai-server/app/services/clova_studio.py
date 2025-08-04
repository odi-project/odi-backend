import uuid
from enum import Enum
from typing import Optional, Dict, Any

import httpx
import structlog
from app.config import settings
from app.schemas.chat import ChatCompletionRequest, ChatCompletionResponse

logger = structlog.get_logger()

class RequestType(str, Enum):
    """요청 타입 분류"""
    GREETING = "greeting"
    FUNCTION_CALL = "function_call"
    GENERAL_CHAT = "general_chat"

class ClovaStudioService:
    def __init__(self):
        self.base_url = settings.clova_studio_base_url
        self.api_key = settings.clova_studio_api_key
        self.gateway_key = settings.clova_studio_api_gateway_key
        self.model_name = settings.clova_studio_model_name

    def _generate_request_id(self, request_type: RequestType) -> str:
        """요청 타입에 따른 Request ID 반환 (환경변수에서 가져오기)"""

        # 1. 전역 고정 ID가 있으면 우선 사용
        if settings.clova_studio_request_id:
            return settings.clova_studio_request_id

        # 2. 요청 타입별로 환경변수에서 설정된 ID 사용
        if request_type == RequestType.GREETING:
            return settings.request_id_greeting
        elif request_type == RequestType.FUNCTION_CALL:
            return settings.request_id_function_call
        else:  # GENERAL_CHAT
            return settings.request_id_general_chat

    def _get_headers(
            self,
            request_type: RequestType,
            custom_request_id: Optional[str] = None,
            accept_type: str = "application/json"
    ) -> Dict[str, str]:
        """API 요청 헤더 생성"""

        # Request ID 결정 우선순위:
        # 1. 커스텀 ID (함수 파라미터)
        # 2. 환경변수에서 설정된 타입별 ID
        if custom_request_id:
            request_id = custom_request_id
        else:
            request_id = self._generate_request_id(request_type)

        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json",
            "Accept": accept_type,
            "X-NCP-CLOVASTUDIO-REQUEST-ID": request_id
        }

        # 디버깅용 추가 헤더
        headers["X-Request-Type"] = request_type.value

        return headers

    def _prepare_request_data(self, request: ChatCompletionRequest) -> Dict[str, Any]:
        """요청 데이터 준비"""
        # Message 객체를 딕셔너리로 변환
        messages = []
        for msg in request.messages:
            message_dict = {
                "role": msg.role.value,
                "content": msg.content
            }
            if msg.tool_calls:
                message_dict["toolCalls"] = [
                    {
                        "id": tc.id,
                        "type": tc.type.value,
                        "function": tc.function
                    } for tc in msg.tool_calls
                ]
            if msg.tool_call_id:
                message_dict["toolCallId"] = msg.tool_call_id

            messages.append(message_dict)

        data = {
            "messages": messages,
            "maxTokens": request.max_tokens,
            "temperature": request.temperature,
            "topP": request.top_p,
            "topK": request.top_k,
            "repetitionPenalty": request.repetition_penalty,
            "stop": request.stop
        }

        # Function calling 설정
        if request.tools:
            data["tools"] = [
                {
                    "type": tool.type.value,
                    "function": {
                        "name": tool.function.name,
                        "description": tool.function.description,
                        "parameters": tool.function.parameters.dict()
                    }
                } for tool in request.tools
            ]
            data["toolChoice"] = request.tool_choice

        return data

    async def chat_completion(self, request: ChatCompletionRequest, request_type: RequestType) -> ChatCompletionResponse:
        """채팅 완성 API 호출"""
        url = f"{self.base_url}/v3/chat-completions/{self.model_name}"
        headers = self._get_headers(request_type)
        data = self._prepare_request_data(request)

        logger.info("Calling Clova Studio API", url=url, model=self.model_name)

        async with httpx.AsyncClient(timeout=30.0) as client:
            try:
                response = await client.post(url, headers=headers, json=data)
                response.raise_for_status()

                result = response.json()
                logger.info("API call successful", status_code=response.status_code)

                return ChatCompletionResponse(**result)

            except httpx.HTTPStatusError as e:
                logger.error("HTTP error occurred", status_code=e.response.status_code, response=e.response.text)
                raise Exception(f"API 호출 실패: {e.response.status_code} - {e.response.text}")
            except Exception as e:
                logger.error("Unexpected error occurred", error=str(e))
                raise Exception(f"예상치 못한 오류: {str(e)}")

    async def simple_chat(self, message: str, max_tokens: int = 1024, temperature: float = 0.5) -> str:
        """간단한 채팅 (Function Calling 없이)"""
        request = ChatCompletionRequest(
            messages=[
                {"role": "system", "content":"""
                    | 시간대 | 범위 (24시간제)     | 인사 표현 예시      |\n| --- | -------------- | ------------- |\n| 아침  | 05:00 \\~ 11:59 | 좋은 아침이야! ☀️   |\n| 점심  | 12:00 \\~ 13:59 | 점심 잘 먹었어? 🍱  |\n| 오후  | 14:00 \\~ 17:59 | 오후도 힘내! ☕     |\n| 저녁  | 18:00 \\~ 21:59 | 좋은 저녁 보내\\~ 🌆 |\n| 밤   | 22:00 \\~ 04:59 | 늦었네, 푹 쉬어! 🌙 |\n\n지금 시간이 [현재시간]이니까, 친구에게 이렇게 인사해줘:\n\"[인사말] [친근한 멘트 한 마디]\"
                """},
                {"role": "user", "content": message}
            ],
            max_tokens=max_tokens,
            temperature=temperature
        )

        response = await self.chat_completion(request, request_type=RequestType.GREETING)

        # 응답에서 텍스트 추출
        if response.result and "message" in response.result:
            return response.result["message"].get("content", "응답을 생성할 수 없습니다.")

        return "응답을 생성할 수 없습니다."

# 싱글톤 인스턴스
clova_service = ClovaStudioService()