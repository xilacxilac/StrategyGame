import java.lang.reflect.Array;
import java.util.*;

public class StrategyGame {
	public static void main(String[] args) {
		// Config
		Scanner input = new Scanner(System.in);
		Random rand = new Random();

		// Numbers corresponding with each position
		int[] boardnums = new int[25];

		// Board with placements
		char[] board = new char[25];
		board = board(board, boardnums);

		// Movements open to each position
		String[] movements = new String[25];
		movements = movementsetup(movements);

		// First Move
		String[] firstmove = { "Player", "Computer", "Random" };
		int firstcounter = 0;

		// Bot Traits
		// Group up and attack)
		String[] traits = { "Aggressive", "Passive", "Defensive", "Smart", "Random" };
		int traitscounter = 0;

		// Difficulty
		String[] difficulties = { "Easy", "Medium", "Hard", "Extreme" };
		int difficultycounter = 1;

		int[] mastersettings = { traitscounter, difficultycounter, firstcounter };
		mastersettings = mainscreen(mastersettings, boardnums, board, traits, difficulties, firstmove, traitscounter,
				difficultycounter, firstcounter);

		// Difficulty (Easy: 3 v 2; Medium: 3 v 4; Hard: 4 v 6; Extremely: 4 v 8)
		// Board Setup
		int[] enemy = new int[(mastersettings[1] + 1) * 2];
		int[] allied = new int[3 + (mastersettings[1] / 3)];

		for (int i = 0; i < enemy.length; i++) {
			enemy[i] = rand.nextInt(10) + 1;
			for (int k = 0; k < i; k++) {
				if (enemy[i] == enemy[k]) {
					enemy[i] = rand.nextInt(10) + 1;
					;
					k = -1;
				}
			}

			board[enemy[i] - 1] = 'O';

		}
		
		System.out.println("\n");
		for (int i = 0; i < allied.length; i++) {
			System.out.println("You will have " + allied.length + " units against " + enemy.length + " enemies.\n");
			printBoardNumbers(boardnums);
			System.out.print("\n\nInput a position for unit " + (i + 1) + " using numbers 16 - 25 according to the map: ");
			allied[i] = input.nextInt();

			// Illegal Position Error
			while (allied[i] < 16 || allied[i] > 25) {
				System.out.println("Error: illegal position");
				System.out.print("Please input a new position: ");
				allied[i] = input.nextInt();
			}

			// Spot Taken Error
			for (int k = 0; k < i; k++) {
				if (allied[i] == allied[k]) {
					System.out.println("Error: spot taken");
					System.out.print("Please input a new position: ");
					allied[i] = input.nextInt();
					k = -1;
				}
			}

			board[allied[i] - 1] = 'X';
			System.out.println("\n\n\n\n\n");
			
		}

		System.out.println("\n\n");
		
		// Randomization Settings
		if (firstcounter % 3 == 2) {
			mastersettings[2] = rand.nextInt(2);
		}

		if (traitscounter % 5 == 4) {
			mastersettings[0] = rand.nextInt(4);
		}

		// Gameplay
		boolean cont = true;
		int turns = 0;
		while (cont) {
			turns++;
			
			// Movements + Combat
			// Player First
			if (mastersettings[2] == 0) {
				playermovement(allied, movements, board, turns);
				combat(enemy, movements, board);
				combat(allied, movements, board);
				computermovement(allied, enemy, movements, board, turns, mastersettings[0]);
				combat(allied, movements, board);
				combat(enemy, movements, board);
				cont = end(allied, enemy);
				
			}

			// Computer First
			else {
				computermovement(allied, enemy, movements, board, turns, mastersettings[0]);
				combat(enemy, movements, board);
				combat(allied, movements, board);
				playermovement(allied, movements, board, turns);
				combat(allied, movements, board);
				combat(enemy, movements, board);
				cont = end(allied, enemy);
				
			}

			System.out.println("");
			
			// End of Game

		}
	}
	
	public static boolean end (int[] allied, int[] enemy) {
		if (winner(allied, enemy) == 2) {
			System.out.println("The Player won!");
			return false;
		}
		
		else if (winner(allied, enemy) == 1) {
			System.out.println("The Player lost!");
			return false;
		}
		
		else {
			return true;
		}
	}
	
