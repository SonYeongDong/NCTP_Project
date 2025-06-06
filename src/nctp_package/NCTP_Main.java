package nctp_package;

import java.util.*;

public class NCTP_Main {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            printTitle();
            String userName = greetUser(scanner);
            NCTP_Problem[] problems = loadProblems();

            if (problems.length == 0) {
                System.out.println(ANSI_RED + "⚠️ 문제가 존재하지 않거나 파싱에 실패했습니다." + ANSI_RESET);
                return;
            }

            int[] userAnswers = new int[problems.length];
            int correctCount = runTest(problems, scanner, userAnswers);

            printResult(userName, correctCount, problems.length);
            showFeedbackIfRequested(problems, userAnswers, scanner);
        }
    }

    static void printTitle() {
        System.out.println(ANSI_BLUE +
                "  _   _  ____ _____ ____     ___  _   _ ___ _____\n"
                + " | \\ | |/ ___|_   _|  _ \\   / _ \\| | | |_ _|__  /\n"
                + " |  \\| | |     | | | |_) | | | | | | | || |  / / \n"
                + " | |\\  | |___  | | |  __/  | |_| | |_| || | / /_ \n"
                + " |_| \\_|\\____| |_| |_|      \\__\\_\\\\___/|___/____|\n"
                + ANSI_RESET);

        System.out.println(ANSI_PURPLE + "🏢===========================================🏢");
        System.out.println("     신입사원 정규직 전환 테스트     ");
        System.out.println("🏢===========================================🏢" + ANSI_RESET);
    }

    static String greetUser(Scanner scanner) {
        System.out.print("🙋 이름을 입력해주세요: ");
        String userName = scanner.nextLine().trim();

        System.out.println("\n🎉 " + userName + "님, 입사 축하드립니다!");
        System.out.println("❌ 문제를 틀릴 때마다 정규직 전환 확률이 줄어듭니다.");
        System.out.println("🧪 모든 평가를 통과하면 정규직 전환에 성공합니다.");
        System.out.println("👉 엔터 키를 누르면 테스트를 시작합니다...");
        scanner.nextLine();
        return userName;
    }

    static NCTP_Problem[] loadProblems() {
        NCTP_Parser parser = new NCTP_Parser("Asset/Problem_Set.txt");
        NCTP_Problem[] problems = parser.parseProblems();

        Random rand = new Random();
        for (int i = problems.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            NCTP_Problem temp = problems[i];
            problems[i] = problems[j];
            problems[j] = temp;
        }

        return problems;
    }

    static int runTest(NCTP_Problem[] problems, Scanner scanner, int[] userAnswers) {
        int correctCount = 0;

        for (int i = 0; i < problems.length; i++) {
            NCTP_Problem p = problems[i];
            System.out.println("📌 문제 " + (i + 1) + " / " + problems.length);
            System.out.println("❓ " + p.getQuestion());

            String[] choices = p.getChoices();
            for (int j = 0; j < choices.length; j++) {
                System.out.println((j + 1) + ". " + choices[j]);
            }

            int userAnswer = -1;
            while (true) {
                System.out.print("\n🔢 정답 입력 (1~4): ");
                try {
                    userAnswer = Integer.parseInt(scanner.nextLine().trim());
                    if (userAnswer < 1 || userAnswer > 4) throw new Exception();
                    break;
                } catch (Exception e) {
                    System.out.println(ANSI_RED + "🚫 1에서 4 사이의 숫자만 입력해주세요!" + ANSI_RESET);
                }
            }

            userAnswers[i] = userAnswer;
            if (userAnswer == p.getAnswer()) correctCount++;

            int progress = (int) (((i + 1) * 100.0) / problems.length);
            int barLength = 20;
            int filledLength = progress * barLength / 100;
            StringBuilder barBuilder = new StringBuilder();
            barBuilder.append("[");

            for (int j = 0; j < barLength; j++) {
                if (j < filledLength) {
                    barBuilder.append("#");
                } else {
                    barBuilder.append(" ");
                }
            }
            barBuilder.append("]");

            System.out.println("📊 진행률: " + ANSI_CYAN + barBuilder + " " + progress + "%" + ANSI_RESET);
            System.out.println("===========================================\n");
        }

        return correctCount;
    }

    static void printResult(String userName, int correctCount, int totalQuestions) {
        int score = (int) ((correctCount * 100.0) / totalQuestions);

        System.out.println("📝=== 최종 평가 결과 ===📝");
        System.out.println("👤 평가 대상자: " + userName);
        System.out.println("📈 정답 수: " + correctCount + " / " + totalQuestions);
        System.out.println("📊 정답률: " + score + "%");

        if (score == 100) {
            System.out.println(ANSI_GREEN + "🏆 만점입니다. 이 정도면 바로 정규직 확정입니다. 축하드립니다!" + ANSI_RESET);
        } else if (score >= 70) {
            System.out.println(ANSI_CYAN + "👍 기준 이상은 넘겼습니다. 정규직 전환 가능성 높지만, 추가 피드백은 꼭 반영하세요." + ANSI_RESET);
        } else if (score >= 40) {
            System.out.println(ANSI_YELLOW + "😐 아슬아슬합니다. 아직 부족한 점이 많습니다. 교육 연장 또는 재평가 대상입니다." + ANSI_RESET);
        } else {
            System.out.println(ANSI_RED + "❌ 기준 미달입니다. 현재 상태로는 정규직 전환이 어렵습니다. 처음부터 다시 준비하세요." + ANSI_RESET);
        }
    }

    static void showFeedbackIfRequested(NCTP_Problem[] problems, int[] userAnswers, Scanner scanner) {
        System.out.println("\n📌 정답과 해설을 보고 싶다면 'Y'를 입력하세요. (그 외 입력 시 종료): ");
        String input = scanner.nextLine().trim().toUpperCase();

        if (!input.equals("Y")) return;

        System.out.println("\n📋 평가 피드백 (틀린 문제 복습):");
        for (int i = 0; i < problems.length; i++) {
            if (userAnswers[i] != problems[i].getAnswer()) {
                System.out.println("❓ " + problems[i].getQuestion());
                System.out.println(ANSI_RED + "❌ 당신의 선택: " + userAnswers[i] + ANSI_RESET);
                System.out.println(ANSI_GREEN + "✅ 정답: " + problems[i].getAnswer() + ANSI_RESET);
                System.out.println("📘 피드백: " + problems[i].getCommentary());
                System.out.println("-----------------------------");
            }
        }

        System.out.println(ANSI_PURPLE + "\n🏢===========================================🏢" + ANSI_RESET);
    }
}