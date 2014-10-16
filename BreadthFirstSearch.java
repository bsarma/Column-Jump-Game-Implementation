// Implementation of Breadth First Search for Column Jump



import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BreadthFirstSearch {
	public static void main(String[] args) throws Exception 

	{
		String str;
		int count = 0;
		int board_size = 0;
		int row = 0;
		String message = null;

		List<String> zero_positions = new ArrayList<String>();
		HashMap<String, Integer> eligible_moves = new HashMap<String, Integer>();
		HashMap<int[][], Integer> parentid_hash = new HashMap<int[][], Integer>();
		HashMap<Integer, Integer> parentid_childid_hash = new HashMap<Integer, Integer>();
		HashMap<Integer, String> movehash = new HashMap<Integer, String>();
		List<Integer> final_path = new ArrayList<Integer>();

		BufferedReader in = new BufferedReader(new FileReader("moves1.txt")); // read input file 

		while ((str = in.readLine()) != null)
		{
			count++;
			if(count == 1)
			{
				board_size = Integer.parseInt(str); //store board dimension
			}
			else
				break;
		}

		int[][] board = new int[board_size][board_size]; // initialize board matrix

		// store input in board matrix
		while ((str = in.readLine()) != null)
		{
			count++;
			if(count>2)
			{
				String[] temp = str.split(" ");
				for (int column = 0; column < board_size; column++ )
				{
					board[row][column] = Integer.parseInt(temp[column]);
				}
				row++;
			}
		}

		// implementation of BFS
		Queue queue = new LinkedList(); // initialize queue
		queue.add(board); // push initial board configuration into queue
		parentid_hash.put(board, 1); // parent id hash
		int lcounter = 1;
		int node_counter = 0;

		while(!queue.isEmpty()) 
		{ 
			int[][] board_child = (int[][]) queue.remove(); // pop child
			int parent_id = parentid_hash.get(board_child);
			
			if (Is_goal_state(board_child, board_size)) // check if popped child is goal state
			{
				for (int i = 0; i < board_size; i++) 
				{          
					for (int j = 0; j < board_size; j++) 
					{
						System.out.print(board_child[i][j] + " ");
					}
					System.out.print("\n");
				}

				lcounter++;
				parentid_childid_hash.put(lcounter, parent_id);
				break;
			}

			node_counter++;
			
			eligible_moves.clear(); // clear eligible moves hash
			zero_positions.clear(); // clear zero position array

			zero_positions = Count_zeroes(board_child, board_size); // get zero positions
			eligible_moves = Move_calculator(zero_positions, board_child, board_size); // get eligible moves
			
			for(String key:eligible_moves.keySet()) // loop through each move
			{
				String[] temp = key.split(" ");

				String[] from = temp[0].split(",");
				int from_row = Integer.parseInt(from[0]);
				int from_column = Integer.parseInt(from[1]);

				String[] to = temp[1].split(",");
				int to_row = Integer.parseInt(to[0]);
				int to_column = Integer.parseInt(to[1]);

				board = Move_player(board_child, from_row, from_column, to_row, to_column, board_size); // move player and return updated board
				
				if(board == board_child) // checks for unsolvable games
				{
					message = "No more moves!";
				}
				
				queue.add(board);
				movehash.put(lcounter, key);

				lcounter++;
				parentid_hash.put(board,lcounter);
				parentid_childid_hash.put(lcounter, parent_id);
			}
			
			if(message == "No more moves!")
			{
				System.out.println("Can't solve!");
				break;
			}
		}
		
		// backtracking to obtain the path from goal state to start node 
		int parentid = parentid_childid_hash.get(parentid_childid_hash.size()+1);
		final_path.add(parentid);
		while(parentid != 1)
		{
			parentid = parentid_childid_hash.get(parentid);
			final_path.add(parentid);
		}

		System.out.println("\n" + "Total no. of expanded nodes: " + node_counter + "\n");
			
		// print the final moves
		for (int f = final_path.size() - 1; f >= 0; f--)
		{
			if (parentid_hash.containsValue(final_path.get(f)))
			{
				System.out.println(movehash.get(final_path.get(f))); // prints the moves in reverse order
			}
		}
		
	}




	// Function to find zero positions in the board
	// Input: board matrix & dimension
	// Returns list of zero positions
	public static List<String> Count_zeroes(int[][] board, int board_size) 
	{
		List<String> zero = new ArrayList<String>();
		for (int i = 0; i < board_size; i++) 
		{          
			for (int j = 0; j < board_size; j++) 
			{
				if (board[i][j] == 0)
				{
					zero.add(i + " " + j);
				}
			}
		}
		return zero;
	}


	// Function to look for eligible moves
	// Input: list of all zeroes, board matrix & dimension
	// Returns hashmap of all possible moves
	public static HashMap<String, Integer> Move_calculator(List<String> zero_positions, int[][] board, int board_size)
	{

		HashMap<String, Integer> moves = new HashMap<String, Integer>();
		for (int k = 0; k < zero_positions.size(); k++) // looping on all zero positions
		{
			String[] temp = (zero_positions.get(k)).split(" ");
			int zero_row = Integer.parseInt(temp[0]);
			int zero_column = Integer.parseInt(temp[1]);
			int jump = 1;

			// look up
			for (int i = 1; i < board_size; i++) 
			{
				if (((zero_row - i) < 0) || ((zero_row - i -1) < 0) || (board[zero_row-i][zero_column] == 0) || (board[zero_row-i-1][zero_column] == 0))
					break;


				else if (board[zero_row - i - 1][zero_column] !=  board[zero_row - i][zero_column])
				{
					moves.put((zero_row - i - 1) + "," + zero_column + " " + zero_row + "," + zero_column, jump);
					break;
				}
				else
				{
					jump++;
				}
			} // end of look up

			jump = 1;
			// look down
			for (int i = 1; i < board_size; i++) 
			{
				if (((zero_row + i) > board_size-1) || (board[zero_row + i][zero_column] == 0) || ((zero_row + i + 1) > board_size - 1) || (board[zero_row + i + 1][zero_column] == 0))
					break;


				else if (board[zero_row + i + 1][zero_column] !=board[zero_row + i][zero_column])
				{
					moves.put((zero_row + i + 1) + "," + zero_column + " " + zero_row + "," + zero_column, jump);
					break;
				}
				else
				{
					jump++;
				}
			} // end of look down

			jump = 1;
			// look right
			for (int i = 1; i < board_size; i++) 
			{
				if (((zero_column + i) > board_size - 1) || (board[zero_row][zero_column + i] == 0) || ((zero_column + i + 1) > board_size - 1) || (board[zero_row][zero_column + i + 1] == 0))
					break;

				else if (board[zero_row][zero_column + i + 1] != board[zero_row][zero_column + i])
				{
					moves.put(zero_row + "," + (zero_column + i + 1) + " " + zero_row + "," + zero_column, jump);
					break;
				}
				else
				{
					jump++;
				}
			} // end of look right

			jump = 1;
			// look left
			for (int i = 1; i < board_size; i++) 
			{
				if (((zero_column - i) < 0) || (board[zero_row][zero_column - i] == 0) || ((zero_column - i - 1) < 0) || (board[zero_row][zero_column - i - 1] == 0))
					break;


				else if (board[zero_row][zero_column - i - 1] !=board[zero_row][zero_column - i])
				{
					moves.put(zero_row + "," + (zero_column - i - 1) + " " + zero_row + "," + zero_column, jump);
					break;
				}
				else
				{
					jump++;
				}
			} // end of look left
		}
		return moves;
	}


	// Function for deep cloning of the original board
	// Input: original board matrix & dimension
	// Stores original board matrix so that the value is not overwritten
	public static int[][] Deep_cloner(int[][] board, int board_size)
	{
		int[][] new_board = new int[board_size][board_size];
		for (int i = 0; i < board_size; i++) 
		{          
			for (int j = 0; j < board_size; j++) 
			{
				new_board[i][j] = board[i][j];
			}
		}
		return new_board;
	}


	// Function to move player and update board
	// Input: original board matrix & dimension, from row & column values, to row & column values
	// Returns updated board matrix
	public static int[][] Move_player(int[][] currboard, int from_row, int from_column, int to_row, int to_column, int board_size) 
	{
		int[][] board = Deep_cloner(currboard, board_size);
		if((from_column == to_column) && (from_row < to_row)) //move down
		{
			board[to_row][to_column] = board[from_row][from_column];
			board[from_row][from_column] = 0;
			int dist = to_row - from_row;
			for(int i = 1; i < dist; i++)
			{
				//if((from_row + i) > board_size || (from_row + i) < 0)
				//break;
				board[from_row + i][from_column] = 0;
			}
		}

		if((from_column == to_column) && (from_row > to_row)) //move up
		{
			board[to_row][to_column] = board[from_row][from_column];
			board[from_row][from_column] = 0;
			int dist = from_row - to_row;
			for(int i = 1; i < dist; i++)
			{
				//if((from_row - i) > board_size || (from_row - i) < 0)
				//break;
				board[from_row - i][from_column] = 0;
			}
		}

		if((from_row == to_row) && (from_column > to_column)) //move left
		{
			board[to_row][to_column] = board[from_row][from_column];
			board[from_row][from_column] = 0;
			int dist = from_column - to_column;
			for(int i = 1; i < dist; i++)
			{
				//if((from_column - i) > board_size || (from_column - i) < 0)
				//break;
				board[from_row][from_column - i] = 0;
			}
		}

		if((from_row == to_row) && (from_column < to_column)) //move right
		{
			board[to_row][to_column] = board[from_row][from_column];
			board[from_row][from_column] = 0;
			int dist = to_column - from_column;
			for(int i = 1; i < dist; i++)
			{
				//if((from_column + i) > board_size || (from_column + i) < 0)
				//break;
				board[from_row][from_column + i] = 0;
			}
		}

		return board;
	}


	// Function to check goal state
	// Input: updated board & dimension
	// Returns true if updated board is goal state
	public static boolean Is_goal_state(int[][] board, int board_size)
	{
		boolean result = false;
		int counter = 0;
		for (int i = 0; i < board_size; i++) 
		{          
			for (int j = 0; j < board_size; j++) 
			{
				if(board[i][j] != 0)
				{
					counter++;
				}
			}
		}
		if(counter == 1)
		{
			result = true;
		}
		counter = 0;
		return result;
	}
}



