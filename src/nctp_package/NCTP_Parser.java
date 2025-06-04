package nctp_package;

// 파일입출력을 위한 최소한의 import 추가
import java.io.FileReader;
import java.io.IOException;

// NCTP 텍스트 파싱 규칙에 맞춘 파서 Class
public class NCTP_Parser {
    // [상수 선언]
	private static final int  LINES_PER_PROBLEM_BLOCK = 7; // 문제 한개당 충족해야하는 요소 개수

    private static final int QUES_CHECK = 1;   // 질문 구분 부호 충족 개수
    private static final int CHOI_CHECK = 4;   // 보기 구분 부호 충족 개수
    private static final int ANS_CHECK  = 1;   // 정답 구분 부호 충족 개수
    private static final int CMT_CHECK  = 1;   // 해설 구분 부호 충족 개수
	
	private static final char QUES    = '-';   // 질문 구분 부호
    private static final char CHOI    = '+';   // 보기 구분 부호
    private static final char ANS     = '=';   // 정답 구분 부호
    private static final char CMT     = '?';   // 해설 구분 부호
    private static final char GR_ACC  = '`';   // 억음부호

    private static final int QUES_INDEX  = 0;   // 질문 인덱스
    private static final int ANS_INDEX   = 5;   // 정답 인덱스
    private static final int CMT_INDEX   = 6;   // 해설 인덱스
    
    // [필드 선언]
    private String filePath = ""; // 사용자가 지정한 파싱 파일 경로를 담는 필드
    private int questionCount = 0;
    private int choicesCount = 0;
    private int answerCount = 0;
    private int commentaryCount = 0;
    
	// 생성자 (파서 필드 초기화)
	public NCTP_Parser(String file_path){
		this.filePath = file_path;
		this.questionCount = 0;
		this.choicesCount = 0;
		this.answerCount = 0;
		this.commentaryCount = 0;
	}
	
	// 요소별 억음부호(`) 기준 문장 추출
	public String sentenceExtraction(FileReader fileReader) {
		String tempString = "";
		int tempWord = -1;
		
		try {
			tempWord = fileReader.read();
			if((char)tempWord != GR_ACC) { // 읽은 문자가 억음문자(`) 가 아니라면 빈 문자열 반환
				System.out.println("[문법 오류] : 기본 부호 뒤에는 억음문자(`)가 와야합니다");
				return null;
			}
			
			while(true) {
				tempWord = fileReader.read();
				if(tempWord == -1){
					System.out.println("[문법 오류] : 더 이상 읽을 문자가 없습니다");
					return null; // 정상적인 문법으로 구성된 TextFile이라면 파서에서 TextFile EOF에 도달할일이 없음 
				}

				if((char)tempWord == GR_ACC) { break;}
				tempString += (char)tempWord;
			}
		} catch (IOException error) {
            System.err.println("파일을 읽는 도중 오류가 발생했습니다 : " + error.getMessage());
            error.printStackTrace();
        }

		return tempString;
	}
	
	// 단일 문제 파싱
	public NCTP_Problem parseSingleProblem(String[] problemLines) {		
		if(questionCount != QUES_CHECK || choicesCount != CHOI_CHECK || answerCount != ANS_CHECK || commentaryCount != CMT_CHECK) {
			System.out.println("[문법 오류] : 구분 부호 개수가 일치하지 않습니다.");
			return null;
		}
		
		String question = problemLines[QUES_INDEX];
		
		String[] choices = new String[CHOI_CHECK];
        for(int i = 0; i < CHOI_CHECK; i++) {
        	choices[i] = problemLines[i+1];
        }
       
        Integer answer = null;
        if     (problemLines[ANS_INDEX].equals("1")) { answer = 1; }
        else if(problemLines[ANS_INDEX].equals("2")) { answer = 2; }
        else if(problemLines[ANS_INDEX].equals("3")) { answer = 3; }
        else if(problemLines[ANS_INDEX].equals("4")) { answer = 4; }
        else {
        	System.out.println("[문법 오류] : 정답 범위(1~4)가 아니거나 정답에 숫자 이외에 문자가 있습니다.");
        	return null;
        }
        
        String commentary = problemLines[CMT_INDEX];
        
		return new NCTP_Problem(question, choices, answer, commentary);
	}
	
	// 파서 핵심 함수로 파일 파싱 및 Problem 객체 배열 생성
    public NCTP_Problem[] parseProblems() {
    	NCTP_Problem[] dumyProblems = new NCTP_Problem[0]; // 오류시 더미로 반환할 배열
    	NCTP_Problem[] problems = new NCTP_Problem[0];     // 파싱된 문제들을 저장 및 반환하는 배열
        int problemsIndex = 0;
    	
        // 파일입출력 시작
        int currentLineIndex = 0;
        try (FileReader fileReader = new FileReader(filePath)) {
            int word;
            String[] currentProblemLines = new String[LINES_PER_PROBLEM_BLOCK];
            
            while((word = fileReader.read()) != -1) {            	
				if((char)word == QUES || (char)word == CHOI || (char)word == ANS || (char)word == CMT)
				{
					if     ((char)word == QUES) { questionCount++; }
					else if((char)word == CHOI) { choicesCount++; }
					else if((char)word == ANS)  { answerCount++; }
					else if((char)word == CMT)  { commentaryCount++; }
					
            		String tempString = sentenceExtraction(fileReader);
            		if(tempString == null) { return dumyProblems; }
            		currentProblemLines[currentLineIndex++] = tempString;
            		
            		if(currentLineIndex >= LINES_PER_PROBLEM_BLOCK) {
            			NCTP_Problem tempProblem = parseSingleProblem(currentProblemLines);
            			
            			if(tempProblem == null) {
            				return dumyProblems; }
            			
            			// Problems 자동 할당
            			if (problemsIndex >= problems.length) {
            				NCTP_Problem[] newProblems = new NCTP_Problem[problems.length + 1];
            				
                            for (int i = 0; i < problems.length; i++) {
                            	newProblems[i] = problems[i];
                            }
                            problems = newProblems;
            			}
            			problems[problemsIndex++] = tempProblem;
            			
            			// 인덱스 및 Count 변수 Reset
            			currentLineIndex = 0;
            			questionCount = 0;
            			choicesCount = 0;
            			answerCount = 0;
            			commentaryCount = 0;
            		}
				}            	
            }
        } catch (IOException error) {
            System.err.println("파일을 읽는 도중 오류가 발생했습니다: " + error.getMessage());
            error.printStackTrace();
        }
        
        if(currentLineIndex != 0 ) {
        	System.out.println("[문법 오류] : 문제가 온전히 구현되지 않았습니다(문제당 질문, 보기, 정답, 해설 요소가 반드시 포함되어야 합니다)");
        	return dumyProblems;
        }
        
        return problems;
    }
}