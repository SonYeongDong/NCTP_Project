package nctp_package;

// 문제 구성 요소(질문, 보기, 정답, 해설) 정보를 할당 및 추출하는 Class
public class NCTP_Problem {
	// [필드 선언]
    private String   question;   // 파싱된 질문을 담는 필드
    private String[] choices;    // 파싱된 보기를 담는 필드
    private int      answer;     // 파싱된 정답을 담는 필드
    private String   commentary; // 파싱된 해답을 담는 필드

    // [메소드 선언]
    // 생성자 (Problem 필드 초기화)
    public NCTP_Problem(String question, String[] choices, int answer, String commentary) { 
        this.question   = question;
        this.choices    = choices;
        this.answer     = answer;
        this.commentary = commentary;
    }

    // 질문 필드 반환
    public String getQuestion() {
        return question;
    }

    // 보기 필드 반환
    public String[] getChoices() { 
        return choices;
    }

    // 정답 필드 반환
    public int getAnswer() {
        return answer;
    }
    
    // 해답 필드 반환
    public String getCommentary() {
        return commentary;
    }

    // Demo Value 출력을 위한 함수 오버라이드
    public String toString() {
        String result = "";
        
        // 질문 result에 합치기
        result += "Question:\n" + question + "\n";
        
        // 보기 result에 합치기
        result += "Choices:\n";
        for (int i = 0; i < choices.length; i++) {
            if (choices[i] != null) {
                result += (i + 1) + ". " + choices[i] + "\n";
            }
        }
        
        // 정답 result에 합치기
        result += "Answer:\n" + answer + "\n";
        
        // 해설 result에 합치기
        result += "Commentary:\n" + commentary + "\n";
        
        return result;
    }
}
