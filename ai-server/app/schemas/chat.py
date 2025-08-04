from enum import Enum
from typing import Any, Dict, List, Optional, Union
from pydantic import BaseModel, Field


class MessageRole(str, Enum):
    SYSTEM = "system"
    USER = "user"
    ASSISTANT = "assistant"
    TOOL = "tool"

class ToolType(str, Enum):
    FUNCTION = "function"

class FunctionParameters(BaseModel):
    type: str = "object"
    properties: Dict[str, Any]
    required: List[str] = []

class Function(BaseModel):
    name: str
    description: str
    parameters: FunctionParameters

class Tool(BaseModel):
    type: ToolType = ToolType.FUNCTION
    function: Function

class ToolCall(BaseModel):
    id: str
    type: ToolType = ToolType.FUNCTION
    function: Dict[str, Any]

class Message(BaseModel):
    role: MessageRole
    content: str
    tool_calls: Optional[List[ToolCall]] = None
    tool_call_id: Optional[str] = None

class ChatCompletionRequest(BaseModel):
    messages: List[Message]
    tools: Optional[List[Tool]] = None
    tool_choice: Optional[Union[str, Dict[str, Any]]] = "auto"
    max_tokens: Optional[int] = Field(default=1024, ge=1) # 최소 1 토큰
    temperature: Optional[float] = Field(default=0.5, ge=0.0, le=1.0)
    top_p: Optional[float] = Field(default=0.8, gt=0.0, le=1.0)
    top_k: Optional[int] = Field(default=0, ge=0, le=128)
    repetition_penalty: Optional[float] = Field(default=1.1, gt=0.0, le=2.0)
    stop: Optional[List[str]] = []
    stream: Optional[bool] = False

class Usage(BaseModel):
    prompt_tokens: int
    completion_tokens: int
    total_tokens: int

class ChatMessage(BaseModel):
    role: MessageRole
    content: str
    tool_calls: Optional[List[ToolCall]] = None

class ChatCompletionResponse(BaseModel):
    status: Dict[str, Any]
    result: Dict[str, Any]

# 간단한 채팅 요청/응답 (Function Calling 없이)
class SimpleChatRequest(BaseModel):
    message: str
    max_tokens: Optional[int] = 1024
    temperature: Optional[float] = 0.5

class SimpleChatResponse(BaseModel):
    response: str
    usage: Optional[Dict[str, int]] = None