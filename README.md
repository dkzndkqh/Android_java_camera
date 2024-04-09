CheckpermissionActivity.java -> 권한 승인 여부 ( 카메라 , 오디오 )

MainActivity.java -> 카메라 여부 판단과 사진 촬영 버튼, 동영상 촬영버튼 

CameraPreview.java  -> 카메라 화면 미리보기 설정, 생성

Gridlist.java -> 사진, 동영상 목록 사진이나 동영상을 처리 길게 누를시 다이얼로그 알람 후 삭제여부 판단

GridViewBase -> GridViewAdapter 설정 사진인지 영상인지 판단해 각각에 따른 그리드 뷰에 비트맵 썸네일로 나타나게 하고 목록에서 삭제시 업데이트

ImageView -> Gridlist에서 사진 클릭시 해당 사진을 띄우는 화면, 절대 경로값을 받아온다.

VideoPlayer -> 비디오 재생 화면, 절대 경로값을 받아온다.
