package egovframework.com.cmm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * DB나 Spring 컨텍스트 없이 jsoup 1.18.3 기반 정제 결과를 고정한다.
 * jsoup 버전 변경 시에는 문자열 출력 차이와 보안 정책을 함께 검토한다.
 */
class EgovHtmlSanitizerGoldenTest {

	static Stream<Arguments> goldenCases() {
		return Stream.of(
				Arguments.of("허용 태그와 텍스트 유지",
						"<p>본문 <strong>굵게</strong> <em>강조</em></p>",
						"<p>본문 <strong>굵게</strong> <em>강조</em></p>"),
				Arguments.of("script 태그와 스크립트 본문 제거",
						"<script>alert('xss')</script><strong>안전</strong>",
						"<strong>안전</strong>"),
				Arguments.of("이벤트 핸들러 제거",
						"<img src=\"/images/sample.png\" onload=\"alert(1)\" onerror=\"alert(2)\">",
						"<img src=\"/images/sample.png\">"),
				Arguments.of("링크의 javascript 프로토콜 제거",
						"<a href=\"javascript:alert(1)\" onclick=\"alert(2)\">링크</a>",
						"<a>링크</a>"),
				Arguments.of("이미지의 javascript 프로토콜 제거",
						"<img src=\"javascript:alert(1)\" alt=\"안전\">",
						"<img alt=\"안전\">"),
				Arguments.of("컨텍스트 상대 이미지 경로 유지",
						"<img src=\"/utl/web/imageSrc.do?atchFileId=FILE_1&amp;fileSn=0\" alt=\"사진\">",
						"<img src=\"/utl/web/imageSrc.do?atchFileId=FILE_1&amp;fileSn=0\" alt=\"사진\">"),
				Arguments.of("컨텍스트 상대 링크 경로 유지",
						"<a href=\"/cop/bbs/selectArticleDetail.do?nttId=1\">게시글</a>",
						"<a href=\"/cop/bbs/selectArticleDetail.do?nttId=1\">게시글</a>"),
				Arguments.of("이미지 style에서 width와 height만 유지",
						"<img src=\"/images/sample.png\" style=\"color: red; width: 120px; "
								+ "background-image: url(javascript:alert(1)); height: 50%; position: fixed\">",
						"<img src=\"/images/sample.png\" style=\"width: 120px; height: 50%\">"),
				Arguments.of("허용 크기 선언의 대소문자와 단위 없는 값 유지",
						"<img style=\" WIDTH : 12PX ; height:42; width:10000px; height:-1px; "
								+ "width:1.5px; height:expression(alert(1)) \">",
						"<img style=\"WIDTH : 12PX; height:42\">"),
				Arguments.of("허용 크기 선언이 없는 이미지 style 제거",
						"<img src=\"/images/sample.png\" style=\"color: red; width: auto; height: calc(100%)\">",
						"<img src=\"/images/sample.png\">"),
				Arguments.of("이미지 외 태그의 style 제거",
						"<p style=\"width: 120px; height: 50px\">본문</p>",
						"<p>본문</p>"));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("goldenCases")
	void matchesCurrentSanitizedHtml(String description, String input, String expected) {
		assertEquals(expected, EgovHtmlSanitizer.sanitize(input), description);
	}
}