	public static int winner (int[] allied, int[] enemy) {
		Arrays.sort(allied);
		Arrays.sort(enemy);
		if (allied[0] == 26) {
			return 1;
		}
		
		if (enemy[0] == 26) {
			return 2;
		}
		
		return 0;
		
	}
	
	public static void combat (int[] p1, String[] movements, char[] board) {
		Arrays.sort(p1);
		int[] aclose = new int[p1.length];
		char[] boardcopy = new char[board.length];
		String move = "";
		for (int i = 0; i < board.length; i++) {
			boardcopy[i] = board[i];
		}
		
		int counter = 0;
		for (int i = 0; i < p1.length; i++) {
			if (p1[i] == 26) {
				counter++;
			}
		}
		
		for (int i = 0; i < p1.length - counter; i++) {
			boolean afk = false;
			int hold = i;
			if (boardcopy[p1[i] - 1] != '-') {
				aclose[i] = 0;
				if (movements[p1[i] - 1].equals("br")) {
					aclose[i]+=east(p1, boardcopy, move, hold, false) + southeast(p1, boardcopy, move, hold, false) + south(p1, boardcopy, move, hold, false) + 10;
				
					if ((aclose[i] - 10) / 10 + (aclose[i] - 10) % 10 == 3) {
						afk = true;
					}				
				}

				else if (movements[p1[i] - 1].equals("b")) {
					aclose[i]+=east(p1, boardcopy, move, hold, false) + southeast(p1, boardcopy, move, hold, false) + south(p1, boardcopy, move, hold, false) + 
							southwest(p1, boardcopy, move, hold, false) + west(p1, boardcopy, move, hold, false) + 10;

					if ((aclose[i] - 10) / 10 + (aclose[i] - 10) % 10 == 5) {
						afk = true;
					}					
				}

				else if (movements[p1[i] - 1].equals("bl")) {
					aclose[i]+=south(p1, boardcopy, move, hold, false) + southwest(p1, boardcopy, move, hold, false) + west(p1, boardcopy, move, hold, false) + 10;

					if ((aclose[i] - 10) / 10 + (aclose[i] - 10) % 10 == 3) {
						afk = true;
					}
				}

				else if (movements[p1[i] - 1].equals("tr")) {
					aclose[i]+=north(p1, boardcopy, move, hold, false) + northeast(p1, boardcopy, move, hold, false) + east(p1, boardcopy, move, hold, false) + 10;

					if ((aclose[i] - 10) / 10 + (aclose[i] - 10) % 10 == 3) {
						afk = true;
					}
				}

				else if (movements[p1[i] - 1].equals("t")) {
					aclose[i]+=west(p1, boardcopy, move, hold, false) + northwest(p1, boardcopy, move, hold, false) + north(p1, boardcopy, move, hold, false) + 
							northeast(p1, boardcopy, move, hold, false) + east(p1, boardcopy, move, hold, false) + 10;

					if ((aclose[i] - 10) / 10 + (aclose[i] - 10) % 10 == 5) {
						afk = true;
					}
				}

				else if (movements[p1[i] - 1].equals("tl")) {
					aclose[i]+=west(p1, boardcopy, move, hold, false) + northwest(p1, boardcopy, move, hold, false) + north(p1, boardcopy, move, hold, false) + 10;

					if ((aclose[i] - 10) / 10 + (aclose[i] - 10) % 10 == 3) {
						afk = true;
					}
				}

				else if (movements[p1[i] - 1].equals("r")) {
					aclose[i]+=north(p1, boardcopy, move, hold, false) + northeast(p1, boardcopy, move, hold, false) + east(p1, boardcopy, move, hold, false) + 
							southeast(p1, boardcopy, move, hold, false) + south(p1, boardcopy, move, hold, false) + 10;

					if ((aclose[i] - 10) / 10 + (aclose[i] - 10) % 10 == 5) {
						afk = true;
					}
				}

				else if (movements[p1[i] - 1].equals("l")) {
					aclose[i]+=south(p1, boardcopy, move, hold, false) + southwest(p1, boardcopy, move, hold, false) + west(p1, boardcopy, move, hold, false) + 
							northwest(p1, boardcopy, move, hold, false) + north(p1, boardcopy, move, hold, false) + 10;

					if ((aclose[i] - 10) / 10 + (aclose[i] - 10) % 10 == 5) {
						afk = true;
					}
				}

				else if (movements[p1[i] - 1].equals("a")) {
					aclose[i]+=north(p1, boardcopy, move, hold, false) + northeast(p1, boardcopy, move, hold, false) + east(p1, boardcopy, move, hold, false) +
							southeast(p1, boardcopy, move, hold, false) + south(p1, boardcopy, move, hold, false) + southwest(p1, boardcopy, move, hold, false) +
							west(p1, boardcopy, move, hold, false) + northwest(p1, boardcopy, move, hold, false) + 10;

					if ((aclose[i] - 10) / 10 + (aclose[i] - 10) % 10 == 8) {
						afk = true;
					}
				}
			
				if (afk) {
					boardcopy[p1[i] - 1] = '-';
					i = -1;
				}
			}
		}
		
		for (int i = 0; i < p1.length - counter; i++) {
			if (aclose[i] <= 0) {
				board[p1[i] - 1] = '-';
				p1[i] = 26;
			}
		}
	}
	
