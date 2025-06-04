package nctp_package;

public class NCTP_Main {
	public static void main(String[] args) {
		System.out.println("Please Wrtie NCTP Main Logic!");
		System.out.println("NCTP Parser Demo Logic!");
		System.out.println("");
		
		NCTP_Parser testParser = new NCTP_Parser("Asset/Problem_Set.txt");
		NCTP_Problem[] testProblems = testParser.parseProblems();
		
        if (testProblems.length == 0) {
            System.out.println("파싱할 문자가 없거나 문법 오류로 파싱에 실패했습니다.");
        } else {
            System.out.println("전체 문제 개수 : " + testProblems.length);
            System.out.println("");
            
            for (int i = 0; i < testProblems.length; i++) {
                System.out.println(">>>>> Problem " + (i + 1) + " >>>>>");
                System.out.println(testProblems[i]);
            }
        }
	}
}