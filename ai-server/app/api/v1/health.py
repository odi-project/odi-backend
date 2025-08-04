from fastapi import APIRouter
from app.schemas.common import APIResponse

router = APIRouter(prefix="/health", tags=["Health"])

@router.get("/", response_model=APIResponse)
async def health_check():
    """서버 상태 확인"""
    return APIResponse(
        success=True,
        message="서버가 정상적으로 작동중입니다.",
        data={"status": "healthy"}
    )


@router.get("/clova")
async def clova_health_check():
    """클로바스튜디오 연결 상태 확인"""
    try:
        from app.services.clova_studio import clova_service

        # 간단한 테스트 요청
        response = await clova_service.simple_chat("안녕하세요", max_tokens=10)

        return APIResponse(
            success=True,
            message="클로바스튜디오 연결 정상",
            data={"clova_response": response}
        )
    except Exception as e:
        return APIResponse(
            success=False,
            message=f"클로바스튜디오 연결 실패: {str(e)}"
        )