	public static void computermovement(int[] allied, int[] enemy, String[] movements, char[] board, int turns, int traits) {
		// Not done
		// {"Aggressive", "Passive", "Defensive", "Smart", "Random"}
		// 1  2  3  4  5
		// 6  7  8  9  10
		// 11 12 13 14 15
		// 16 17 18 19 20
		// 21 22 23 24 25
		Random rand = new Random();
		
		// Difficulty (Easy: 3 v 2; Medium: 3 v 4; Hard: 4 v 6; Extremely: 4 v 8)
		// Aggressive, charges at the enemy without grouping up
		if (traits == 0) {
			
		}
		
		// Passive, retreat to the closest corner or side
		else if (traits == 1) {
			
		}
		
		// Defensive, makes a defensive line then stays still
		else if (traits == 2) {
			
		}
		
		// Smart, groups up then moves and attacks
		else if (traits == 3) {
			
		}
		
		for (int i = 0; i < enemy.length; i++) {
			
		}
	}

	public static void playermovement(int[] allied, String[] movements, char[] board, int turns) {
		Scanner input = new Scanner(System.in);
		String move;
		int counter = 0;
		for (int i = 0; i < allied.length; i++) {
			if (allied[i] == 26) {
				counter++;
			}
		}
		
		for (int i = 0; i < allied.length - counter; i++) {
			int hold = i;
			System.out.println("\nTurn " + turns + ": Player's Move " + (i + 1) + "/" + (allied.length - counter) + "\n");
			if (movements[allied[i] - 1].equals("br")) {
				printBoard(board);
				System.out.print(
						"\n\nMoving Unit at " + allied[i] + " East, Southeast, South, or Anything Else to Hold Position: ");
				move = input.next();
				hold = east(allied, board, move, hold, true);
				hold = southeast(allied, board, move, hold, true);
				hold = south(allied, board, move, hold, true);
			}

			else if (movements[allied[i] - 1].equals("b")) {
				printBoard(board);
				System.out.print("\n\nMoving Unit at " + allied[i]
						+ " East, Southeast, South, Southwest, West or Anything Else to Hold Position: ");
				move = input.next();
				hold = east(allied, board, move, hold, true);
				hold = southeast(allied, board, move, hold, true);
				hold = south(allied, board, move, hold, true);
				hold = southwest(allied, board, move, hold, true);
				hold = west(allied, board, move, hold, true);
			}

			else if (movements[allied[i] - 1].equals("bl")) {
				printBoard(board);
				System.out.print(
						"\n\nMoving Unit at " + allied[i] + " South, Southwest, West, or Anything Else to Hold Position: ");
				move = input.next();
				hold = south(allied, board, move, hold, true);
				hold = southwest(allied, board, move, hold, true);
				hold = west(allied, board, move, hold, true);
			}

			else if (movements[allied[i] - 1].equals("tr")) {
				printBoard(board);
				System.out.print(
						"\n\nMoving Unit at " + allied[i] + " North, Northeast, East, or Anything Else to Hold Position: ");
				move = input.next();
				hold = north(allied, board, move, hold, true);
				hold = northeast(allied, board, move, hold, true);
				hold = east(allied, board, move, hold, true);
			}

			else if (movements[allied[i] - 1].equals("t")) {
				printBoard(board);
				System.out.print("\n\nMoving Unit at " + allied[i]
						+ " West, Northwest, North, Northeast, East, or Anything Else to Hold Position: ");
				move = input.next();
				hold = west(allied, board, move, hold, true);
				hold = northwest(allied, board, move, hold, true);
				hold = north(allied, board, move, hold, true);
				hold = northeast(allied, board, move, hold, true);
				hold = east(allied, board, move, hold, true);
			}

			else if (movements[allied[i] - 1].equals("tl")) {
				printBoard(board);
				System.out.print(
						"\n\nMoving Unit at " + allied[i] + " West, Northwest, North, or Anything Else to Hold Position: ");
				move = input.next();
				hold = west(allied, board, move, hold, true);
				hold = northwest(allied, board, move, hold, true);
				hold = north(allied, board, move, hold, true);
			}

			else if (movements[allied[i] - 1].equals("r")) {
				printBoard(board);
				System.out.print("\n\nMoving Unit at " + allied[i]
						+ " North, Northeast, East, Southeast, South, or Anything Else to Hold Position: ");
				move = input.next();
				hold = north(allied, board, move, hold, true);
				hold = northeast(allied, board, move, hold, true);
				hold = east(allied, board, move, hold, true);
				hold = southeast(allied, board, move, hold, true);
				hold = south(allied, board, move, hold, true);
			}

			else if (movements[allied[i] - 1].equals("l")) {
				printBoard(board);
				System.out.print("\n\nMoving Unit at " + allied[i]
						+ " South, Southwest, West, Northwest, North, or Anything Else to Hold Position: ");
				move = input.next();
				hold = south(allied, board, move, hold, true);
				hold = southwest(allied, board, move, hold, true);
				hold = west(allied, board, move, hold, true);
				hold = northwest(allied, board, move, hold, true);
				hold = north(allied, board, move, hold, true);
			}

			else if (movements[allied[i] - 1].equals("a")) {
				printBoard(board);
				System.out.print("\n\nMoving Unit at " + allied[i]
						+ " North, Northeast, East, Southeast, South, Southwest, West, Northwest, or Anything Else to Hold Position: ");
				move = input.next();
				hold = north(allied, board, move, hold, true);
				hold = northeast(allied, board, move, hold, true);
				hold = east(allied, board, move, hold, true);
				hold = southeast(allied, board, move, hold, true);
				hold = south(allied, board, move, hold, true);
				hold = southwest(allied, board, move, hold, true);
				hold = west(allied, board, move, hold, true);
				hold = northwest(allied, board, move, hold, true);
			}
			
			i = hold;
			
			if (i == allied.length - 1) {
				System.out.println("End of Turn " + turns + " for the Player\n");
				printBoard(board);
				System.out.println("");
			}
		}
	}

	public static int north(int[] allied, char[] board, String move, int num, boolean error) {
		if (move.equalsIgnoreCase("north") && error) {
			if (board[allied[num] - 5 - 1] == 'X') {
				System.out.println("Error: spot taken");
				return num - 1;
			}

			else {
				board[allied[num] - 1] = '-';
				allied[num] -= 5;
				board[allied[num] - 1] = 'X';
				return num;
			}
		}
		
		if (!error) {
			if (board[allied[num] - 5 - 1] == board[allied[num] - 1]) {
				return 10;
			}
			
			else if (board[allied[num] - 5 - 1] == '-') {
				return 1;
			}
			
			else {
				return -10;
			}
		}
		
		return num;

	}

	public static int northeast(int[] allied, char[] board, String move, int num, boolean error) {
		if (move.equalsIgnoreCase("northeast") && error) {
			if (board[allied[num] - 4 - 1] == 'X') {
				System.out.println("Error: spot taken");
				return num - 1;
			}

			else {
				board[allied[num] - 1] = '-';
				allied[num] -= 4;
				board[allied[num] - 1] = 'X';
				return num;
			}
		}
		

		if (!error) {
			if (board[allied[num] - 4 - 1] == board[allied[num] - 1]) {
				return 10;
			}
			
			else if (board[allied[num] - 4 - 1] == '-') {
				return 1;
			}
			
			else {
				return -10;
			}
		}
		
		return num;

	}

	public static int east(int[] allied, char[] board, String move, int num, boolean error) {
		if (move.equalsIgnoreCase("east") && error) {
			if (board[allied[num] + 1 - 1] == 'X') {
				System.out.println("Error: spot taken");
				return num - 1;
			}

			else {
				board[allied[num] - 1] = '-';
				allied[num] += 1;
				board[allied[num] - 1] = 'X';
				return num;
			}
		}
		
		if (!error) {
			if (board[allied[num] + 1 - 1] == board[allied[num] - 1]) {
				return 10;
			}
			
			else if (board[allied[num] + 1 - 1] == '-') {
				return 1;
			}
			
			else {
				return -10;
			}
		}
		
		return num;

	}

	public static int southeast(int[] allied, char[] board, String move, int num, boolean error) {
		if (move.equalsIgnoreCase("southeast") && error) {
			if (board[allied[num] + 6 - 1] == 'X') {
				System.out.println("Error: spot taken");
				return num - 1;
			}

			else {
				board[allied[num] - 1] = '-';
				allied[num] += 6;
				board[allied[num] - 1] = 'X';
				return num;
			}
		}

		if (!error) {
			if (board[allied[num] + 6 - 1] == board[allied[num] - 1]) {
				return 10;
			}
			
			else if (board[allied[num] + 6 - 1] == '-') {
				return 1;
			}
			
			else {
				return -10;
			}
		}

		return num;

	}

	public static int south(int[] allied, char[] board, String move, int num, boolean error) {
		if (move.equalsIgnoreCase("south") && error) {
			if (board[allied[num] + 5 - 1] == 'X') {
				System.out.println("Error: spot taken");
				return num - 1;
			}

			else {
				board[allied[num] - 1] = '-';
				allied[num] += 5;
				board[allied[num] - 1] = 'X';
				return num;
			}
		}

		if (!error) {
			if (board[allied[num] + 5 - 1] == board[allied[num] - 1]) {
				return 10;
			}
			
			else if (board[allied[num] + 5 - 1] == '-') {
				return 1;
			}
			
			else {
				return -10;
			}
		}

		return num;

	}

	public static int southwest(int[] allied, char[] board, String move, int num, boolean error) {
		if (move.equalsIgnoreCase("southwest") && error) {
			if (board[allied[num] + 4 - 1] == 'X') {
				System.out.println("Error: spot taken");
				return num - 1;
			}

			else {
				board[allied[num] - 1] = '-';
				allied[num] += 4;
				board[allied[num] - 1] = 'X';
				return num;
			}
		}

		if (!error) {
			if (board[allied[num] + 4 - 1] == board[allied[num] - 1]) {
				return 10;
			}
			
			else if (board[allied[num] + 4 - 1] == '-') {
				return 1;
			}
			
			else {
				return -10;
			}
		}

		return num;

	}

	public static int west(int[] allied, char[] board, String move, int num, boolean error) {
		if (move.equalsIgnoreCase("west") && error) {
			if (board[allied[num] - 1 - 1] == 'X') {
				System.out.println("Error: spot taken");
				return num - 1;
			}

			else {
				board[allied[num] - 1] = '-';
				allied[num] -= 1;
				board[allied[num] - 1] = 'X';
				return num;
			}
		}

		if (!error) {
			if (board[allied[num] - 1 - 1] == board[allied[num] - 1]) {
				return 10;
			}
			
			else if (board[allied[num] - 1 - 1] == '-') {
				return 1;
			}
			
			else {
				return -10;
			}
		}

		return num;

	}

	public static int northwest (int[] allied, char[] board, String move, int num, boolean error) {
		if (move.equalsIgnoreCase("northwest") && error) {
			if (board[allied[num] - 6 - 1] == 'X') {
				System.out.println("Error: spot taken");
				return num - 1;
			}

			else {
				board[allied[num] - 1] = '-';
				allied[num] -= 6;
				board[allied[num] - 1] = 'X';
				return num;
			}
		}

		if (!error) {
			if (board[allied[num] - 6 - 1] == board[allied[num] - 1]) {
				return 10;
			}
			
			else if (board[allied[num] - 6 - 1] == '-') {
				return 1;
			}
			
			else {
				return -10;
			}
		}

		return num;

	}

	public static void tutorial(String usertutorial, int[] boardnums, char[] board) {
		Scanner input = new Scanner(System.in);
		if (usertutorial.equalsIgnoreCase("tutorial")) {
			System.out.println("Welcome to Strategy 5x5\n");
			System.out.println("The goal is to elimate all enemy units on the board.");
			System.out.println("Your units are represented by the symbol X.");
			System.out.println("While enemy units are represented by O.\n\n\n\n");
			System.out.print("Press enter to continue");
			String x = input.nextLine();
			System.out.println("\n");
			System.out.println("This is the empty board with hyphens (-) represented unoccupied spaces.\n");
			printBoard(board);

			System.out.print("\n\nPress enter to continue");
			x = input.nextLine();
			System.out.println("");
			System.out.println("");
			System.out.println("These are the values corresponded to each position on the board.\n");
			printBoardNumbers(boardnums);

			System.out.print("\n\nPress enter to continue");
			x = input.nextLine();
			System.out.println("\n");
			System.out.println("Units can only move 1 unit in any direction and cannot move onto another unit.\n");
			System.out.println("If your unit borders an enemy unit, they will attack each other.");
			System.out.println("Whoever has more allies bordering that enemy unit will win.");
			System.out.println("\nIf there is an equal amount, they will both get elimated.\n\n\n");
			System.out.print("Press enter to continue");
			x = input.nextLine();

			System.out.println("\n\n");
			System.out.println("Heres an example in a 3x3 board\n");
			System.out.println("-\t-\tO");
			System.out.println("O\t-\t-");
			System.out.println("X\tX\t-");
			System.out.println("");
			System.out.println("In this example, you would win because it is 2 to 1 in the bottom left corner\n\n");
			System.out.print("Press enter to continue");
			x = input.nextLine();
			
			System.out.println("\n\n[Troubleshooting]");
			System.out.print("Why is nothing happening when I input something? ");
			System.out.println("Nothing is happening either because you inputted something the program cannot read,");
			System.out.println("or there was a Spot Taken Error and you need to input another movement command.");
			System.out.print("\nWhy does the game crash? ");
			System.out.println("One of the most frequent causes of crashing is caused by inputting a wrong variable.");
			System.out.print("\nWhy is the game not listening to my commands? ");
			System.out.println("The game is listening to your commands, but you have to spell the commands right.");
			System.out.println("Say you wanted to move your unit north, you would have to type north. Spelled correctly.");
			System.out.print("\nPress enter to end the tutorial: ");
			x = input.nextLine();
		}
	}

	public static String[] movementsetup(String[] movements) {
		for (int i = 0; i < 5; i++) {
			movements[i] = "b";
		}

		for (int i = 20; i < 25; i++) {
			movements[i] = "t";
		}

		for (int i = 0; i < 5; i++) {
			if (movements[i * 5] == null) {
				movements[i * 5] = "r";
			}

			else {
				movements[i * 5] += "r";
			}
		}

		for (int i = 0; i < 5; i++) {
			if (movements[4 + i * 5] == null) {
				movements[4 + i * 5] = "l";
			}

			else {
				movements[4 + i * 5] += "l";
			}
		}

		for (int i = 0; i < 25; i++) {
			if (movements[i] == null) {
				movements[i] = "a";
			}
		}

		return movements;
	}

	public static char[] board(char[] board, int[] boardnums) {
		for (int i = 0; i < boardnums.length; i++) {
			boardnums[i] = i + 1;
			board[i] = '-';
		}

		return board;

	}

	public static int[] mainscreen(int[] mastersettings, int[] boardnums, char[] board, String[] traits,
			String[] difficulties, String[] firstmove, int traitscounter, int difficultycounter, int firstcounter) {
		// Intro
		Scanner input = new Scanner(System.in);
		String userstart = "";
		String usersettings;

		while (!userstart.equalsIgnoreCase("play")) {
			System.out.println("[Strategy 5x5]");
			System.out.println("--------------");
			System.out.println("by Calix Tran-Luu\n");
			System.out.println("Tutorial");
			System.out.println("Play");
			System.out.println("Settings\nRecommend Console Size: 10 Lines\n");
			userstart = input.next();
			System.out.println("\n");

			// Tutorial
			tutorial(userstart, boardnums, board);
			System.out.println("\n\n\n\n\n");

			// Settings
			while (userstart.equalsIgnoreCase("settings")) {
				// Change Settings
				System.out.println("[Settings]");
				System.out.println("Input what you want to change or input anything else to leave");
				System.out.println("Traits: " + traits[traitscounter % 5]);
				System.out.println("Difficulty: " + difficulties[difficultycounter % 4]);
				System.out.println("First: " + firstmove[firstcounter % 3]);
				System.out.println("");
				System.out.println("Quit\n\n");
				usersettings = input.next();
				if (usersettings.equalsIgnoreCase("quit")) {
					userstart = "quit";
				}

				// Traits
				while (usersettings.equalsIgnoreCase("traits") || usersettings.equalsIgnoreCase("next")) {
					traitscounter++;
					System.out.println("\n\n\n\n\n");
					System.out.println("[Settings]\n");
					System.out.println("Type next or traits again to change the trait of the bot");
					System.out.println("Traits: " + traits[traitscounter % 5]);
					System.out.println("\n\n\n\n");
					usersettings = input.next();
				}

				mastersettings[0] = traitscounter % 5;

				// Difficulty
				while (usersettings.equalsIgnoreCase("difficulty") || usersettings.equalsIgnoreCase("next")) {
					difficultycounter++;
					System.out.println("\n\n\n\n\n");
					System.out.println("Settings\n");
					System.out.println("Type next or difficulty again to change the difficulty of the game");
					System.out.println("Difficulty: " + difficulties[difficultycounter % 4]);
					System.out.println("\n\n\n\n");
					usersettings = input.next();
				}

				mastersettings[1] = difficultycounter % 4;

				// First Move

				while (usersettings.equalsIgnoreCase("first") || usersettings.equalsIgnoreCase("next")) {
					firstcounter++;
					System.out.println("\n\n\n\n\n");
					System.out.println("Settings\n");
					System.out.println("Type next or first again to change who gets the first move ");
					System.out.println("First: " + firstmove[firstcounter % 3]);
					System.out.println("\n\n\n\n");
					usersettings = input.next();
				}

				mastersettings[2] = firstcounter % 3;

			}

			System.out.println("\n\n\n\n\n");

		}

		return mastersettings;

	}

	public static void printBoardNumbers(int[] boardnums) {
		for (int k = 0; k < 5; k++) {
			for (int i = 0; i < 5; i++) {
				System.out.print(boardnums[5 * k + i] + "\t");
			}

			System.out.println("");

		}
	}

	public static void printBoard(char[] board) {
		for (int k = 0; k < 5; k++) {
			for (int i = 0; i < 5; i++) {
				System.out.print(board[5 * k + i] + "\t");
			}

			System.out.println("");

		}
	}

	public boolean isPalindrome (String str) {
	      if (str == null) {
	         return false;
	      }
	      
	      str.toLowerCase();
	      
	      String newString = str;
	      for (int i = 0; i < newString.length(); i++) {
	         int value = (int) newString.charAt(i);
	         if (value >= 97 && value <= 122) {
	            newString = newString.substring(0, i) + newString.substring(i, newString.length());
	         }
	      }
	      
	      int i = 0;
	      int j = newString.length() - 1; 
	      while (i < j) {
	         if (newString.charAt(i) != newString.charAt(j)) {
	            return false;
	         }
	         
	         i++;
	         j--;
	         
	      }

	      return true;
	      
	   }

}
